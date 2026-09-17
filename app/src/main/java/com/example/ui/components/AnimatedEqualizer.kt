package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.SpotifyGreen

/**
 * Premium Spotify-style live vertical equalizer indicator.
 * Displays 3 animated jumping bars when playing, rests when paused.
 */
@Composable
fun AnimatedEqualizer(
    isPlaying: Boolean,
    modifier: Modifier = Modifier,
    color: Color = SpotifyGreen,
    barCount: Int = 3,
    size: Dp = 16.dp
) {
    val infiniteTransition = rememberInfiniteTransition(label = "equalizer_bars")

    val bar1Fraction by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 0.95f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 480, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar1"
        )
    } else {
        remember { mutableFloatStateOf(0.3f) }
    }

    val bar2Fraction by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0.85f,
            targetValue = 0.20f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 380, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar2"
        )
    } else {
        remember { mutableFloatStateOf(0.5f) }
    }

    val bar3Fraction by if (isPlaying) {
        infiniteTransition.animateFloat(
            initialValue = 0.35f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 560, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "bar3"
        )
    } else {
        remember { mutableFloatStateOf(0.3f) }
    }

    Canvas(modifier = modifier.size(size)) {
        val totalWidth = this.size.width
        val totalHeight = this.size.height
        val barWidth = totalWidth / (barCount * 2 - 1)
        val cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)

        val fractions = listOf(bar1Fraction, bar2Fraction, bar3Fraction)

        for (i in 0 until barCount) {
            val fraction = fractions.getOrElse(i) { 0.4f }
            val currentBarHeight = (totalHeight * fraction).coerceAtLeast(barWidth)
            val left = i * (barWidth * 2)
            val top = totalHeight - currentBarHeight

            drawRoundRect(
                color = color,
                topLeft = Offset(left, top),
                size = Size(barWidth, currentBarHeight),
                cornerRadius = cornerRadius
            )
        }
    }
}

/**
 * Animated Like (Heart) button with tactile pop bounce on toggle.
 */
@Composable
fun AnimatedLikeButton(
    isLiked: Boolean,
    onToggleLike: () -> Unit,
    modifier: Modifier = Modifier,
    likedColor: Color = SpotifyGreen,
    unlikedColor: Color = MutedGrey,
    iconSize: Dp = 20.dp,
    testTag: String = "animated_like_button"
) {
    var scaleTarget by remember { mutableFloatStateOf(1f) }
    var previousLiked by remember { mutableStateOf(isLiked) }

    LaunchedEffect(isLiked) {
        // Only bounce on a genuine toggle, never on first composition/scroll recycling.
        if (isLiked != previousLiked) {
            scaleTarget = 1.35f
            kotlinx.coroutines.delay(120)
            scaleTarget = 1f
        }
        previousLiked = isLiked
    }

    val scale by animateFloatAsState(
        targetValue = scaleTarget,
        animationSpec = spring(dampingRatio = 0.45f, stiffness = 600f),
        label = "heart_bounce"
    )

    IconButton(
        onClick = onToggleLike,
        modifier = modifier
            .testTag(testTag)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isLiked) "Liked" else "Like",
            tint = if (isLiked) likedColor else unlikedColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
