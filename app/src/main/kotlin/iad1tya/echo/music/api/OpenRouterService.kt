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
        model: String,
        sourceLanguage: String? = null
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val prompt = "Translate the following lyrics to $targetLanguage. Return ONLY the translated text, preserving the line breaks and structure. Do not add any introduction or explanation.\n\n$text"
            
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are a helpful assistant that translates lyrics accurately while preserving meaning and structure.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            }

            val jsonBody = JSONObject().apply {
                put("model", model.ifBlank { "google/gemini-flash-1.5" })
                put("messages", messages)
            }

            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${apiKey.trim()}")
                .addHeader("Content-Type", "application/json")
                .addHeader("HTTP-Referer", "https://github.com/iad1tya/Echo-Music") // Required by OpenRouter
                .addHeader("X-Title", "Echo Music") // Required by OpenRouter
                .post(jsonBody.toString().toRequestBody(JSON))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful) {
                var errorMsg = try {
                    JSONObject(responseBody ?: "").optJSONObject("error")?.optString("message") 
                        ?: "HTTP ${response.code}: ${response.message}"
                } catch (e: Exception) {
                    "HTTP ${response.code}: ${response.message}"
                }

                if (errorMsg.contains("data policy", ignoreCase = true) || errorMsg.contains("retention", ignoreCase = true)) {
                    errorMsg = "Policy Error: Models may not match your 'Zero Retention' setting. Disable it in OpenRouter or try a different model."
                } else if (errorMsg.contains("provider", ignoreCase = true)) {
                    errorMsg = "$errorMsg. Try a different model"
                }

                return@withContext Result.failure(Exception("Translation failed: $errorMsg"))
            }

            if (responseBody == null) {
                return@withContext Result.failure(Exception("Empty response body"))
            }

            val jsonResponse = JSONObject(responseBody)
            val choices = jsonResponse.optJSONArray("choices")
            if (choices != null && choices.length() > 0) {
                val message = choices.getJSONObject(0).optJSONObject("message")
                val content = message?.optString("content")?.trim()
                if (content.isNullOrBlank()) {
                     return@withContext Result.failure(Exception("Empty translation content"))
                }
                return@withContext Result.success(content)
            }
            
            return@withContext Result.failure(Exception("No choices in response"))
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure(e)
        }
    }
}
