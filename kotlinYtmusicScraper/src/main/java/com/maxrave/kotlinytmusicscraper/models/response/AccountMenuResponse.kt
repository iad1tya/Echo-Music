package iad1tya.echo.kotlinytmusicscraper.models.response

import iad1tya.echo.kotlinytmusicscraper.models.AccountInfo
import iad1tya.echo.kotlinytmusicscraper.models.Runs
import iad1tya.echo.kotlinytmusicscraper.models.Thumbnails
import kotlinx.serialization.Serializable

@Serializable
data class AccountMenuResponse(
    val actions: List<Action>,
) {
    @Serializable
    data class Action(
        val openPopupAction: OpenPopupAction,
    ) {
        @Serializable
        data class OpenPopupAction(
            val popup: Popup,
        ) {
            @Serializable
            data class Popup(
                val multiPageMenuRenderer: MultiPageMenuRenderer,
            ) {
                @Serializable
                data class MultiPageMenuRenderer(
                    val header: Header?,
                ) {
                    @Serializable
                    data class Header(
                        val activeAccountHeaderRenderer: ActiveAccountHeaderRenderer,
                    ) {
                        @Serializable
                        data class ActiveAccountHeaderRenderer(
                            val accountName: Runs,
                            val accountPhoto: Thumbnails,
                            val channelHandle: Runs,
                        ) {
                            fun toAccountInfo() =
                                AccountInfo(
                                    accountName.runs!!.first().text,
                                    channelHandle.runs!!.first().text,
                                    accountPhoto.thumbnails,
                                )
                        }
                    }
                }
            }
        }
    }
}