package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlaylistEntity
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.HighlightGrey
import com.example.ui.theme.LikedSongsGradientEnd
import com.example.ui.theme.LikedSongsGradientStart
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun QuickAccessPlaylistCard(
    title: String,
    coverType: String,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(HighlightGrey.copy(alpha = 0.4f))
            .clickable { onClick() }
            .padding(end = 12.dp)
            .testTag("quick_access_card_$title"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (coverType == "liked") {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(LikedSongsGradientStart, LikedSongsGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = CrispWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    TrackCoverArtThumb(
                        coverDrawable = coverType,
                        modifier = Modifier.matchParentSize()
                    )
                }
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = CrispWhite,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Green circular play button
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(SpotifyGreen)
                .clickable { onPlayClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Play",
                tint = PitchBlack,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun FeaturedPlaylistCard(
    playlist: PlaylistEntity,
    onClick: () -> Unit,
    onPlayClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(160.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCharcoal)
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("featured_card_${playlist.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(6.dp))
        ) {
            TrackCoverArtThumb(
                coverDrawable = playlist.coverType,
                modifier = Modifier.matchParentSize()
            )

            // Green play button at bottom right
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SpotifyGreen)
                    .clickable { onPlayClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play Playlist",
                    tint = PitchBlack,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = playlist.name,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
            color = CrispWhite,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = playlist.description,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
            color = MutedGrey,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
