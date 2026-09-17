package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlaylistEntity
import com.example.data.TrackEntity
import com.example.ui.components.FeaturedPlaylistCard
import com.example.ui.components.QuickAccessPlaylistCard
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.PitchBlack
import java.util.Calendar

@Composable
fun HomeScreen(
    allTracks: List<TrackEntity>,
    playlists: List<PlaylistEntity>,
    likedTracksCount: Int,
    currentTrackId: Long?,
    isPlaying: Boolean,
    onTrackClick: (TrackEntity) -> Unit,
    onToggleLike: (TrackEntity) -> Unit,
    onToggleDownload: ((TrackEntity) -> Unit)? = null,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onLikedSongsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val greeting = rememberGreeting()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
            .testTag("home_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        // Greeting Header (e.g., "Good evening")
        item {
            Text(
                text = greeting,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = CrispWhite,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Quick Access Grid (2 columns)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickAccessPlaylistCard(
                        title = "Liked Songs",
                        coverType = "liked",
                        onClick = onLikedSongsClick,
                        onPlayClick = onLikedSongsClick,
                        modifier = Modifier.weight(1f)
                    )

                    val firstPl = playlists.firstOrNull()
                    if (firstPl != null) {
                        QuickAccessPlaylistCard(
                            title = firstPl.name,
                            coverType = firstPl.coverType,
                            onClick = { onPlaylistClick(firstPl) },
                            onPlayClick = { onPlaylistClick(firstPl) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (playlists.size >= 2) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickAccessPlaylistCard(
                            title = playlists[1].name,
                            coverType = playlists[1].coverType,
                            onClick = { onPlaylistClick(playlists[1]) },
                            onPlayClick = { onPlaylistClick(playlists[1]) },
                            modifier = Modifier.weight(1f)
                        )

                        if (playlists.size >= 3) {
                            QuickAccessPlaylistCard(
                                title = playlists[2].name,
                                coverType = playlists[2].coverType,
                                onClick = { onPlaylistClick(playlists[2]) },
                                onPlayClick = { onPlaylistClick(playlists[2]) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        // Section: Featured Playlists
        item {
            Text(
                text = "Featured Playlists",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = CrispWhite,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(playlists, key = { it.id }) { playlist ->
                    FeaturedPlaylistCard(
                        playlist = playlist,
                        onClick = { onPlaylistClick(playlist) },
                        onPlayClick = { onPlaylistClick(playlist) },
                        modifier = Modifier.animateItem()
                    )
                }
            }
        }

        // Section: Recently Played & Popular Tracks
        item {
            Text(
                text = "Popular Tracks in Library",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = CrispWhite,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        itemsIndexed(allTracks, key = { _, track -> track.id }) { index, track ->
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

private fun rememberGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Good afternoon"
        else -> "Good evening"
    }
}
