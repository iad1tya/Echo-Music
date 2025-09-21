package iad1tya.echo.kotlinytmusicscraper.pages

import iad1tya.echo.kotlinytmusicscraper.models.Album
import iad1tya.echo.kotlinytmusicscraper.models.AlbumItem
import iad1tya.echo.kotlinytmusicscraper.models.Artist
import iad1tya.echo.kotlinytmusicscraper.models.ArtistItem
import iad1tya.echo.kotlinytmusicscraper.models.MusicResponsiveListItemRenderer
import iad1tya.echo.kotlinytmusicscraper.models.PlaylistItem
import iad1tya.echo.kotlinytmusicscraper.models.SongItem
import iad1tya.echo.kotlinytmusicscraper.models.Thumbnails
import iad1tya.echo.kotlinytmusicscraper.models.YTItem
import iad1tya.echo.kotlinytmusicscraper.models.oddElements
import iad1tya.echo.kotlinytmusicscraper.models.splitBySeparator
import iad1tya.echo.kotlinytmusicscraper.utils.parseTime

data class SearchResult(
    val items: List<YTItem>,
    val listPodcast: List<PodcastItem>,
    val continuation: String? = null,
)

object SearchPage {
    fun toPodcast(renderer: MusicResponsiveListItemRenderer?): PodcastItem? {
        return if (renderer == null) {
            null
        } else {
            PodcastItem(
                id = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                title =
                    renderer.flexColumns
                        .firstOrNull()
                        ?.musicResponsiveListItemFlexColumnRenderer
                        ?.text
                        ?.runs
                        ?.firstOrNull()
                        ?.text
                        ?: return null,
                author =
                    Artist(
                        name =
                            renderer.flexColumns
                                .lastOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text
                                ?: return null,
                        id =
                            renderer.flexColumns
                                .lastOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.navigationEndpoint
                                ?.browseEndpoint
                                ?.browseId
                                ?: return null,
                    ),
                thumbnail = renderer.thumbnail?.musicThumbnailRenderer?.thumbnail ?: return null,
            )
        }
    }

    fun toYTItem(renderer: MusicResponsiveListItemRenderer?): YTItem? {
        if (renderer == null) {
            return null
        } else {
            val secondaryLine =
                renderer.flexColumns
                    .getOrNull(1)
                    ?.musicResponsiveListItemFlexColumnRenderer
                    ?.text
                    ?.runs
                    ?.splitBySeparator()
                    ?: return null
            return when {
                renderer.isSong -> {
                    SongItem(
                        id = renderer.playlistItemData?.videoId ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        artists =
                            secondaryLine.firstOrNull()?.oddElements()?.map {
                                Artist(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId,
                                )
                            } ?: return null,
                        album =
                            secondaryLine
                                .getOrNull(1)
                                ?.firstOrNull()
                                ?.takeIf { it.navigationEndpoint?.browseEndpoint != null }
                                ?.let {
                                    Album(
                                        name = it.text,
                                        id = it.navigationEndpoint?.browseEndpoint?.browseId!!,
                                    )
                                },
                        duration =
                            secondaryLine
                                .lastOrNull()
                                ?.firstOrNull()
                                ?.text
                                ?.parseTime(),
                        thumbnail =
                            renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                                ?: return null,
                        explicit =
                            renderer.badges?.find {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } != null,
                        thumbnails = renderer.thumbnail.musicThumbnailRenderer.thumbnail,
                    )
                }

                renderer.isArtist -> {
                    ArtistItem(
                        id = renderer.navigationEndpoint?.browseEndpoint?.browseId ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text
                                ?: return null,
                        thumbnail =
                            renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                                ?: return null,
                        shuffleEndpoint =
                            renderer.menu
                                ?.menuRenderer
                                ?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?: return null,
                        radioEndpoint =
                            renderer.menu.menuRenderer.items
                                .find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?: return null,
                    )
                }

                renderer.isAlbum -> {
                    AlbumItem(
                        browseId =
                            renderer.navigationEndpoint?.browseEndpoint?.browseId
                                ?: return null,
                        playlistId =
                            renderer.overlay
                                ?.musicItemThumbnailOverlayRenderer
                                ?.content
                                ?.musicPlayButtonRenderer
                                ?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?.playlistId
                                ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        artists =
                            secondaryLine.getOrNull(1)?.oddElements()?.map {
                                Artist(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId,
                                )
                            } ?: return null,
                        year =
                            secondaryLine
                                .getOrNull(2)
                                ?.firstOrNull()
                                ?.text
                                ?.toIntOrNull(),
                        thumbnail =
                            renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                                ?: return null,
                        explicit =
                            renderer.badges?.find {
                                it.musicInlineBadgeRenderer?.icon?.iconType == "MUSIC_EXPLICIT_BADGE"
                            } != null,
                    )
                }

                renderer.isPlaylist -> {
                    PlaylistItem(
                        id =
                            renderer.navigationEndpoint
                                ?.browseEndpoint
                                ?.browseId
                                ?.removePrefix("VL")
                                ?: return null,
                        title =
                            renderer.flexColumns
                                .firstOrNull()
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.firstOrNull()
                                ?.text ?: return null,
                        author =
                            secondaryLine.firstOrNull()?.firstOrNull()?.let {
                                Artist(
                                    name = it.text,
                                    id = it.navigationEndpoint?.browseEndpoint?.browseId,
                                )
                            } ?: return null,
                        songCountText =
                            renderer.flexColumns
                                .getOrNull(1)
                                ?.musicResponsiveListItemFlexColumnRenderer
                                ?.text
                                ?.runs
                                ?.lastOrNull()
                                ?.text ?: return null,
                        thumbnail =
                            renderer.thumbnail?.musicThumbnailRenderer?.getThumbnailUrl()
                                ?: return null,
                        playEndpoint =
                            renderer.overlay
                                ?.musicItemThumbnailOverlayRenderer
                                ?.content
                                ?.musicPlayButtonRenderer
                                ?.playNavigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?: return null,
                        shuffleEndpoint =
                            renderer.menu
                                ?.menuRenderer
                                ?.items
                                ?.find { it.menuNavigationItemRenderer?.icon?.iconType == "MUSIC_SHUFFLE" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?: return null,
                        radioEndpoint =
                            renderer.menu.menuRenderer.items
                                .find { it.menuNavigationItemRenderer?.icon?.iconType == "MIX" }
                                ?.menuNavigationItemRenderer
                                ?.navigationEndpoint
                                ?.watchPlaylistEndpoint
                                ?: return null,
                    )
                }

                else -> null
            }
        }
    }
}

data class PodcastItem(
    val id: String,
    val title: String,
    val author: Artist,
    val thumbnail: Thumbnails,
)