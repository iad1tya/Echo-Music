package aditya.echo.aiservice


class AiClient {
    private var aiService: AiService? = null
    var host = AIHost.GEMINI
        set(value) {
            field = value
            apiKey?.let {
                aiService =
                    AiService(
                        aiHost = value,
                        apiKey = it,
                    )
            }
        }
    var apiKey: String? = null
        set(value) {
            field = value
            aiService =
                if (value != null) {
                    AiService(
                        aiHost = host,
                        apiKey = value,
                    )
                } else {
                    null
                }
        }
    var customModelId: String? = null
        set(value) {
            field = value
            aiService =
                if (apiKey != null) {
                    AiService(
                        aiHost = host,
                        apiKey = apiKey!!,
                        customModelId = value,
                    )
                } else {
                    null
                }
        }

}