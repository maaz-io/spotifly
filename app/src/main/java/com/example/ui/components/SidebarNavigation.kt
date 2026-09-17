package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.PlaylistEntity
import com.example.ui.SpotiflyNavTab
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
fun SidebarNavigation(
    selectedTab: SpotiflyNavTab,
    onTabSelected: (SpotiflyNavTab) -> Unit,
    playlists: List<PlaylistEntity>,
    likedSongsCount: Int,
    onPlaylistClick: (PlaylistEntity) -> Unit,
    onLikedSongsClick: () -> Unit,
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(PitchBlack)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Navigation Box (Home, Search, Library)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceCharcoal)
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Spotify Logo Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(SpotifyGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Spotifly Logo",
                            tint = PitchBlack,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "Spotifly",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = CrispWhite
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                SidebarNavItem(
                    label = stringResource(R.string.nav_home),
                    icon = if (selectedTab == SpotiflyNavTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    isSelected = selectedTab == SpotiflyNavTab.HOME,
                    onClick = { onTabSelected(SpotiflyNavTab.HOME) },
                    testTag = "sidebar_nav_home"
                )

                SidebarNavItem(
                    label = stringResource(R.string.nav_search),
                    icon = if (selectedTab == SpotiflyNavTab.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                    isSelected = selectedTab == SpotiflyNavTab.SEARCH,
                    onClick = { onTabSelected(SpotiflyNavTab.SEARCH) },
                    testTag = "sidebar_nav_search"
                )

                SidebarNavItem(
                    label = stringResource(R.string.nav_library),
                    icon = if (selectedTab == SpotiflyNavTab.LIBRARY) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                    isSelected = selectedTab == SpotiflyNavTab.LIBRARY,
                    onClick = { onTabSelected(SpotiflyNavTab.LIBRARY) },
                    testTag = "sidebar_nav_library"
                )
            }
        }

        // Library & Playlists Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceCharcoal)
                .padding(horizontal = 14.dp, vertical = 16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Header & Create Playlist button with '+' icon
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "YOUR LIBRARY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MutedGrey
                    )

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(ElevatedGrey)
                            .clickable { onCreatePlaylistClick() }
                            .testTag("create_playlist_plus_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Create Playlist",
                            tint = CrispWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Liked Songs card with a gradient heart icon
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { onLikedSongsClick() }
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                        .testTag("liked_songs_sidebar_card"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(4.dp))
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
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "Liked Songs",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 15.sp),
                            color = CrispWhite
                        )
                        Text(
                            text = "$likedSongsCount tracks",
                            style = MaterialTheme.typography.labelSmall,
                            color = MutedGrey
                        )
                    }
                }

                HorizontalDivider(color = DividerGrey, thickness = 1.dp)

                // Scrollable list of user-created playlists
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(playlists, key = { it.id }) { playlist ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .clip(RoundedCornerShape(4.dp))
                                .clickable { onPlaylistClick(playlist) }
                                .padding(vertical = 6.dp, horizontal = 4.dp)
                                .testTag("playlist_item_${playlist.id}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(ElevatedGrey),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MusicNote,
                                    contentDescription = null,
                                    tint = SpotifyGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = playlist.name,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = CrispWhite
                                    ),
                                    maxLines = 1
                                )
                                Text(
                                    text = "Playlist • ${playlist.getTrackIds().size} songs",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MutedGrey,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SidebarNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) SpotifyGreen else MutedGrey,
        animationSpec = tween(durationMillis = 180),
        label = "sidebar_icon_color"
    )
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) CrispWhite else MutedGrey,
        animationSpec = tween(durationMillis = 180),
        label = "sidebar_label_color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
            ),
            color = labelColor
        )
    }
}
