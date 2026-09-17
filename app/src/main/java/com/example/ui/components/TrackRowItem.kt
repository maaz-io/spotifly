package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TrackEntity
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SpotifyGreen

@Composable
fun TrackRowItem(
    index: Int?,
    track: TrackEntity,
    isPlaying: Boolean,
    isCurrentTrack: Boolean,
    onClick: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleDownload: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isCurrentTrack) ElevatedGrey.copy(alpha = 0.55f) else Color.Transparent,
        animationSpec = tween(durationMillis = 220),
        label = "row_bg_color"
    )

    val titleColor by animateColorAsState(
        targetValue = if (isCurrentTrack) SpotifyGreen else CrispWhite,
        animationSpec = tween(durationMillis = 200),
        label = "row_title_color"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("track_row_${track.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Track index or live animated equalizer indicator
        if (index != null) {
            Box(
                modifier = Modifier.width(24.dp),
                contentAlignment = Alignment.Center
            ) {
                if (isCurrentTrack) {
                    AnimatedEqualizer(
                        isPlaying = isPlaying,
                        color = SpotifyGreen,
                        size = 14.dp
                    )
                } else {
                    Text(
                        text = index.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 13.sp),
                        color = MutedGrey
                    )
                }
            }
        }

        // Cover thumbnail (44x44)
        TrackCoverArtThumb(
            coverDrawable = track.coverDrawable,
            modifier = Modifier.size(44.dp)
        )

        // Title and Artist / Album
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                ),
                color = titleColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${track.artist} • ${track.album}",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                color = MutedGrey,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Duration
        Text(
            text = formatTime(track.durationSeconds),
            style = MaterialTheme.typography.labelSmall,
            color = MutedGrey
        )

        // Download button (if callback provided)
        if (onToggleDownload != null) {
            IconButton(
                onClick = onToggleDownload,
                modifier = Modifier
                    .size(32.dp)
                    .testTag("track_download_btn_${track.id}")
            ) {
                Icon(
                    imageVector = if (track.isDownloaded) Icons.Filled.ArrowCircleDown else Icons.Outlined.ArrowCircleDown,
                    contentDescription = if (track.isDownloaded) "Downloaded" else "Download to Device",
                    tint = if (track.isDownloaded) SpotifyGreen else MutedGrey,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Like button with pop tactile animation
        AnimatedLikeButton(
            isLiked = track.isLiked,
            onToggleLike = onToggleLike,
            iconSize = 18.dp,
            testTag = "track_like_btn_${track.id}",
            modifier = Modifier.size(32.dp)
        )
    }
}
