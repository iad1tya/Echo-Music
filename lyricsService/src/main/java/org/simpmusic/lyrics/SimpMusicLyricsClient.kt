package aditya.echo.lyrics

import android.content.Context
import android.util.Log
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import aditya.echo.lyrics.models.request.LyricsBody
import aditya.echo.lyrics.models.request.TranslatedLyricsBody
import aditya.echo.lyrics.models.response.BaseResponse
import aditya.echo.lyrics.models.response.LrclibObject
import aditya.echo.lyrics.models.response.LyricsResponse
import aditya.echo.lyrics.models.response.TranslatedLyricsResponse
import aditya.echo.lyrics.parser.parseSyncedLyrics
import aditya.echo.lyrics.parser.parseUnsyncedLyrics
import kotlin.math.abs

class EchoLyricsClient(
    context: Context,
) {
    @Suppress("ktlint:standard:property-naming")
    private val TAG = "EchoLyricsClient"
    private val hmacService = HmacService()
    private val lyricsService = EchoLyrics(context)

    private var insertingLyrics: Pair<String?, Boolean> = (null to false)
    private val isInsertingLyrics: Boolean
        get() = insertingLyrics.second

    private var insertingTranslatedLyrics: Pair<String?, Boolean> = (null to false)
    private val isInsertingTranslatedLyrics: Boolean
        get() = insertingTranslatedLyrics.second

    suspend fun getLyrics(videoId: String): Result<List<LyricsResponse>> =
        runCatching {
            lyricsService.findLyricsByVideoId(videoId).bodyOrThrow<List<LyricsResponse>>()
        }

    suspend fun getTranslatedLyrics(
        videoId: String,
        language: String,
    ): Result<TranslatedLyricsResponse> =
        runCatching {
            if (language.length != 2) {
                throw IllegalArgumentException("Language code must be a 2-letter code")
            }
            lyricsService.findTranslatedLyrics(videoId, language).bodyOrThrow<TranslatedLyricsResponse>()
        }

    suspend fun insertLyrics(lyricsBody: LyricsBody): Result<LyricsResponse> =
        runCatching {
            if (isInsertingLyrics && insertingLyrics.first == lyricsBody.videoId) {
                throw IllegalStateException("Already inserting lyrics, please wait until the current operation is complete.")
            }
            insertingLyrics = lyricsBody.videoId to true
            val hmacTimestamp =
                hmacService.getMacTimestampPair(
                    HmacService.BASE_HMAC_URI,
                )
            lyricsService.insertLyrics(lyricsBody, hmacTimestamp).bodyOrThrow<LyricsResponse>()
        }

    suspend fun insertTranslatedLyrics(translatedLyricsBody: TranslatedLyricsBody): Result<TranslatedLyricsResponse> =
        runCatching {
            if (translatedLyricsBody.language.length != 2) {
                throw IllegalArgumentException("Language code must be a 2-letter code")
            }
            if (isInsertingTranslatedLyrics && insertingTranslatedLyrics.first == translatedLyricsBody.videoId) {
                throw IllegalStateException("Already inserting translated lyrics, please wait until the current operation is complete.")
            }
            insertingTranslatedLyrics = translatedLyricsBody.videoId to true
            val hmacTimestamp =
                hmacService.getMacTimestampPair(
                    HmacService.TRANSLATED_HMAC_URI,
                )
            lyricsService.insertTranslatedLyrics(translatedLyricsBody, hmacTimestamp).bodyOrThrow<TranslatedLyricsResponse>()
        }

    suspend fun voteLyrics(
        lyricsId: String,
        upvote: Boolean,
    ): Result<LyricsResponse> =
        runCatching {
            val hmacTimestamp =
                hmacService.getMacTimestampPair(
                    HmacService.VOTE_HMAC_URI,
                )
            lyricsService.voteLyrics(lyricsId, upvote, hmacTimestamp).bodyOrThrow<LyricsResponse>()
        }

    suspend fun voteTranslatedLyrics(
        translatedLyricsId: String,
        upvote: Boolean,
    ): Result<TranslatedLyricsResponse> =
        runCatching {
            val hmacTimestamp =
                hmacService.getMacTimestampPair(
                    HmacService.VOTE_TRANSLATED_HMAC_URI,
                )
            lyricsService.voteTranslatedLyrics(translatedLyricsId, upvote, hmacTimestamp).bodyOrThrow<TranslatedLyricsResponse>()
        }

    suspend fun searchLrclibLyrics(
        q_track: String,
        q_artist: String,
        duration: Int?,
    ) = runCatching {
        val rs =
            lyricsService
                .searchLrclibLyrics(
                    q_track = q_track,
                    q_artist = q_artist,
                ).body<List<LrclibObject>>()
        val lrclibObject: LrclibObject? =
            if (duration != null) {
                rs.find { abs(it.duration.toInt() - duration) <= 10 }
            } else {
                rs.firstOrNull()
            }
        if (lrclibObject != null) {
            val syncedLyrics = lrclibObject.syncedLyrics
            val plainLyrics = lrclibObject.plainLyrics
            if (!syncedLyrics.isNullOrEmpty()) {
                parseSyncedLyrics(syncedLyrics)
            } else if (!plainLyrics.isNullOrEmpty()) {
                parseUnsyncedLyrics(plainLyrics)
            } else {
                null
            }
        } else {
            null
        }
    }

    private suspend inline fun <reified T> HttpResponse.bodyOrThrow(): T {
        try {
            val data = body<BaseResponse<T>>()
            if (data.error != null) {
                val error = data.error
                Log.e(TAG, "Error response: ${error.reason} (code: ${error.code})")
                throw Exception("Error response: ${error.reason} (code: ${error.code})")
            }
            return data.data ?: throw Exception("Response data is null")
        } catch (e: Exception) {
            throw e
        }
    }
}