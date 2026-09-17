package com.example.audio

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import com.example.data.MusicSearchService
import com.example.data.TrackEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

enum class RepeatMode {
    OFF, ALL, ONE
}

data class PlaybackState(
    val currentTrack: TrackEntity? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPositionSeconds: Int = 0,
    val durationSeconds: Int = 0,
    val isShuffle: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val volume: Float = 0.8f,
    val isMuted: Boolean = false,
    val queue: List<TrackEntity> = emptyList(),
    val queueIndex: Int = 0,
    val errorMessage: String? = null
)

class AudioPlaybackManager {
    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private var streamResolveJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun playTrack(track: TrackEntity, newQueue: List<TrackEntity> = emptyList()) {
        val q = if (newQueue.isNotEmpty()) newQueue else listOf(track)
        val index = q.indexOfFirst { it.id == track.id }.let { if (it >= 0) it else 0 }

        _playbackState.value = _playbackState.value.copy(
            currentTrack = track,
            queue = q,
            queueIndex = index,
            isPlaying = false,
            isLoading = true,
            currentPositionSeconds = 0,
            durationSeconds = if (track.durationSeconds > 0) track.durationSeconds else 180,
            errorMessage = null
        )

        stopCurrentPlayer()
        streamResolveJob?.cancel()

        if (track.filePath.startsWith("http")) {
            startStreamingAudio(track.filePath, track)
        } else {
            // Missing stream URL: resolve authentic original stream in real-time
            streamResolveJob = scope.launch(Dispatchers.IO) {
                val fullTrackStream = MusicSearchService.fetchPreviewForTrack(track.title, track.artist)
                if (fullTrackStream != null && fullTrackStream.first.startsWith("http")) {
                    val updatedTrack = track.copy(
                        filePath = fullTrackStream.first,
                        coverDrawable = if (track.coverDrawable.isNotBlank() && track.coverDrawable != "app_icon") {
                            track.coverDrawable
                        } else {
                            fullTrackStream.second
                        }
                    )
                    launch(Dispatchers.Main) {
                        if (_playbackState.value.currentTrack?.id == track.id) {
                            _playbackState.value = _playbackState.value.copy(currentTrack = updatedTrack)
                            startStreamingAudio(fullTrackStream.first, updatedTrack)
                        }
                    }
                } else {
                    launch(Dispatchers.Main) {
                        _playbackState.value = _playbackState.value.copy(
                            isLoading = false,
                            isPlaying = false,
                            errorMessage = "Track audio stream not available"
                        )
                    }
                }
            }
        }
    }

    private fun startStreamingAudio(streamUrl: String, track: TrackEntity) {
        scope.launch(Dispatchers.Main) {
            if (_playbackState.value.currentTrack?.id != track.id) return@launch
            try {
                stopCurrentPlayer()
                val mp = MediaPlayer()
                mediaPlayer = mp

                mp.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )

                mp.setDataSource(streamUrl)

                mp.setOnPreparedListener { player ->
                    if (mediaPlayer == player) {
                        val vol = if (_playbackState.value.isMuted) 0f else _playbackState.value.volume
                        player.setVolume(vol, vol)
                        player.start()

                        val playerSec = (player.duration / 1000).coerceAtLeast(1)
                        _playbackState.value = _playbackState.value.copy(
                            isPlaying = true,
                            isLoading = false,
                            currentPositionSeconds = 0,
                            durationSeconds = playerSec
                        )
                        startProgressTracker()
                    }
                }

                mp.setOnCompletionListener { player ->
                    if (mediaPlayer == player) {
                        handleTrackCompletion()
                    }
                }

                mp.setOnErrorListener { player, what, extra ->
                    Log.e("AudioPlaybackManager", "MediaPlayer error: what=$what, extra=$extra")
                    _playbackState.value = _playbackState.value.copy(
                        isPlaying = false,
                        isLoading = false,
                        errorMessage = "Error during playback"
                    )
                    true
                }

                mp.prepareAsync()
            } catch (e: Exception) {
                Log.e("AudioPlaybackManager", "Failed to start streaming $streamUrl", e)
                _playbackState.value = _playbackState.value.copy(
                    isPlaying = false,
                    isLoading = false,
                    errorMessage = "Failed to stream audio"
                )
            }
        }
    }

    private fun handleTrackCompletion() {
        val state = _playbackState.value
        when (state.repeatMode) {
            RepeatMode.ONE -> {
                try {
                    mediaPlayer?.seekTo(0)
                    mediaPlayer?.start()
                } catch (e: Exception) {
                    Log.w("AudioPlaybackManager", "Repeat one seek error", e)
                }
                _playbackState.value = state.copy(isPlaying = true, currentPositionSeconds = 0)
                startProgressTracker()
            }
            RepeatMode.ALL -> {
                skipNext()
            }
            RepeatMode.OFF -> {
                if (state.queueIndex + 1 < state.queue.size) {
                    skipNext()
                } else {
                    _playbackState.value = state.copy(
                        isPlaying = false,
                        currentPositionSeconds = 0
                    )
                    progressJob?.cancel()
                }
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                delay(500)
                val mp = mediaPlayer
                if (mp != null && mp.isPlaying) {
                    val posSec = (mp.currentPosition / 1000)
                    _playbackState.value = _playbackState.value.copy(
                        currentPositionSeconds = posSec
                    )
                }
            }
        }
    }

    fun togglePlayPause() {
        val mp = mediaPlayer
        if (mp != null) {
            try {
                if (mp.isPlaying) {
                    mp.pause()
                    _playbackState.value = _playbackState.value.copy(isPlaying = false)
                } else {
                    mp.start()
                    _playbackState.value = _playbackState.value.copy(isPlaying = true)
                    startProgressTracker()
                }
            } catch (e: Exception) {
                Log.e("AudioPlaybackManager", "togglePlayPause error", e)
            }
        } else {
            val track = _playbackState.value.currentTrack
            if (track != null) {
                playTrack(track, _playbackState.value.queue)
            }
        }
    }

    fun skipNext() {
        val current = _playbackState.value
        val queue = current.queue
        if (queue.isEmpty()) return
        val nextIndex = when {
            current.isShuffle -> (queue.indices).random()
            current.queueIndex + 1 < queue.size -> current.queueIndex + 1
            current.repeatMode == RepeatMode.ALL -> 0
            else -> 0
        }
        val nextTrack = queue[nextIndex]
        playTrack(nextTrack, queue)
    }

    fun skipPrevious() {
        val current = _playbackState.value
        val queue = current.queue
        if (queue.isEmpty()) return
        if (current.currentPositionSeconds > 3) {
            seekTo(0)
            return
        }
        val prevIndex = when {
            current.isShuffle -> (queue.indices).random()
            current.queueIndex - 1 >= 0 -> current.queueIndex - 1
            else -> queue.size - 1
        }
        val prevTrack = queue[prevIndex]
        playTrack(prevTrack, queue)
    }

    fun seekTo(seconds: Int) {
        try {
            mediaPlayer?.seekTo(seconds * 1000)
            _playbackState.value = _playbackState.value.copy(currentPositionSeconds = seconds)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun toggleShuffle() {
        _playbackState.value = _playbackState.value.copy(
            isShuffle = !_playbackState.value.isShuffle
        )
    }

    fun toggleRepeat() {
        val nextMode = when (_playbackState.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _playbackState.value = _playbackState.value.copy(repeatMode = nextMode)
    }

    fun setVolume(vol: Float) {
        val clamped = vol.coerceIn(0f, 1f)
        _playbackState.value = _playbackState.value.copy(
            volume = clamped,
            isMuted = clamped == 0f
        )
        try {
            mediaPlayer?.setVolume(clamped, clamped)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun toggleMute() {
        val current = _playbackState.value
        if (current.isMuted) {
            val restored = if (current.volume <= 0.05f) 0.7f else current.volume
            _playbackState.value = current.copy(isMuted = false, volume = restored)
            try { mediaPlayer?.setVolume(restored, restored) } catch (e: Exception) {}
        } else {
            _playbackState.value = current.copy(isMuted = true)
            try { mediaPlayer?.setVolume(0f, 0f) } catch (e: Exception) {}
        }
    }

    private fun stopCurrentPlayer() {
        progressJob?.cancel()
        progressJob = null
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (e: Exception) {
            // Ignore
        } finally {
            mediaPlayer = null
        }
    }

    fun release() {
        stopCurrentPlayer()
        streamResolveJob?.cancel()
        scope.cancel()
    }
}
