package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IngestionState
import com.example.data.TrackEntity
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen

@Composable
fun AutoDownloadBanner(
    ingestionState: IngestionState,
    onPlayTrack: ((TrackEntity) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = ingestionState !is IngestionState.Idle,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        when (ingestionState) {
            is IngestionState.Ingesting -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    ElevatedGrey,
                                    PitchBlack
                                )
                            )
                        )
                        .border(1.dp, SpotifyGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                        .testTag("auto_download_banner")
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(SpotifyGreen.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = { ingestionState.progress },
                                    modifier = Modifier.size(24.dp),
                                    color = SpotifyGreen,
                                    strokeWidth = 3.dp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Fetching audio...",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = SpotifyGreen
                                )
                                Text(
                                    text = ingestionState.step,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = CrispWhite
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.CloudDownload,
                                contentDescription = "Downloading",
                                tint = SpotifyGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        LinearProgressIndicator(
                            progress = { ingestionState.progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = SpotifyGreen,
                            trackColor = ElevatedGrey
                        )

                        // Command Preview Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(PitchBlack)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "$ ${ingestionState.command}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp
                                ),
                                color = MutedGrey,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            is IngestionState.Success -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElevatedGrey)
                        .border(1.dp, SpotifyGreen, RoundedCornerShape(12.dp))
                        .clickable { onPlayTrack?.invoke(ingestionState.track) }
                        .padding(14.dp)
                        .testTag("ingestion_success_banner")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = SpotifyGreen,
                            modifier = Modifier.size(28.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Track Ingested & Added to Library!",
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispWhite
                            )
                            Text(
                                text = "${ingestionState.track.title} • ${ingestionState.track.artist}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MutedGrey
                            )
                        }

                        // Play Now Button
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SpotifyGreen)
                                .clickable { onPlayTrack?.invoke(ingestionState.track) }
                                .testTag("ingestion_play_now_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = androidx.compose.material.icons.Icons.Default.PlayArrow,
                                contentDescription = "Play Now",
                                tint = PitchBlack,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            is IngestionState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(ElevatedGrey)
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = ingestionState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite
                        )
                    }
                }
            }

            else -> {}
        }
    }
}
