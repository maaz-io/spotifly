package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.PlaylistEntity
import com.example.ui.components.BottomPlayerDock
import com.example.ui.components.SidebarNavigation
import com.example.ui.components.TopSearchBar
import com.example.ui.screens.CreatePlaylistDialog
import com.example.ui.screens.FullScreenMediaPlayer
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.PlaylistDetailScreen
import com.example.ui.screens.QueueDialog
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.CrispWhite
import com.example.ui.theme.ElevatedGrey
import com.example.ui.theme.MutedGrey
import com.example.ui.theme.PitchBlack
import com.example.ui.theme.SpotifyGreen
import com.example.ui.theme.SurfaceCharcoal

@Composable
fun SpotiflyApp(
    viewModel: SpotiflyViewModel = viewModel()
) {
    val allTracks by viewModel.allTracks.collectAsStateWithLifecycle()
    val likedTracks by viewModel.likedTracks.collectAsStateWithLifecycle()
    val playlists by viewModel.allPlaylists.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val localResults by viewModel.localSearchResults.collectAsStateWithLifecycle()
    val onlineResults by viewModel.onlineSearchResults.collectAsStateWithLifecycle()
    val isSearchingOnline by viewModel.isSearchingOnline.collectAsStateWithLifecycle()
    val hasSearched by viewModel.hasSearched.collectAsStateWithLifecycle()
    val ingestionState by viewModel.ingestionState.collectAsStateWithLifecycle()
    val playbackState by viewModel.playbackState.collectAsStateWithLifecycle()
    val selectedPlaylist by viewModel.selectedPlaylist.collectAsStateWithLifecycle()
    val showCreatePlaylistDialog by viewModel.showCreatePlaylistDialog.collectAsStateWithLifecycle()
    val showQueueDialog by viewModel.showQueueDialog.collectAsStateWithLifecycle()
    val showFullScreenPlayer by viewModel.showFullScreenPlayer.collectAsStateWithLifecycle()

    val onLikedSongsClick: () -> Unit = {
        viewModel.selectPlaylist(
            PlaylistEntity(
                id = -1,
                name = "Liked Songs",
                description = "Your favorite tracks saved to library.",
                coverType = "liked",
                isCustom = false,
                trackIdsCsv = likedTracks.joinToString(",") { it.id.toString() }
            )
        )
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(PitchBlack)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        val isWideScreen = maxWidth >= 700.dp

        Column(modifier = Modifier.fillMaxSize()) {
            // Main Top & Middle Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                // Sidebar on wide screens (Tablet / Desktop 3-Column layout)
                if (isWideScreen) {
                    SidebarNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        playlists = playlists,
                        likedSongsCount = likedTracks.size,
                        onPlaylistClick = { viewModel.selectPlaylist(it) },
                        onLikedSongsClick = onLikedSongsClick,
                        onCreatePlaylistClick = { viewModel.setShowCreatePlaylistDialog(true) }
                    )
                }

                // Main Content View & Top Bar
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(PitchBlack)
                ) {
                    // Top Bar with Search bar and Profile avatar
                    TopSearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            if (selectedTab != SpotiflyNavTab.SEARCH) {
                                viewModel.selectTab(SpotiflyNavTab.SEARCH)
                            }
                            viewModel.onSearchQueryChanged(it)
                        },
                        onSearchSubmit = { viewModel.triggerSearchImmediate() },
                        onBackClick = if (selectedPlaylist != null) {
                            { viewModel.selectPlaylist(null) }
                        } else null
                    )

                    // Main View Body
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        AnimatedContent(
                            targetState = Pair(selectedTab, selectedPlaylist),
                            transitionSpec = {
                                if (targetState.second != null && initialState.second == null) {
                                    (slideInVertically(
                                        initialOffsetY = { 60 },
                                        animationSpec = tween(220, easing = FastOutSlowInEasing)
                                    ) + fadeIn(animationSpec = tween(200)))
                                        .togetherWith(fadeOut(animationSpec = tween(150)))
                                } else if (targetState.second == null && initialState.second != null) {
                                    fadeIn(animationSpec = tween(200))
                                        .togetherWith(
                                            slideOutVertically(
                                                targetOffsetY = { 60 },
                                                animationSpec = tween(200, easing = FastOutSlowInEasing)
                                            ) + fadeOut(animationSpec = tween(150))
                                        )
                                } else {
                                    (fadeIn(animationSpec = tween(200)) + slideInHorizontally(
                                        initialOffsetX = { 24 },
                                        animationSpec = tween(200)
                                    )).togetherWith(fadeOut(animationSpec = tween(150)))
                                }
                            },
                            label = "screen_tab_transition",
                            modifier = Modifier.fillMaxSize()
                        ) { (_, currentPlaylist) ->
                            if (currentPlaylist != null) {
                                val playlistTrackIds = currentPlaylist.getTrackIds()
                                val playlistTracks = if (currentPlaylist.id == -1L) {
                                    likedTracks
                                } else {
                                    allTracks.filter { playlistTrackIds.contains(it.id) }
                                }

                                PlaylistDetailScreen(
                                    title = currentPlaylist.name,
                                    description = currentPlaylist.description,
                                    coverType = currentPlaylist.coverType,
                                    tracks = playlistTracks,
                                    currentTrackId = playbackState.currentTrack?.id,
                                    isPlaying = playbackState.isPlaying,
                                    onBackClick = { viewModel.selectPlaylist(null) },
                                    onTrackClick = { viewModel.playTrack(it, playlistTracks) },
                                    onPlayAllClick = {
                                        if (playlistTracks.isNotEmpty()) {
                                            viewModel.playTrack(playlistTracks.first(), playlistTracks)
                                        }
                                    },
                                    onToggleLike = { viewModel.toggleLike(it) },
                                    onToggleDownload = { viewModel.toggleDownload(it) }
                                )
                            } else {
                                when (selectedTab) {
                                    SpotiflyNavTab.HOME -> {
                                        HomeScreen(
                                            allTracks = allTracks,
                                            playlists = playlists,
                                            likedTracksCount = likedTracks.size,
                                            currentTrackId = playbackState.currentTrack?.id,
                                            isPlaying = playbackState.isPlaying,
                                            onTrackClick = { viewModel.playTrack(it, allTracks) },
                                            onToggleLike = { viewModel.toggleLike(it) },
                                            onToggleDownload = { viewModel.toggleDownload(it) },
                                            onPlaylistClick = { viewModel.selectPlaylist(it) },
                                            onLikedSongsClick = onLikedSongsClick
                                        )
                                    }

                                    SpotiflyNavTab.SEARCH -> {
                                        SearchScreen(
                                            searchQuery = searchQuery,
                                            hasSearched = hasSearched,
                                            localResults = localResults,
                                            onlineResults = onlineResults,
                                            isSearchingOnline = isSearchingOnline,
                                            ingestionState = ingestionState,
                                            currentTrackId = playbackState.currentTrack?.id,
                                            isPlaying = playbackState.isPlaying,
                                            isLoading = playbackState.isLoading,
                                            onTrackClick = { track, queue -> viewModel.playTrack(track, queue) },
                                            onToggleLike = { viewModel.toggleLike(it) },
                                            onToggleDownload = { viewModel.toggleDownload(it) },
                                            onTriggerIngest = { viewModel.forceIngestQuery(it) },
                                            onGenreClick = { genre ->
                                                viewModel.onSearchQueryChanged(genre)
                                            }
                                        )
                                    }

                                    SpotiflyNavTab.LIBRARY -> {
                                        LibraryScreen(
                                            playlists = playlists,
                                            likedTracks = likedTracks,
                                            allTracks = allTracks,
                                            onPlaylistClick = { viewModel.selectPlaylist(it) },
                                            onLikedSongsClick = onLikedSongsClick,
                                            onCreatePlaylistClick = { viewModel.setShowCreatePlaylistDialog(true) }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Mobile Navigation Bar (if screen is compact)
                    if (!isWideScreen) {
                        NavigationBar(
                            containerColor = PitchBlack,
                            contentColor = CrispWhite,
                            tonalElevation = 0.dp
                        ) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == SpotiflyNavTab.HOME && selectedPlaylist == null) Icons.Filled.Home else Icons.Outlined.Home,
                                        contentDescription = "Home"
                                    )
                                },
                                label = { Text("Home") },
                                selected = selectedTab == SpotiflyNavTab.HOME && selectedPlaylist == null,
                                onClick = { viewModel.selectTab(SpotiflyNavTab.HOME) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SpotifyGreen,
                                    selectedTextColor = CrispWhite,
                                    indicatorColor = ElevatedGrey,
                                    unselectedIconColor = MutedGrey,
                                    unselectedTextColor = MutedGrey
                                ),
                                modifier = Modifier.testTag("nav_item_home")
                            )

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == SpotiflyNavTab.SEARCH && selectedPlaylist == null) Icons.Filled.Search else Icons.Outlined.Search,
                                        contentDescription = "Search"
                                    )
                                },
                                label = { Text("Search") },
                                selected = selectedTab == SpotiflyNavTab.SEARCH && selectedPlaylist == null,
                                onClick = { viewModel.selectTab(SpotiflyNavTab.SEARCH) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SpotifyGreen,
                                    selectedTextColor = CrispWhite,
                                    indicatorColor = ElevatedGrey,
                                    unselectedIconColor = MutedGrey,
                                    unselectedTextColor = MutedGrey
                                ),
                                modifier = Modifier.testTag("nav_item_search")
                            )

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        imageVector = if (selectedTab == SpotiflyNavTab.LIBRARY && selectedPlaylist == null) Icons.Filled.LibraryMusic else Icons.Outlined.LibraryMusic,
                                        contentDescription = "Your Library"
                                    )
                                },
                                label = { Text("Your Library") },
                                selected = selectedTab == SpotiflyNavTab.LIBRARY && selectedPlaylist == null,
                                onClick = { viewModel.selectTab(SpotiflyNavTab.LIBRARY) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = SpotifyGreen,
                                    selectedTextColor = CrispWhite,
                                    indicatorColor = ElevatedGrey,
                                    unselectedIconColor = MutedGrey,
                                    unselectedTextColor = MutedGrey
                                ),
                                modifier = Modifier.testTag("nav_item_library")
                            )
                        }
                    }
                }
            }

            // Persistent Bottom Playback Control Dock
            BottomPlayerDock(
                playbackState = playbackState,
                onPlayPauseToggle = { viewModel.togglePlayPause() },
                onSkipNext = { viewModel.skipNext() },
                onSkipPrevious = { viewModel.skipPrevious() },
                onSeekTo = { viewModel.seekTo(it) },
                onToggleShuffle = { viewModel.toggleShuffle() },
                onToggleRepeat = { viewModel.toggleRepeat() },
                onVolumeChange = { viewModel.setVolume(it) },
                onToggleMute = { viewModel.toggleMute() },
                onToggleLike = { viewModel.toggleLike(it) },
                onQueueClick = { viewModel.setShowQueueDialog(true) },
                onOpenFullScreen = { viewModel.setShowFullScreenPlayer(true) }
            )
        }
    }

    // Full Screen Media Player Overlay with spring slide transition
    AnimatedVisibility(
        visible = showFullScreenPlayer && playbackState.currentTrack != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.85f, stiffness = 420f)
        ) + fadeIn(animationSpec = tween(180)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.88f, stiffness = 450f)
        ) + fadeOut(animationSpec = tween(160))
    ) {
        FullScreenMediaPlayer(
            playbackState = playbackState,
            onCollapse = { viewModel.setShowFullScreenPlayer(false) },
            onPlayPauseToggle = { viewModel.togglePlayPause() },
            onSkipNext = { viewModel.skipNext() },
            onSkipPrevious = { viewModel.skipPrevious() },
            onSeekTo = { viewModel.seekTo(it) },
            onToggleShuffle = { viewModel.toggleShuffle() },
            onToggleRepeat = { viewModel.toggleRepeat() },
            onToggleLike = { viewModel.toggleLike(it) },
            onToggleDownload = { viewModel.toggleDownload(it) },
            onVolumeChange = { viewModel.setVolume(it) },
            onToggleMute = { viewModel.toggleMute() },
            onQueueClick = { viewModel.setShowQueueDialog(true) }
        )
    }

    // Dialogs
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { viewModel.setShowCreatePlaylistDialog(false) },
            onCreate = { name, desc ->
                viewModel.createPlaylist(name, desc)
            }
        )
    }

    if (showQueueDialog) {
        QueueDialog(
            currentTrack = playbackState.currentTrack,
            queue = playbackState.queue,
            currentIndex = playbackState.queueIndex,
            onTrackClick = { viewModel.playTrack(it, playbackState.queue) },
            onDismiss = { viewModel.setShowQueueDialog(false) }
        )
    }
}
