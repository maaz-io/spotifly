package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioPlaybackManager
import com.example.audio.PlaybackState
import com.example.data.IngestionState
import com.example.data.PlaylistEntity
import com.example.data.SpotiflyDatabase
import com.example.data.SpotiflyRepository
import com.example.data.TrackEntity
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class SpotiflyNavTab {
    HOME, SEARCH, LIBRARY
}

class SpotiflyViewModel(application: Application) : AndroidViewModel(application) {

    private val database = SpotiflyDatabase.getInstance(application)
    private val repository = SpotiflyRepository(database.spotiflyDao())

    val audioManager = AudioPlaybackManager()

    val allTracks: StateFlow<List<TrackEntity>> = repository.allTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val likedTracks: StateFlow<List<TrackEntity>> = repository.likedTracks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPlaylists: StateFlow<List<PlaylistEntity>> = repository.allPlaylists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ingestionState: StateFlow<IngestionState> = repository.ingestionState
    val playbackState: StateFlow<PlaybackState> = audioManager.playbackState

    private val _selectedTab = MutableStateFlow(SpotiflyNavTab.HOME)
    val selectedTab: StateFlow<SpotiflyNavTab> = _selectedTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _localSearchResults = MutableStateFlow<List<TrackEntity>>(emptyList())
    val localSearchResults: StateFlow<List<TrackEntity>> = _localSearchResults.asStateFlow()

    private val _onlineSearchResults = MutableStateFlow<List<TrackEntity>>(emptyList())
    val onlineSearchResults: StateFlow<List<TrackEntity>> = _onlineSearchResults.asStateFlow()

    private val _isSearchingOnline = MutableStateFlow(false)
    val isSearchingOnline: StateFlow<Boolean> = _isSearchingOnline.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _selectedPlaylist = MutableStateFlow<PlaylistEntity?>(null)
    val selectedPlaylist: StateFlow<PlaylistEntity?> = _selectedPlaylist.asStateFlow()

    private val _showCreatePlaylistDialog = MutableStateFlow(false)
    val showCreatePlaylistDialog: StateFlow<Boolean> = _showCreatePlaylistDialog.asStateFlow()

    private val _showQueueDialog = MutableStateFlow(false)
    val showQueueDialog: StateFlow<Boolean> = _showQueueDialog.asStateFlow()

    private val _showFullScreenPlayer = MutableStateFlow(false)
    val showFullScreenPlayer: StateFlow<Boolean> = _showFullScreenPlayer.asStateFlow()

    private var searchDebounceJob: Job? = null

    init {
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun selectTab(tab: SpotiflyNavTab) {
        _selectedTab.value = tab
        _selectedPlaylist.value = null
    }

    fun selectPlaylist(playlist: PlaylistEntity?) {
        _selectedPlaylist.value = playlist
    }

    fun setShowCreatePlaylistDialog(show: Boolean) {
        _showCreatePlaylistDialog.value = show
    }

    fun setShowQueueDialog(show: Boolean) {
        _showQueueDialog.value = show
    }

    fun setShowFullScreenPlayer(show: Boolean) {
        _showFullScreenPlayer.value = show
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchDebounceJob?.cancel()

        if (query.isBlank()) {
            _localSearchResults.value = emptyList()
            _onlineSearchResults.value = emptyList()
            _hasSearched.value = false
            _isSearchingOnline.value = false
            repository.resetIngestionState()
            return
        }

        _hasSearched.value = true

        // 1. Immediately search local tracks
        viewModelScope.launch {
            _localSearchResults.value = repository.searchLocalTracksSync(query)
        }

        // 2. Debounce and search real online songs in real-time
        searchDebounceJob = viewModelScope.launch {
            delay(350) // User finished typing
            performOnlineSearch(query)
        }
    }

    fun triggerSearchImmediate() {
        val q = _searchQuery.value.trim()
        if (q.isNotEmpty()) {
            searchDebounceJob?.cancel()
            viewModelScope.launch {
                _localSearchResults.value = repository.searchLocalTracksSync(q)
                performOnlineSearch(q)
            }
        }
    }

    private suspend fun performOnlineSearch(query: String) {
        _isSearchingOnline.value = true
        try {
            val results = repository.searchOnlineTracks(query)
            _onlineSearchResults.value = results
        } catch (e: Exception) {
            _onlineSearchResults.value = emptyList()
        } finally {
            _isSearchingOnline.value = false
        }
    }

    fun forceIngestQuery(query: String) {
        viewModelScope.launch {
            val ingestedTrack = repository.triggerIngestSearchAndDownload(query)
            if (ingestedTrack != null) {
                val current = _onlineSearchResults.value.toMutableList()
                if (!current.any { it.id == ingestedTrack.id }) {
                    current.add(0, ingestedTrack)
                    _onlineSearchResults.value = current
                }
                playTrack(ingestedTrack, current)
            }
        }
    }

    fun playTrack(track: TrackEntity, contextList: List<TrackEntity>? = null) {
        val queue = if (!contextList.isNullOrEmpty()) {
            contextList
        } else {
            val all = allTracks.value
            if (all.isNotEmpty()) all else listOf(track)
        }

        audioManager.playTrack(track, queue)

        viewModelScope.launch {
            // Persist online track into Room library so user has it saved permanently
            repository.saveOrUpdateTrack(track)
            repository.incrementPlayCount(track.id)
        }
    }

    fun togglePlayPause() {
        if (playbackState.value.currentTrack == null && allTracks.value.isNotEmpty()) {
            playTrack(allTracks.value.first())
        } else {
            audioManager.togglePlayPause()
        }
    }

    fun skipNext() {
        audioManager.skipNext()
    }

    fun skipPrevious() {
        audioManager.skipPrevious()
    }

    fun seekTo(seconds: Int) {
        audioManager.seekTo(seconds)
    }

    fun toggleShuffle() {
        audioManager.toggleShuffle()
    }

    fun toggleRepeat() {
        audioManager.toggleRepeat()
    }

    fun setVolume(vol: Float) {
        audioManager.setVolume(vol)
    }

    fun toggleMute() {
        audioManager.toggleMute()
    }

    fun toggleLike(track: TrackEntity) {
        viewModelScope.launch {
            repository.toggleLikeTrack(track)
        }
    }

    fun toggleDownload(track: TrackEntity) {
        viewModelScope.launch {
            repository.toggleDownloadTrack(track)
        }
    }

    fun createPlaylist(name: String, description: String) {
        viewModelScope.launch {
            repository.createPlaylist(name, description)
            _showCreatePlaylistDialog.value = false
        }
    }

    fun addTrackToPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            repository.addTrackToPlaylist(playlistId, trackId)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioManager.release()
    }
}
