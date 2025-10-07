package iad1tya.echo.music.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import iad1tya.echo.music.extension.shimmer
import iad1tya.echo.music.ui.theme.shimmerBackground

@Composable
fun HomeItemShimmer() {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Section title skeleton
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(28.dp)
                .padding(vertical = 8.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
        
        // Horizontal items skeleton
        LazyRow(
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(4) {
                HomeItemContentSkeleton()
            }
        }
    }
}

@Composable
fun HomeItemContentSkeleton() {
    Column(
        modifier = Modifier.width(160.dp)
    ) {
        // Album/Playlist art skeleton
        Box(
            modifier = Modifier
                .size(160.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(12.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Title skeleton
        Box(
            modifier = Modifier
                .width(140.dp)
                .height(18.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(6.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(6.dp))
        
        // Subtitle skeleton
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(16.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(6.dp))
                .shimmer()
        )
    }
}

@Preview(
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun PreviewShimmer() {
    HomeShimmer()
}

@Composable
fun PlaylistShimmer() {
    Column(
        Modifier
            .height(270.dp)
            .padding(10.dp),
    ) {
        Box(
            Modifier
                .size(160.dp)
                .clip(
                    RoundedCornerShape(10),
                ).background(
                    color = shimmerBackground,
                ).shimmer(),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            Modifier
                .width(130.dp)
                .height(18.dp)
                .clip(
                    RoundedCornerShape(10),
                ).background(
                    color = shimmerBackground,
                ).shimmer(),
        )
        Spacer(modifier = Modifier.size(10.dp))
        Box(
            Modifier
                .width(130.dp)
                .height(18.dp)
                .clip(
                    RoundedCornerShape(10),
                ).background(
                    color = shimmerBackground,
                ).shimmer(),
        )
    }
}

@Composable
fun QuickPicksShimmerItem() {
    Row(
        modifier = Modifier
            .height(72.dp)
            .padding(vertical = 8.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Album art skeleton
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Text content skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Song title skeleton
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(18.dp)
                    .background(shimmerBackground)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmer()
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            // Artist name skeleton
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(16.dp)
                    .background(shimmerBackground)
                    .clip(RoundedCornerShape(6.dp))
                    .shimmer()
            )
        }
        
        // Duration skeleton
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(14.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
    }
}

@Composable
fun QuickPicksShimmer() {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Title skeleton
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(24.dp)
                .padding(vertical = 8.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(6.dp))
                .shimmer()
        )
        
        // Quick picks items skeleton
        LazyColumn(
            userScrollEnabled = false,
        ) {
            items(4) {
                QuickPicksShimmerItem()
            }
        }
    }
}

@Composable
fun HomeShimmer() {
    Column(
        Modifier.padding(horizontal = 15.dp),
    ) {
        // Recently Played Skeleton
        RecentlyPlayedSkeleton()
        
        // Quick Picks Skeleton
        QuickPicksShimmer()
        
        // Home Items Skeleton
        LazyColumn(
            userScrollEnabled = false,
        ) {
            items(8) {
                HomeItemShimmer()
            }
        }
    }
}

@Composable
fun RecentlyPlayedSkeleton() {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        // Title skeleton
        Box(
            modifier = Modifier
                .width(150.dp)
                .height(24.dp)
                .padding(vertical = 8.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(6.dp))
                .shimmer()
        )
        
        // Recently played items skeleton
        LazyRow(
            userScrollEnabled = false,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(5) {
                RecentlyPlayedItemSkeleton()
            }
        }
    }
}

@Composable
fun RecentlyPlayedItemSkeleton() {
    Column(
        modifier = Modifier.width(120.dp)
    ) {
        // Album art skeleton
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Title skeleton
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(16.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Artist skeleton
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(14.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
    }
}

@Composable
fun ShimmerSearchItem() {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 16.dp)
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail shimmer
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(shimmerBackground)
                .shimmer()
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Text content shimmer
        Column {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBackground)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBackground)
                    .shimmer()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(shimmerBackground)
                    .shimmer()
            )
        }
    }
}

@Composable
fun ChartSkeleton() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        // Chart title skeleton
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(28.dp)
                .padding(vertical = 8.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(8.dp))
                .shimmer()
        )
        
        // Chart items skeleton
        LazyColumn(
            userScrollEnabled = false,
        ) {
            items(10) {
                ChartItemSkeleton()
            }
        }
    }
}

@Composable
fun ChartItemSkeleton() {
    Row(
        modifier = Modifier
            .height(64.dp)
            .padding(vertical = 6.dp, horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank number skeleton
        Box(
            modifier = Modifier
                .width(24.dp)
                .height(20.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Album art skeleton
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(6.dp))
                .shimmer()
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Song info skeleton
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            // Song title skeleton
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(16.dp)
                    .background(shimmerBackground)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // Artist name skeleton
            Box(
                modifier = Modifier
                    .width(120.dp)
                    .height(14.dp)
                    .background(shimmerBackground)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmer()
            )
        }
        
        // Duration skeleton
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(14.dp)
                .background(shimmerBackground)
                .clip(RoundedCornerShape(4.dp))
                .shimmer()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChartSkeletonPreview() {
    ChartSkeleton()
}
