package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.audio.PlaybackState
import com.example.audio.RepeatMode
import com.example.data.TrackEntity
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.HighlightGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun BottomPlayerDock(
    playbackState: PlaybackState,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onToggleLike: (TrackEntity) -> Unit,
    onQueueClick: () -> Unit,
    onOpenFullScreen: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val currentTrack = playbackState.currentTrack

    val playButtonScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1f else 0.94f,
        animationSpec = spring(dampingRatio = 0.55f, stiffness = 550f),
        label = "dock_play_scale"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceCharcoal)
            .testTag("bottom_player_dock")
    ) {
        val isCompact = maxWidth < 600.dp

        if (isCompact) {
            // MOBILE COMPACT MINI-PLAYER
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (onOpenFullScreen != null && currentTrack != null) {
                            Modifier.clickable { onOpenFullScreen() }
                        } else Modifier
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left: 44dp cover art + Title + Artist
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TrackCoverArtThumb(
                            coverDrawable = currentTrack?.coverDrawable,
                            modifier = Modifier.size(44.dp)
                        )

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentTrack?.title ?: "Select a song to play",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = CrispWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("dock_song_title")
                            )
                            Text(
                                text = currentTrack?.artist ?: "Spotifly Library",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                color = MutedGrey,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("dock_artist_name")
                            )
                        }
                    }

                    // Right: Like + Previous + Play/Pause + Next
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (currentTrack != null) {
                            AnimatedLikeButton(
                                isLiked = currentTrack.isLiked,
                                onToggleLike = { onToggleLike(currentTrack) },
                                iconSize = 20.dp,
                                testTag = "dock_like_button",
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        IconButton(
                            onClick = onSkipPrevious,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("dock_prev_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = CrispWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .graphicsLayer {
                                    scaleX = playButtonScale
                                    scaleY = playButtonScale
                                }
                                .clip(CircleShape)
                                .background(SpotifyGreen)
                                .clickable { onPlayPauseToggle() }
                                .testTag("dock_play_pause_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            if (playbackState.isLoading) {
                                CircularProgressIndicator(
                                    color = PitchBlack,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                AnimatedContent(
                                    targetState = playbackState.isPlaying,
                                    transitionSpec = {
                                        (scaleIn(animationSpec = tween(120)) + fadeIn()) togetherWith
                                                (scaleOut(animationSpec = tween(120)) + fadeOut())
                                    },
                                    label = "dock_play_icon"
                                ) { isPlaying ->
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = PitchBlack,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            onClick = onSkipNext,
                            modifier = Modifier
                                .size(34.dp)
                                .testTag("dock_next_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = CrispWhite,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Sleek animated mini progress line across bottom edge
                val duration = playbackState.durationSeconds.coerceAtLeast(1)
                val rawProgress = (playbackState.currentPositionSeconds.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
                val animatedProgress by animateFloatAsState(
                    targetValue = rawProgress,
                    animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
                    label = "mini_progress"
                )

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .testTag("dock_seek_slider"),
                    color = SpotifyGreen,
                    trackColor = HighlightGrey
                )
            }
        } else {
            // DESKTOP / TABLET EXPANDED DOCK
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // LEFT SECTION: Cover Art, Song Name, Artist Name, Heart Like button
                    Row(
                        modifier = Modifier
                            .weight(1.2f)
                            .clip(RoundedCornerShape(8.dp))
                            .then(
                                if (onOpenFullScreen != null && currentTrack != null) {
                                    Modifier.clickable { onOpenFullScreen() }
                                } else Modifier
                            )
                            .padding(end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TrackCoverArtThumb(
                            coverDrawable = currentTrack?.coverDrawable,
                            modifier = Modifier.size(56.dp)
                        )

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = currentTrack?.title ?: "Select a song to play",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp
                                ),
                                color = CrispWhite,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("dock_song_title")
                            )
                            Text(
                                text = currentTrack?.artist ?: "Spotifly Library",
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.testTag("dock_artist_name")
                            )
                        }

                        if (currentTrack != null) {
                            AnimatedLikeButton(
                                isLiked = currentTrack.isLiked,
                                onToggleLike = { onToggleLike(currentTrack) },
                                iconSize = 20.dp,
                                testTag = "dock_like_button",
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // CENTER SECTION: Media Controls & Progress Seek Bar
                    Column(
                        modifier = Modifier
                            .weight(2f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            IconButton(
                                onClick = onToggleShuffle,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dock_shuffle_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shuffle,
                                    contentDescription = "Shuffle",
                                    tint = if (playbackState.isShuffle) SpotifyGreen else MutedGrey,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            IconButton(
                                onClick = onSkipPrevious,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dock_prev_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous",
                                    tint = CrispWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .graphicsLayer {
                                        scaleX = playButtonScale
                                        scaleY = playButtonScale
                                    }
                                    .clip(CircleShape)
                                    .background(SpotifyGreen)
                                    .clickable { onPlayPauseToggle() }
                                    .testTag("dock_play_pause_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (playbackState.isLoading) {
                                    CircularProgressIndicator(
                                        color = PitchBlack,
                                        strokeWidth = 2.dp,
                                        modifier = Modifier.size(20.dp)
                                    )
                                } else {
                                    AnimatedContent(
                                        targetState = playbackState.isPlaying,
                                        transitionSpec = {
                                            (scaleIn(animationSpec = tween(120)) + fadeIn()) togetherWith
                                                    (scaleOut(animationSpec = tween(120)) + fadeOut())
                                        },
                                        label = "desktop_dock_play_icon"
                                    ) { isPlaying ->
                                        Icon(
                                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isPlaying) "Pause" else "Play",
                                            tint = PitchBlack,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }

                            IconButton(
                                onClick = onSkipNext,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dock_next_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next",
                                    tint = CrispWhite,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            IconButton(
                                onClick = onToggleRepeat,
                                modifier = Modifier
                                    .size(32.dp)
                                    .testTag("dock_repeat_button")
                            ) {
                                Icon(
                                    imageVector = if (playbackState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                    contentDescription = "Repeat",
                                    tint = if (playbackState.repeatMode != RepeatMode.OFF) SpotifyGreen else MutedGrey,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Seek bar with current and total time
                        var isDragging by remember { mutableStateOf(false) }
                        var dragPosition by remember { mutableFloatStateOf(0f) }

                        val currentPos = if (isDragging) dragPosition.toInt() else playbackState.currentPositionSeconds
                        val duration = playbackState.durationSeconds.coerceAtLeast(1)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = formatTime(currentPos),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey,
                                modifier = Modifier.testTag("dock_current_time")
                            )

                            SpotifySlider(
                                value = currentPos.toFloat(),
                                onValueChange = {
                                    isDragging = true
                                    dragPosition = it
                                },
                                onValueChangeFinished = {
                                    isDragging = false
                                    onSeekTo(dragPosition.toInt())
                                },
                                valueRange = 0f..duration.toFloat(),
                                thumbColor = CrispWhite,
                                activeTrackColor = SpotifyGreen,
                                inactiveTrackColor = ElevatedGrey,
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("dock_seek_slider")
                            )

                            Text(
                                text = formatTime(duration),
                                style = MaterialTheme.typography.labelSmall,
                                color = MutedGrey,
                                modifier = Modifier.testTag("dock_total_time")
                            )
                        }
                    }

                    // RIGHT SECTION: Mute/Volume slider & Queue button
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End
                    ) {
                        IconButton(
                            onClick = onQueueClick,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dock_queue_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = "Queue",
                                tint = MutedGrey,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onToggleMute,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("dock_volume_icon")
                        ) {
                            val volIcon = when {
                                playbackState.isMuted || playbackState.volume == 0f -> Icons.Default.VolumeOff
                                playbackState.volume < 0.4f -> Icons.Default.VolumeDown
                                else -> Icons.Default.VolumeUp
                            }
                            Icon(
                                imageVector = volIcon,
                                contentDescription = "Volume",
                                tint = MutedGrey,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        SpotifySlider(
                            value = if (playbackState.isMuted) 0f else playbackState.volume,
                            onValueChange = { onVolumeChange(it) },
                            valueRange = 0f..1f,
                            thumbColor = CrispWhite,
                            activeTrackColor = CrispWhite,
                            inactiveTrackColor = ElevatedGrey,
                            modifier = Modifier
                                .width(90.dp)
                                .testTag("dock_volume_slider")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TrackCoverArtThumb(
    coverDrawable: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(ElevatedGrey),
        contentAlignment = Alignment.Center
    ) {
        if (!coverDrawable.isNullOrBlank() && (coverDrawable.startsWith("http://") || coverDrawable.startsWith("https://"))) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(coverDrawable)
                    .crossfade(300)
                    .build(),
                contentDescription = "Cover",
                modifier = Modifier.matchParentSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            when (coverDrawable) {
                "synthwave" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_cover_synthwave),
                        contentDescription = "Cover",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                "lofi" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_cover_lofi),
                        contentDescription = "Cover",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                "app_icon" -> {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Cover",
                        modifier = Modifier.matchParentSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(
                                Brush.linearGradient(listOf(HighlightGrey, PitchBlack))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%d:%02d", mins, secs)
}

