package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.ArrowCircleDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material.icons.outlined.ArrowCircleDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.PlaybackState
import com.example.audio.RepeatMode
import com.example.data.TrackEntity
import com.example.ui.components.AnimatedEqualizer
import com.example.ui.components.AnimatedLikeButton
import com.example.ui.components.SpotifySlider
import com.example.ui.components.TrackCoverArtThumb
import com.example.ui.components.formatTime
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.HighlightGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.PureBlack
import com.example.ui.theme.SpotifyGreen

/**
 * Full-Screen Media Player consistent with Spotify mobile & desktop UI experience.
 * Features:
 * - Fluid spring scale album art reaction on play/pause
 * - Live animated equalizer in header
 * - Smooth tacticle play/pause & like button animations
 * - Scrubbable seek bar with elapsed and remaining timestamps
 */
@Composable
fun FullScreenMediaPlayer(
    playbackState: PlaybackState,
    onCollapse: () -> Unit,
    onPlayPauseToggle: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Int) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleLike: (TrackEntity) -> Unit,
    onToggleDownload: (TrackEntity) -> Unit,
    onVolumeChange: (Float) -> Unit,
    onToggleMute: () -> Unit,
    onQueueClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val track = playbackState.currentTrack

    // Dynamic dark gradient background derived from Spotify dark themes
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF2E3842),
            Color(0xFF181E24),
            PitchBlack,
            PureBlack
        )
    )

    // Album art spring animation: breathes and expands on play, contracts slightly on pause
    val albumArtScale by animateFloatAsState(
        targetValue = if (playbackState.isPlaying) 1.0f else 0.88f,
        animationSpec = spring(dampingRatio = 0.72f, stiffness = 320f),
        label = "album_art_scale"
    )

    val albumArtElevation by animateDpAsState(
        targetValue = if (playbackState.isPlaying) 22.dp else 8.dp,
        animationSpec = spring(dampingRatio = 0.75f, stiffness = 320f),
        label = "album_art_elevation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(gradientBackground)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { /* Intercept background clicks */ }
            .testTag("full_screen_media_player")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. TOP BAR: Collapse button, Header + live equalizer, More options
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onCollapse,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("fullscreen_collapse_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse Player",
                        tint = CrispWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "PLAYING FROM SPOTIFLY",
                            style = MaterialTheme.typography.labelSmall.copy(
                                letterSpacing = 1.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            color = MutedGrey
                        )
                        AnimatedEqualizer(
                            isPlaying = playbackState.isPlaying,
                            color = SpotifyGreen,
                            size = 12.dp
                        )
                    }
                    Text(
                        text = track?.album ?: "Spotifly Master Collection",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = CrispWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { /* More options */ },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("fullscreen_more_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More Options",
                        tint = CrispWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. LARGE ALBUM ARTWORK WITH REACTIVE SCALE & ELEVATION
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .graphicsLayer {
                        scaleX = albumArtScale
                        scaleY = albumArtScale
                    }
                    .shadow(elevation = albumArtElevation, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElevatedGrey)
                    .testTag("fullscreen_album_art"),
                contentAlignment = Alignment.Center
            ) {
                TrackCoverArtThumb(
                    coverDrawable = track?.coverDrawable,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. TRACK TITLE, ARTIST, LIKE BUTTON & DOWNLOAD BUTTON
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = track?.title ?: "No Track Playing",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        ),
                        color = CrispWhite,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("fullscreen_track_title")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = track?.artist ?: "Select a song",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        color = MutedGrey,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.testTag("fullscreen_artist_name")
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Download Button
                    if (track != null) {
                        IconButton(
                            onClick = { onToggleDownload(track) },
                            modifier = Modifier
                                .size(44.dp)
                                .testTag("fullscreen_download_button")
                        ) {
                            Icon(
                                imageVector = if (track.isDownloaded) Icons.Filled.ArrowCircleDown else Icons.Outlined.ArrowCircleDown,
                                contentDescription = if (track.isDownloaded) "Downloaded" else "Download",
                                tint = if (track.isDownloaded) SpotifyGreen else MutedGrey,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    // Like (Heart) Button with tactile pop spring animation
                    if (track != null) {
                        AnimatedLikeButton(
                            isLiked = track.isLiked,
                            onToggleLike = { onToggleLike(track) },
                            iconSize = 28.dp,
                            testTag = "fullscreen_like_button",
                            modifier = Modifier.size(44.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4. PROGRESS SEEK BAR & TIMESTAMPS
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                var isDragging by remember { mutableStateOf(false) }
                var dragProgress by remember { mutableFloatStateOf(0f) }

                val duration = (track?.durationSeconds ?: 1).toFloat()
                val currentSec = playbackState.currentPositionSeconds.toFloat()
                val sliderValue = if (isDragging) dragProgress else (currentSec / duration).coerceIn(0f, 1f)

                SpotifySlider(
                    value = sliderValue,
                    onValueChange = {
                        isDragging = true
                        dragProgress = it
                    },
                    onValueChangeFinished = {
                        isDragging = false
                        val targetSeconds = (dragProgress * duration).toInt()
                        onSeekTo(targetSeconds)
                    },
                    valueRange = 0f..1f,
                    thumbColor = CrispWhite,
                    activeTrackColor = CrispWhite,
                    inactiveTrackColor = HighlightGrey,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fullscreen_seek_bar")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displaySec = if (isDragging) (dragProgress * duration).toInt() else playbackState.currentPositionSeconds
                    Text(
                        text = formatTime(displaySec),
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey
                    )
                    Text(
                        text = formatTime(track?.durationSeconds ?: 0),
                        style = MaterialTheme.typography.labelSmall,
                        color = MutedGrey
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 5. MAIN PLAYBACK CONTROLS (Shuffle, Prev, Big Animated Play/Pause, Next, Repeat)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Shuffle Button
                IconButton(
                    onClick = onToggleShuffle,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("fullscreen_shuffle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (playbackState.isShuffle) SpotifyGreen else MutedGrey,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous Button
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("fullscreen_prev_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous Track",
                        tint = CrispWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Center Circular Play/Pause Button (Spotify Green, large 64dp with spring scale)
                val playButtonScale by animateFloatAsState(
                    targetValue = if (playbackState.isPlaying) 1f else 0.94f,
                    animationSpec = spring(dampingRatio = 0.55f, stiffness = 550f),
                    label = "play_button_scale"
                )

                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .graphicsLayer {
                            scaleX = playButtonScale
                            scaleY = playButtonScale
                        }
                        .shadow(elevation = 8.dp, shape = CircleShape)
                        .clip(CircleShape)
                        .background(SpotifyGreen)
                        .clickable { onPlayPauseToggle() }
                        .testTag("fullscreen_play_pause_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (playbackState.isLoading) {
                        CircularProgressIndicator(
                            color = PitchBlack,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(30.dp)
                        )
                    } else {
                        AnimatedContent(
                            targetState = playbackState.isPlaying,
                            transitionSpec = {
                                (scaleIn(animationSpec = tween(150)) + fadeIn()) togetherWith
                                        (scaleOut(animationSpec = tween(150)) + fadeOut())
                            },
                            label = "play_pause_icon"
                        ) { isPlaying ->
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = PitchBlack,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }
                }

                // Next Button
                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier
                        .size(48.dp)
                        .testTag("fullscreen_next_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next Track",
                        tint = CrispWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat Button
                IconButton(
                    onClick = onToggleRepeat,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("fullscreen_repeat_button")
                ) {
                    Icon(
                        imageVector = if (playbackState.repeatMode == RepeatMode.ONE) Icons.Default.RepeatOne else Icons.Default.Repeat,
                        contentDescription = "Repeat Mode",
                        tint = if (playbackState.repeatMode != RepeatMode.OFF) SpotifyGreen else MutedGrey,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6. BOTTOM UTILITIES: Volume Slider and Queue Action
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Volume controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onToggleMute,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("fullscreen_volume_icon")
                    ) {
                        val volIcon = when {
                            playbackState.isMuted || playbackState.volume == 0f -> Icons.Default.VolumeOff
                            playbackState.volume < 0.4f -> Icons.Default.VolumeDown
                            else -> Icons.Default.VolumeUp
                        }
                        Icon(
                            imageVector = volIcon,
                            contentDescription = "Mute / Unmute",
                            tint = MutedGrey,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    SpotifySlider(
                        value = if (playbackState.isMuted) 0f else playbackState.volume,
                        onValueChange = { onVolumeChange(it) },
                        valueRange = 0f..1f,
                        thumbColor = CrispWhite,
                        activeTrackColor = SpotifyGreen,
                        inactiveTrackColor = HighlightGrey,
                        modifier = Modifier
                            .width(140.dp)
                            .testTag("fullscreen_volume_slider")
                    )
                }

                // Queue Button
                IconButton(
                    onClick = onQueueClick,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("fullscreen_queue_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Open Queue",
                        tint = CrispWhite,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    }
}
