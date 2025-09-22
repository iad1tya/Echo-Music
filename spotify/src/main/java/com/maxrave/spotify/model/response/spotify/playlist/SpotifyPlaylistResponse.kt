package aditya.echo.spotify.model.response.spotify.playlist

import kotlinx.serialization.Serializable

@Serializable
data class SpotifyPlaylistResponse(
    val id: String,
    val name: String,
    val description: String? = null,
    val images: List<Image>? = null,
    val owner: Owner,
    val tracks: Tracks,
    val external_urls: ExternalUrls,
    val public: Boolean? = null,
    val collaborative: Boolean? = null,
) {
    @Serializable
    data class Image(
        val url: String,
        val height: Int? = null,
        val width: Int? = null,
    )

    @Serializable
    data class Owner(
        val id: String,
        val display_name: String? = null,
        val external_urls: ExternalUrls,
    )

    @Serializable
    data class Tracks(
        val href: String,
        val total: Int,
        val items: List<TrackItem>? = null,
    ) {
        @Serializable
        data class TrackItem(
            val track: Track? = null,
        ) {
            @Serializable
            data class Track(
                val id: String,
                val name: String,
                val artists: List<Artist>,
                val album: Album,
                val duration_ms: Int,
                val external_urls: ExternalUrls,
                val preview_url: String? = null,
            ) {
                @Serializable
                data class Artist(
                    val id: String,
                    val name: String,
                    val external_urls: ExternalUrls,
                )

                @Serializable
                data class Album(
                    val id: String,
                    val name: String,
                    val images: List<Image>,
                    val external_urls: ExternalUrls,
                )
            }
        }
    }

    @Serializable
    data class ExternalUrls(
        val spotify: String,
    )
}

