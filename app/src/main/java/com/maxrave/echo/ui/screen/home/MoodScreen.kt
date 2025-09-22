package iad1tya.echo.music.ui.screen.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import iad1tya.echo.music.R
import iad1tya.echo.music.ui.component.EndOfPage
import iad1tya.echo.music.ui.component.MoodAndGenresContentItem
import iad1tya.echo.music.ui.component.NormalAppBar
import iad1tya.echo.music.viewModel.MoodViewModel

@Composable
@UnstableApi
fun MoodScreen(
    navController: NavController,
    viewModel: MoodViewModel = viewModel(),
    params: String?,
) {
    val moodData by viewModel.moodsMomentObject.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = params) {
        if (params != null) {
            viewModel.getMood(params)
        }
    }

    Column {
        NormalAppBar(
            title = {
                Text(text = moodData?.header ?: "")
            },
            leftIcon = {
                IconButton(onClick = { navController.navigateUp() }) {
                    Icon(
                        painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back",
                    )
                }
            },
        )
        AnimatedVisibility(visible = !loading) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
            ) {
                items(moodData?.items ?: emptyList()) { item ->
                    MoodAndGenresContentItem(
                        data = item,
                        navController = navController,
                    )
                }
                item {
                    EndOfPage()
                }
            }
        }
        AnimatedVisibility(visible = loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}