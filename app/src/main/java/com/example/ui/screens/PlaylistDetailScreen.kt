package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TrackEntity
import com.example.ui.components.TrackCoverArtThumb
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DividerGrey
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.LikedSongsGradientEnd
import com.example.ui.theme.LikedSongsGradientStart
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun PlaylistDetailScreen(
    title: String,
    description: String,
    coverType: String,
    tracks: List<TrackEntity>,
    currentTrackId: Long?,
    isPlaying: Boolean,
    onBackClick: () -> Unit,
    onTrackClick: (TrackEntity) -> Unit,
    onPlayAllClick: () -> Unit,
    onToggleLike: (TrackEntity) -> Unit,
    onToggleDownload: ((TrackEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val totalDurationSeconds = tracks.sumOf { it.durationSeconds }
    val totalMinutes = totalDurationSeconds / 60

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
            .testTag("playlist_detail_screen"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Back Button
        item {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(ElevatedGrey)
                    .testTag("playlist_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = CrispWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Header Section
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Large Cover Art (140x140)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElevatedGrey),
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
                                modifier = Modifier.size(54.dp)
                            )
                        }
                    } else {
                        TrackCoverArtThumb(
                            coverDrawable = coverType,
                            modifier = Modifier.matchParentSize()
                        )
                    }
                }

                // Playlist metadata
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "PLAYLIST",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = MutedGrey
                    )

                    // Track Title (Main Display): 24px / Bold
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = CrispWhite
                    )

                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                        color = MutedGrey,
                        maxLines = 2
                    )

                    Text(
                        text = "Spotifly • ${tracks.size} songs, ~${totalMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = CrispWhite
                    )
                }
            }
        }

        // Action Buttons Row: Green Play Button, Shuffle, Options
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Big Spotify Green Play Circle
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(SpotifyGreen)
                        .clickable { onPlayAllClick() }
                        .testTag("playlist_play_circle_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPlaying && tracks.any { it.id == currentTrackId }) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play All",
                        tint = PitchBlack,
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(
                    onClick = onPlayAllClick,
                    modifier = Modifier.testTag("playlist_shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = MutedGrey,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = MutedGrey
                    )
                }
            }
        }

        // Detailed Track Table Header
        item {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey,
                        modifier = Modifier.width(28.dp)
                    )
                    Text(
                        text = "TITLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "DURATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey,
                        modifier = Modifier.padding(end = 40.dp)
                    )
                }
                HorizontalDivider(color = DividerGrey, thickness = 1.dp)
            }
        }

        // Detailed Track List
        if (tracks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No songs in this playlist yet.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey
                    )
                }
            }
        } else {
            itemsIndexed(tracks, key = { _, track -> track.id }) { index, track ->
                TrackRowItem(
                    index = index + 1,
                    track = track,
                    isPlaying = isPlaying && currentTrackId == track.id,
                    isCurrentTrack = currentTrackId == track.id,
                    onClick = { onTrackClick(track) },
                    onToggleLike = { onToggleLike(track) },
                    onToggleDownload = if (onToggleDownload != null) { { onToggleDownload(track) } } else null,
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}
