package iad1tya.echo.music.ui.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.window.DialogProperties
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.theme.seed
import iad1tya.echo.music.ui.theme.typo

@Composable
@ExperimentalMaterial3Api
fun ReviewDialog(
    onDismissRequest: () -> Unit,
    onDoneReview: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    AlertDialog(
        properties =
            DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
            ),
        onDismissRequest = {
            onDismissRequest.invoke()
        },
        confirmButton = {
            TextButton(onClick = {
                onDoneReview.invoke()
                uriHandler.openUri("https://github.com/iad1tya/Echo-Music")
            }) {
                Text(
                    stringResource(R.string.give_a_star),
                    style = typo.bodySmall,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismissRequest.invoke()
            }) {
                Text(
                    stringResource(id = R.string.later),
                    style = typo.bodySmall,
                )
            }
        },
        icon = {
            Icon(painterResource(R.drawable.mono), "App Icon")
        },
        title = {
            Text(
                stringResource(R.string.enjoying_echo),
                style = typo.labelSmall,
            )
        },
        text = {
            Text(
                buildAnnotatedString {
                    append(stringResource(R.string.if_you_enjoy_using_echo_star_echo_on_github_or_leave_a_review_on))
                    withLink(
                        LinkAnnotation.Url(
                            "https://www.producthunt.com/products/echo",
                            TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline, color = seed)),
                        ) {
                            onDoneReview.invoke()
                            onDismissRequest.invoke()
                            uriHandler.openUri("https://www.producthunt.com/products/echo")
                        },
                    ) {
                        append(" ProductHunt")
                    }
                    append("\n")
                    append(stringResource(R.string.if_you_love_my_work_consider))
                    withLink(
                        LinkAnnotation.Url(
                            "https://buymeacoffee.com/iad1tya",
                            TextLinkStyles(style = SpanStyle(textDecoration = TextDecoration.Underline, color = seed)),
                        ) {
                            onDoneReview.invoke()
                            onDismissRequest.invoke()
                            uriHandler.openUri("https://buymeacoffee.com/iad1tya")
                        },
                    ) {
                        append(stringResource(R.string.buying_me_a_coffee))
                    }
                },
                textAlign = TextAlign.Center,
                style = typo.bodySmall,
            )
        },
    )
}