package aditya.echo.aiservice

import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.JsonSchema
import com.aallam.openai.api.chat.chatCompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIHost.Companion.Gemini
import com.aallam.openai.client.OpenAIHost
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.add
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject

class AiService(
    private val aiHost: AIHost = AIHost.GEMINI,
    private val apiKey: String,
    private val customModelId: String? = null,
) {
    private val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    private val openAI: OpenAI by lazy {
        when (aiHost) {
            AIHost.GEMINI -> OpenAI(host = Gemini, token = apiKey)
            AIHost.OPENAI -> OpenAI(token = apiKey)
            AIHost.OPENROUTER -> OpenAI(host = OpenAIHost("https://openrouter.ai/api/v1"), token = apiKey)
        }
    }

    private val model by lazy {
        if (!customModelId.isNullOrEmpty()) {
            ModelId(customModelId)
        } else {
            when (aiHost) {
                AIHost.GEMINI -> ModelId("gemini-2.0-flash")
                AIHost.OPENAI -> ModelId("gpt-4o")
                AIHost.OPENROUTER -> ModelId("anthropic/claude-3.5-sonnet")
            }
        }
    }


    companion object {
        private val translationJsonSchema: JsonObject =
            buildJsonObject {
                put("type", "object")
                putJsonObject("properties") {
                    putJsonObject("lyrics") {
                        put("type", "object")
                        putJsonObject("properties") {
                            putJsonObject("lines") {
                                put("type", "array")
                                putJsonObject("items") {
                                    put("type", "object")
                                    putJsonObject("properties") {
                                        putJsonObject("startTimeMs") {
                                            put("type", "string")
                                        }
                                        putJsonObject("endTimeMs") {
                                            put("type", "string")
                                        }
                                        putJsonObject("syllables") {
                                            put("type", "array")
                                            putJsonObject("items") {
                                                put("type", "string")
                                            }
                                        }
                                        putJsonObject("words") {
                                            put("type", "string")
                                        }
                                    }
                                    putJsonArray("required") {
                                        add("startTimeMs")
                                        add("endTimeMs")
                                        add("words")
                                        // `syllables` is optional if it's nullable
                                    }
                                }
                            }
                            putJsonObject("syncType") {
                                put("type", "string")
                            }
                        }
                        putJsonArray("required") {
                            add("lines")
                            add("syncType")
                        }
                    }
                }
                putJsonArray("required") {
                    add("lyrics")
                }
            }
        private val aiResponseJsonSchema =
            JsonSchema(
                name = "ai_translation_schema", // Give your schema a name
                schema = translationJsonSchema,
                strict = true, // Recommended for better adherence
            )
    }
}

enum class AIHost {
    GEMINI,
    OPENAI,
    OPENROUTER,
}