package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.TrackEntity
import com.example.ui.components.TrackCoverArtThumb
import com.example.ui.components.formatTime
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.DividerGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun QueueDialog(
    currentTrack: TrackEntity?,
    queue: List<TrackEntity>,
    currentIndex: Int,
    onTrackClick: (TrackEntity) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.8f)
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCharcoal)
                .padding(20.dp)
                .testTag("queue_dialog"),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Play Queue",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = CrispWhite
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_queue_dialog_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MutedGrey
                    )
                }
            }

            // Now Playing Section
            if (currentTrack != null) {
                Text(
                    text = "NOW PLAYING",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = MutedGrey
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(PitchBlack)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TrackCoverArtThumb(
                        coverDrawable = currentTrack.coverDrawable,
                        modifier = Modifier.size(48.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTrack.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = SpotifyGreen,
                            maxLines = 1
                        )
                        Text(
                            text = currentTrack.artist,
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = formatTime(currentTrack.durationSeconds),
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey
                    )
                }
            }

            HorizontalDivider(color = DividerGrey, thickness = 1.dp)

            Text(
                text = "NEXT IN QUEUE",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = MutedGrey
            )

            val upcoming = if (currentIndex + 1 < queue.size) {
                queue.subList(currentIndex + 1, queue.size)
            } else {
                emptyList()
            }

            if (upcoming.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Queue is empty. Select any song or playlist to play next.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(upcoming, key = { _, track -> track.id }) { idx, track ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { onTrackClick(track) }
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "${idx + 1}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey
                            )

                            TrackCoverArtThumb(
                                coverDrawable = track.coverDrawable,
                                modifier = Modifier.size(40.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = track.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                                    color = CrispWhite,
                                    maxLines = 1
                                )
                                Text(
                                    text = track.artist,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey,
                                    maxLines = 1
                                )
                            }

                            Text(
                                text = formatTime(track.durationSeconds),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey
                            )
                        }
                    }
                }
            }
        }
    }
}
