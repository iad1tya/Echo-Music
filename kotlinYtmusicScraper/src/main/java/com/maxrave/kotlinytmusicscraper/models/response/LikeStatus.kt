package iad1tya.echo.kotlinytmusicscraper.models.response

enum class LikeStatus {
    LIKE,
    DISLIKE,
    INDIFFERENT,
}

fun String?.toLikeStatus(): LikeStatus =
    when (this) {
        "LIKE" -> LikeStatus.LIKE
        "DISLIKE" -> LikeStatus.DISLIKE
        else -> LikeStatus.INDIFFERENT
    }