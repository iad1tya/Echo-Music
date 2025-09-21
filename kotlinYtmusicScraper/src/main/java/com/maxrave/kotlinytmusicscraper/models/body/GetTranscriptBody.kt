package iad1tya.echo.kotlinytmusicscraper.models.body

import iad1tya.echo.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)