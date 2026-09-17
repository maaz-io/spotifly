package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.IngestionState
import com.example.data.TrackEntity
import com.example.ui.components.AutoDownloadBanner
import com.example.ui.components.TrackCoverArtThumb
import com.example.ui.components.TrackRowItem
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.HighlightGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen

data class BrowseCategory(
    val title: String,
    val searchQuery: String,
    val color: Color
)

@Composable
fun SearchScreen(
    searchQuery: String,
    hasSearched: Boolean,
    localResults: List<TrackEntity>,
    onlineResults: List<TrackEntity>,
    isSearchingOnline: Boolean,
    ingestionState: IngestionState,
    currentTrackId: Long?,
    isPlaying: Boolean,
    isLoading: Boolean,
    onTrackClick: (TrackEntity, List<TrackEntity>) -> Unit,
    onToggleLike: (TrackEntity) -> Unit,
    onToggleDownload: (TrackEntity) -> Unit,
    onGenreClick: (String) -> Unit,
    onTriggerIngest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val browseCategories = remember {
        listOf(
            BrowseCategory("Bollywood & Desi", "Bollywood Hits", Color(0xFFFF5722)),
            BrowseCategory("Today's Top Hits", "Top Hits", SpotifyGreen),
            BrowseCategory("Synthwave & Retro", "Synthwave", Color(0xFFE91E63)),
            BrowseCategory("Lo-Fi & Study Beats", "Lo-Fi Beats", Color(0xFF3F51B5)),
            BrowseCategory("Rock & Alternative", "Rock Hits", Color(0xFFD84315)),
            BrowseCategory("Hip-Hop & Rap", "Hip-Hop", Color(0xFF7B1FA2)),
            BrowseCategory("Electronic & Dance", "Electronic Dance", Color(0xFF00ACC1)),
            BrowseCategory("Acoustic Chill", "Acoustic Pop", Color(0xFF43A047))
        )
    }

    // Combine online real-time tracks with local matching tracks seamlessly
    val combinedResults = remember(onlineResults, localResults) {
        val seen = mutableSetOf<String>()
        val list = mutableListOf<TrackEntity>()
        for (track in onlineResults) {
            val key = "${track.title.lowercase().trim()}_${track.artist.lowercase().trim()}"
            if (seen.add(key)) list.add(track)
        }
        for (track in localResults) {
            val key = "${track.title.lowercase().trim()}_${track.artist.lowercase().trim()}"
            if (seen.add(key)) list.add(track)
        }
        list
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(PitchBlack)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (hasSearched && searchQuery.isNotBlank()) {
            // Background yt-dlp ingestion pipeline feedback (fetching / success / error)
            item {
                AutoDownloadBanner(
                    ingestionState = ingestionState,
                    onPlayTrack = { track -> onTrackClick(track, combinedResults) }
                )
            }

            // Live Search Online Status Bar
            if (isSearchingOnline) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElevatedGrey.copy(alpha = 0.6f))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(
                            color = SpotifyGreen,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Searching online for \"$searchQuery\"...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = CrispWhite
                        )
                    }
                }
            }

            if (combinedResults.isNotEmpty()) {
                val topTrack = combinedResults.first()
                val isTopTrackPlaying = isPlaying && currentTrackId == topTrack.id
                val isTopTrackLoading = isLoading && currentTrackId == topTrack.id

                // 1. TOP RESULT CARD (Spotify Desktop/Mobile style)
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Top Result",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            ),
                            color = CrispWhite
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(ElevatedGrey)
                                .clickable { onTrackClick(topTrack, combinedResults) }
                                .padding(16.dp)
                                .testTag("search_top_result_card")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    TrackCoverArtThumb(
                                        coverDrawable = topTrack.coverDrawable,
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Column(
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = topTrack.title,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 18.sp
                                            ),
                                            color = if (isTopTrackPlaying) SpotifyGreen else CrispWhite,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = topTrack.artist,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MutedGrey,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )

                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(HighlightGrey)
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "SONG • REAL STREAM",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp
                                                ),
                                                color = SpotifyGreen
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                // Play Button with Loading state
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(CircleShape)
                                        .background(SpotifyGreen)
                                        .clickable { onTrackClick(topTrack, combinedResults) }
                                        .testTag("top_result_play_btn"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isTopTrackLoading) {
                                        CircularProgressIndicator(
                                            color = PitchBlack,
                                            strokeWidth = 2.5.dp,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    } else {
                                        Icon(
                                            imageVector = if (isTopTrackPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                            contentDescription = if (isTopTrackPlaying) "Pause" else "Play",
                                            tint = PitchBlack,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. SONGS HEADER & REAL TRACKS LIST
                item {
                    Text(
                        text = "Songs (${combinedResults.size})",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = CrispWhite,
                        modifier = Modifier.testTag("search_results_header")
                    )
                }

                itemsIndexed(combinedResults, key = { _, track -> track.id }) { index, track ->
                    val isTrackPlaying = isPlaying && currentTrackId == track.id
                    TrackRowItem(
                        index = index + 1,
                        track = track,
                        isPlaying = isTrackPlaying,
                        isCurrentTrack = currentTrackId == track.id,
                        onClick = { onTrackClick(track, combinedResults) },
                        onToggleLike = { onToggleLike(track) },
                        onToggleDownload = { onToggleDownload(track) },
                        modifier = Modifier.animateItem()
                    )
                }
            } else if (!isSearchingOnline) {
                // No Results Found State
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(ElevatedGrey.copy(alpha = 0.5f))
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = SpotifyGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No songs found for \"$searchQuery\"",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = CrispWhite
                        )
                        Text(
                            text = "Check spelling or search for another artist, song, or genre like Arijit Singh, The Weeknd, Ed Sheeran, or Lo-Fi.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MutedGrey,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }
                }
            }
        } else {
            // DEFAULT BROWSE VIEW WHEN SEARCH IS EMPTY
            item {
                Text(
                    text = "Browse Categories",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = CrispWhite,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    browseCategories.chunked(2).forEach { rowCategories ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            rowCategories.forEach { category ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(100.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(category.color)
                                        .clickable { onGenreClick(category.searchQuery) }
                                        .padding(14.dp)
                                        .testTag("browse_category_${category.title}")
                                ) {
                                    Text(
                                        text = category.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        ),
                                        color = CrispWhite,
                                        modifier = Modifier.align(Alignment.TopStart)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
