package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Minimal Spotify-style slider: a thin pill track with a small dot thumb that
 * grows slightly on press/drag, with no Material ripple halo around it.
 * Delegates all gesture handling to Material3's Slider via its thumb/track slots.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpotifySlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    activeTrackColor: Color,
    inactiveTrackColor: Color,
    thumbColor: Color,
    modifier: Modifier = Modifier,
    onValueChangeFinished: (() -> Unit)? = null,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    trackHeight: Dp = 4.dp,
    thumbSize: Dp = 12.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val isDragged by interactionSource.collectIsDraggedAsState()

    val animatedThumbSize by animateDpAsState(
        targetValue = if (isPressed || isDragged) thumbSize * 1.35f else thumbSize,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 500f),
        label = "spotify_slider_thumb_scale"
    )

    Slider(
        value = value,
        onValueChange = onValueChange,
        onValueChangeFinished = onValueChangeFinished,
        valueRange = valueRange,
        interactionSource = interactionSource,
        modifier = modifier.height(24.dp),
        thumb = {
            Box(
                modifier = Modifier
                    .size(animatedThumbSize)
                    .background(thumbColor, CircleShape)
            )
        },
        track = { sliderState ->
            val span = (valueRange.endInclusive - valueRange.start).let { if (it == 0f) 1f else it }
            val fraction = ((sliderState.value - valueRange.start) / span).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeight)
                    .clip(CircleShape)
                    .background(inactiveTrackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .height(trackHeight)
                        .clip(CircleShape)
                        .background(activeTrackColor)
                )
            }
        }
    )
}
