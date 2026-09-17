package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.PlaylistEntity
import com.example.data.TrackEntity
import com.example.ui.components.TrackCoverArtThumb
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.LikedSongsGradientEnd
import com.example.ui.theme.LikedSongsGradientStart
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun LibraryScreen(
    playlists: List<PlaylistEntity>,
    likedTracks: List<TrackEntity>,
    allTracks: List<TrackEntity>,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onLikedSongsClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("Playlists") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp)
            .testTag("library_screen"),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Create button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Your Library",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = CrispWhite
                )

                IconButton(
                    onClick = onCreatePlaylistClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ElevatedGrey)
                        .testTag("library_create_playlist_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Playlist",
                        tint = CrispWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Filter chips (Playlists, Liked, Ingested)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Playlists", "Liked Songs", "Ingested Files").forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(text = filter, color = if (selectedFilter == filter) PitchBlack else CrispWhite) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SpotifyGreen,
                            containerColor = ElevatedGrey
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }
        }

        // Liked Songs card (shown if filter is Playlists or Liked Songs)
        if (selectedFilter != "Ingested Files") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCharcoal)
                        .clickable { onLikedSongsClick() }
                        .padding(12.dp)
                        .testTag("library_liked_songs_card"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(LikedSongsGradientStart, LikedSongsGradientEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Liked Songs",
                            tint = CrispWhite,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Liked Songs",
                            style = MaterialTheme.typography.titleMedium,
                            color = CrispWhite
                        )
                        Text(
                            text = "Playlist • ${likedTracks.size} songs",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MutedGrey
                        )
                    }
                }
            }
        }

        // Playlists list
        if (selectedFilter == "Playlists" || selectedFilter == "All") {
            items(playlists, key = { it.id }) { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SurfaceCharcoal)
                        .clickable { onPlaylistClick(playlist) }
                        .padding(12.dp)
                        .testTag("library_playlist_${playlist.id}"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElevatedGrey),
                        contentAlignment = Alignment.Center
                    ) {
                        TrackCoverArtThumb(
                            coverDrawable = playlist.coverType,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    Column {
                        Text(
                            text = playlist.name,
                            style = MaterialTheme.typography.titleMedium,
                            color = CrispWhite
                        )
                        Text(
                            text = "Playlist • ${playlist.getTrackIds().size} songs",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                            color = MutedGrey
                        )
                    }
                }
            }
        }

        // Ingested Files filter
        if (selectedFilter == "Ingested Files") {
            val ingestedTracks = allTracks.filter { it.source == "ingested_ytdlp" }
            if (ingestedTracks.isEmpty()) {
                item {
                    Text(
                        text = "No ingested audio tracks yet. Use Search to query and auto-download new songs via the yt-dlp pipeline!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MutedGrey,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                items(ingestedTracks, key = { it.id }) { track ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateItem()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceCharcoal)
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        TrackCoverArtThumb(
                            coverDrawable = track.coverDrawable,
                            modifier = Modifier.size(54.dp)
                        )
                        Column {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = CrispWhite
                            )
                            Text(
                                text = "${track.artist} • ${track.filePath}",
                                style = MaterialTheme.typography.labelSmall,
                                color = SpotifyGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
