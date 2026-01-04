package iad1tya.echo.music.api

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

import java.util.concurrent.TimeUnit

object OpenRouterService {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    private val JSON = "application/json; charset=utf-8".toMediaType()

    suspend fun translate(
        text: String,
        targetLanguage: String,
        apiKey: String,
        baseUrl: String,
        model: String,
        mode: String,
        maxRetries: Int = 3,
        sourceLanguage: String? = null
    ): Result<List<String>> = withContext(Dispatchers.IO) {
        var currentAttempt = 0
        
        while (currentAttempt < maxRetries) {
            try {
                // Request JSON array explicitly
                val jsonInstruction = "Return ONLY a JSON array of strings. Do not provide any explanations, questions, or conversational text. If the target language code is unclear, map it to the closest match or default to English. Output only the JSON array."
                
                val prompt = if (mode == "Meaning") {
                    "Translate the following lyrics to $targetLanguage, conveying the deeper meaning and context. You can rephrase to capture the essence and emotion of the song. Preserve the number of lines mostly but focus on meaning. $jsonInstruction\n\nInput Lyrics:\n$text"
                } else {
                    "Translate the following lyrics to $targetLanguage literally. Provide a direct, word-for-word translation where possible, maintaining the original structure. Do not romanize unless the target language script requires it. Preserve the number of lines and structure exactly. $jsonInstruction\n\nInput Lyrics:\n$text"
                }
                
                val messages = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "system")
                        put("content", "You are a helpful assistant that translates lyrics. You MUST return ONLY a valid JSON array of strings. Do not output anything else. Do not ask questions.")
                    })
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", prompt)
                    })
                }

                val jsonBody = JSONObject().apply {
                    if (model.isNotBlank()) {
                        put("model", model)
                    }
                    put("messages", messages)
                    // Removed response_format to ensure compatibility with all models
                }

                val request = Request.Builder()
                    .url(baseUrl.ifBlank { "https://openrouter.ai/api/v1/chat/completions" })
                    .apply {
                        if (apiKey.isNotBlank()) {
                            addHeader("Authorization", "Bearer ${apiKey.trim()}")
                        }
                    }
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://github.com/iad1tya/Echo-Music")
                    .addHeader("X-Title", "Echo Music")
                    .post(jsonBody.toString().toRequestBody(JSON))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (!response.isSuccessful) {
                    // Retry on server errors (5xx)
                    if (response.code >= 500) {
                        currentAttempt++
                        kotlinx.coroutines.delay(1000L * currentAttempt)
                        continue
                    }
                    
                    var errorMsg = try {
                        JSONObject(responseBody ?: "").optJSONObject("error")?.optString("message") 
                            ?: "HTTP ${response.code}: ${response.message}"
                    } catch (e: Exception) {
                        "HTTP ${response.code}: ${response.message}"
                    }
                    // For client errors (4xx), we fail immediately as retrying won't help
                    return@withContext Result.failure(Exception("Translation failed: $errorMsg"))
                }

                if (responseBody == null) {
                    currentAttempt++
                    continue
                }

                val jsonResponse = JSONObject(responseBody)
                val choices = jsonResponse.optJSONArray("choices")
                if (choices != null && choices.length() > 0) {
                    val message = choices.getJSONObject(0).optJSONObject("message")
                    val content = message?.optString("content")?.trim()
                    
                    if (!content.isNullOrBlank()) {
                        // Extract JSON array from content
                        val jsonString = content.substringAfter("[").substringBeforeLast("]")
                        val finalJson = "[$jsonString]"
                        
                        try {
                            val jsonArray = JSONArray(finalJson)
                            val translatedLines = mutableListOf<String>()
                            for (i in 0 until jsonArray.length()) {
                                translatedLines.add(jsonArray.optString(i))
                            }
                            return@withContext Result.success(translatedLines)
                        } catch (e: Exception) {
                             if (currentAttempt == maxRetries - 1) throw e
                        }
                    }
                }
            } catch (e: Exception) {
                if (currentAttempt == maxRetries - 1) {
                    return@withContext Result.failure(e)
                }
            }
            currentAttempt++
            kotlinx.coroutines.delay(1000L * currentAttempt)
        }
        return@withContext Result.failure(Exception("Max retries exceeded"))
    }
}
