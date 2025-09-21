package iad1tya.echo.kotlinytmusicscraper.models.body

import iad1tya.echo.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetSearchSuggestionsBody(
    val context: Context,
    val input: String,
)