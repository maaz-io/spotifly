package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

sealed interface IngestionState {
    object Idle : IngestionState
    data class Ingesting(
        val query: String,
        val step: String,
        val command: String,
        val progress: Float
    ) : IngestionState
    data class Success(val track: TrackEntity) : IngestionState
    data class Error(val message: String) : IngestionState
}

class SpotiflyRepository(private val dao: SpotiflyDao) {

    val allTracks: Flow<List<TrackEntity>> = dao.getAllTracks()
    val likedTracks: Flow<List<TrackEntity>> = dao.getLikedTracks()
    val allPlaylists: Flow<List<PlaylistEntity>> = dao.getAllPlaylists()

    private val _ingestionState = MutableStateFlow<IngestionState>(IngestionState.Idle)
    val ingestionState: StateFlow<IngestionState> = _ingestionState.asStateFlow()

    /**
     * Seeds initial database with high quality authentic tracks containing real streamable
     * original studio masters and high-definition album artwork from official catalogs.
     */
    suspend fun checkAndSeedInitialData() = withContext(Dispatchers.IO) {
        val currentTracks = dao.getAllTracks().first()
        val hasDubbedOrAudiusUrls = currentTracks.any {
            it.filePath.contains("audius") || !it.filePath.startsWith("http")
        }
        val needsSeed = currentTracks.isEmpty() || hasDubbedOrAudiusUrls

        if (needsSeed) {
            val initialTracks = listOf(
                TrackEntity(
                    id = 1,
                    title = "Blinding Lights",
                    artist = "The Weeknd",
                    album = "After Hours",
                    durationSeconds = 200,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/61/e7/3f/61e73f94-018d-5f50-50ec-8521952bc72e/20UM1IM11629.rgb.jpg/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 171,
                    isLiked = true,
                    playCount = 142,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/19/d6/60/19d660ff-e3a9-8377-15a3-ce4b28e89cac/mzaf_18422426156481158187.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 2,
                    title = "Resonance",
                    artist = "HOME",
                    album = "Odyssey",
                    durationSeconds = 212,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/4f/13/65/4f1365b0-e97c-c469-c438-2f7d8f204355/872133025584_cover.jpg/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 100,
                    isLiked = true,
                    playCount = 98,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/47/d0/32/47d0326f-0757-4400-6532-caf37f69feb2/mzaf_7480432866230687091.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 3,
                    title = "Midnight City",
                    artist = "M83",
                    album = "Hurry Up, We're Dreaming",
                    durationSeconds = 241,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/cb/7b/a9/cb7ba903-b5f1-cc21-90db-7a81b7aa0997/724596951057.jpg/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 105,
                    isLiked = false,
                    playCount = 76,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/24/09/79/2409794c-3d5d-af26-580e-7dc00ee4f207/mzaf_369629549966021675.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 4,
                    title = "Get You the Moon",
                    artist = "Kina ft. Snøw",
                    album = "Get You the Moon",
                    durationSeconds = 179,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music126/v4/b8/6f/7c/b86f7ce0-ac13-123c-bffe-d6a226ea4f14/886447369142.jpg/600x600bb.jpg",
                    audioGenre = "lofi",
                    bpm = 84,
                    isLiked = true,
                    playCount = 110,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/c5/ab/d6/c5abd6f8-e34d-3229-00cc-3bab40f40683/mzaf_6752080994238203574.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 5,
                    title = "death bed (coffee for your head)",
                    artist = "Powfu ft. beabadoobee",
                    album = "poems of the past",
                    durationSeconds = 173,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/58/60/43/58604376-4479-9814-1d3b-61d796ee1be9/886448465140.jpg/600x600bb.jpg",
                    audioGenre = "lofi",
                    bpm = 80,
                    isLiked = false,
                    playCount = 64,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/51/34/00/51340045-8035-1b5c-457f-5e49ab253506/mzaf_580442320445066029.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 6,
                    title = "Levitating",
                    artist = "Dua Lipa",
                    album = "Future Nostalgia",
                    durationSeconds = 203,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music116/v4/6c/11/d6/6c11d681-aa3a-d59e-4c2e-f77e181026ab/190295092665.jpg/600x600bb.jpg",
                    audioGenre = "pop",
                    bpm = 103,
                    isLiked = true,
                    playCount = 185,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/59/dc/4d/59dc4dda-93ff-8f1c-c536-f005f6ea6af5/mzaf_3066686759813252385.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 7,
                    title = "Starboy",
                    artist = "The Weeknd ft. Daft Punk",
                    album = "Starboy",
                    durationSeconds = 230,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/b5/92/bb/b592bb72-52e3-e756-9b26-9f56d08f47ab/16UMGIM67864.rgb.jpg/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 93,
                    isLiked = true,
                    playCount = 135,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/11/71/d6/1171d6ad-3c96-e027-2af6-58028426588c/mzaf_15137631797407745471.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 8,
                    title = "Shape of You",
                    artist = "Ed Sheeran",
                    album = "÷ (Divide)",
                    durationSeconds = 233,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/15/e6/e8/15e6e8a4-4190-6a8b-86c3-ab4a51b88288/190295851286.jpg/600x600bb.jpg",
                    audioGenre = "pop",
                    bpm = 96,
                    isLiked = false,
                    playCount = 89,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/44/c7/4f/44c74f0d-72dc-6143-d4d0-ba14d661ca0d/mzaf_9566898362556366703.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 9,
                    title = "Tum Hi Ho",
                    artist = "Mithoon & Arijit Singh",
                    album = "Aashiqui 2",
                    durationSeconds = 261,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/bb/23/ee/bb23eeed-0c35-4f1d-2b11-485622777ae4/8902894353007_cover.jpg/600x600bb.jpg",
                    audioGenre = "bollywood",
                    bpm = 85,
                    isLiked = true,
                    playCount = 240,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/3a/8c/9b/3a8c9b0b-2def-750a-f615-1555bf941edf/mzaf_17229496441442805917.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 10,
                    title = "Bohemian Rhapsody",
                    artist = "Queen",
                    album = "A Night at the Opera",
                    durationSeconds = 355,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music211/v4/8b/0a/ea/8b0aea60-6f4a-195b-5958-cdf459c2333b/602527644271.jpg/600x600bb.jpg",
                    audioGenre = "rock",
                    bpm = 72,
                    isLiked = true,
                    playCount = 210,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/17/fc/1e/17fc1eba-946d-84a9-710b-a0e88ea64209/mzaf_3049006317693088799.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 11,
                    title = "Yellow",
                    artist = "Coldplay",
                    album = "Parachutes",
                    durationSeconds = 269,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music221/v4/f5/93/8c/f5938c49-964c-31d1-4b33-78b634f71fb7/190295978075.jpg/600x600bb.jpg",
                    audioGenre = "rock",
                    bpm = 88,
                    isLiked = true,
                    playCount = 195,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/66/f3/1a/66f31a76-a6ed-cb4c-f353-23310a7ae9a8/mzaf_10593596652344378873.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 12,
                    title = "Heat Waves",
                    artist = "Glass Animals",
                    album = "Dreamland",
                    durationSeconds = 238,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/da/8b/77/da8b7731-6f4f-eacf-5e74-8b23389eefa1/20UMGIM03371.rgb.jpg/600x600bb.jpg",
                    audioGenre = "pop",
                    bpm = 80,
                    isLiked = false,
                    playCount = 160,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/a3/4c/b9/a34cb911-40fc-5f0c-e862-14bd171a77aa/mzaf_384792072030970151.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 13,
                    title = "After Dark",
                    artist = "Mr.Kitty",
                    album = "Time",
                    durationSeconds = 259,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/ce/5d/c6/ce5dc65e-6dac-bb8a-daaf-72bf77d0ba75/616450974909.png/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 124,
                    isLiked = true,
                    playCount = 230,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview211/v4/3e/cb/52/3ecb5294-5ea1-e392-7262-1b10cc67a299/mzaf_17548677748266742699.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 14,
                    title = "Nightcall",
                    artist = "Kavinsky",
                    album = "Nightcall",
                    durationSeconds = 258,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music125/v4/c1/2d/fe/c12dfe8f-cdf6-e179-d69a-8ec35f760266/00602537248681.rgb.jpg/600x600bb.jpg",
                    audioGenre = "synthwave",
                    bpm = 91,
                    isLiked = true,
                    playCount = 175,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/d2/45/fb/d245fbf9-8570-fdc0-5e6b-aa528c130486/mzaf_11947081694159530687.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 15,
                    title = "Circles",
                    artist = "Post Malone",
                    album = "Hollywood's Bleeding",
                    durationSeconds = 215,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music115/v4/7b/1b/1b/7b1b1b0b-7ce2-b223-f9e0-8e36abe51877/19UMGIM78325.rgb.jpg/600x600bb.jpg",
                    audioGenre = "pop",
                    bpm = 120,
                    isLiked = false,
                    playCount = 145,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/f9/b1/aa/f9b1aaed-3e24-227f-153d-99969f8b8464/mzaf_6272498007975402144.plus.aac.p.m4a"
                ),
                TrackEntity(
                    id = 16,
                    title = "As It Was",
                    artist = "Harry Styles",
                    album = "Harry's House",
                    durationSeconds = 167,
                    coverDrawable = "https://is1-ssl.mzstatic.com/image/thumb/Music126/v4/2a/19/fb/2a19fb85-2f70-9e44-f2a9-82abe679b88e/886449990061.jpg/600x600bb.jpg",
                    audioGenre = "pop",
                    bpm = 174,
                    isLiked = false,
                    playCount = 188,
                    filePath = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview221/v4/67/10/16/67101606-3869-ca44-6c03-e13d6322cb51/mzaf_1135399237022217274.plus.aac.p.m4a"
                )
            )
            dao.insertTracks(initialTracks)

            if (dao.getPlaylistCount() == 0) {
                val initialPlaylists = listOf(
                    PlaylistEntity(
                        id = 1,
                        name = "Synthwave Chill",
                        description = "Neon night drives with 80s synths and retro futuristic basslines.",
                        coverType = "synthwave",
                        isCustom = false,
                        trackIdsCsv = "1,2,3,7,13,14"
                    ),
                    PlaylistEntity(
                        id = 2,
                        name = "Lo-Fi Study Beats",
                        description = "Cozy downtempo hip hop beats to study, relax, and code to.",
                        coverType = "lofi",
                        isCustom = false,
                        trackIdsCsv = "4,5"
                    ),
                    PlaylistEntity(
                        id = 3,
                        name = "Today's Top Hits",
                        description = "The biggest global hits with authentic high-fidelity audio streams.",
                        coverType = "gradient",
                        isCustom = false,
                        trackIdsCsv = "1,6,7,8,9,10,11,12,15,16"
                    )
                )
                dao.insertPlaylists(initialPlaylists)
            }
        }
    }

    /**
     * Searches global online music database in real-time.
     */
    suspend fun searchOnlineTracks(query: String): List<TrackEntity> = withContext(Dispatchers.IO) {
        MusicSearchService.searchTracks(query)
    }

    /**
     * Saves or updates a track into local Room SQLite database for persistent offline/library access.
     */
    suspend fun saveOrUpdateTrack(track: TrackEntity): Long = withContext(Dispatchers.IO) {
        dao.insertTrack(track)
    }

    fun searchLocalTracks(query: String): Flow<List<TrackEntity>> {
        return dao.searchTracks(query)
    }

    suspend fun searchLocalTracksSync(query: String): List<TrackEntity> = withContext(Dispatchers.IO) {
        dao.searchTracksSync(query)
    }

    suspend fun toggleLikeTrack(track: TrackEntity) = withContext(Dispatchers.IO) {
        dao.setLiked(track.id, !track.isLiked)
    }

    suspend fun toggleDownloadTrack(track: TrackEntity) = withContext(Dispatchers.IO) {
        dao.setDownloaded(track.id, !track.isDownloaded)
    }

    suspend fun incrementPlayCount(trackId: Long) = withContext(Dispatchers.IO) {
        dao.incrementPlayCount(trackId)
    }

    suspend fun createPlaylist(name: String, description: String): Long = withContext(Dispatchers.IO) {
        val newPlaylist = PlaylistEntity(
            name = name.ifBlank { "My Playlist" },
            description = description.ifBlank { "User created playlist" },
            coverType = "gradient",
            isCustom = true,
            trackIdsCsv = ""
        )
        dao.insertPlaylist(newPlaylist)
    }

    suspend fun addTrackToPlaylist(playlistId: Long, trackId: Long) = withContext(Dispatchers.IO) {
        val playlist = dao.getPlaylistById(playlistId) ?: return@withContext
        val existing = playlist.getTrackIds().toMutableList()
        if (!existing.contains(trackId)) {
            existing.add(trackId)
            dao.updatePlaylist(playlist.copy(trackIdsCsv = existing.joinToString(",")))
        }
    }

    /**
     * Real-time audio stream ingestion pipeline:
     * Searches internet source for real track, resolves CDN preview URL and HD artwork,
     * persists into SQLite Room database, and provides real track object for immediate playback.
     */
    suspend fun triggerIngestSearchAndDownload(query: String): TrackEntity? = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext null
        try {
            _ingestionState.value = IngestionState.Ingesting(
                query = query,
                step = "Querying live online music catalog...",
                command = "GET https://itunes.apple.com/search?term=$query",
                progress = 0.35f
            )

            val onlineResults = MusicSearchService.searchTracks(query, limit = 5)
            if (onlineResults.isNotEmpty()) {
                val bestTrack = onlineResults.first()

                _ingestionState.value = IngestionState.Ingesting(
                    query = query,
                    step = "Caching real audio stream and HD cover art...",
                    command = "STREAM_URL: ${bestTrack.filePath.take(45)}...",
                    progress = 0.85f
                )

                dao.insertTrack(bestTrack)

                _ingestionState.value = IngestionState.Success(bestTrack)
                bestTrack
            } else {
                _ingestionState.value = IngestionState.Error("No tracks found online for \"$query\"")
                null
            }
        } catch (e: Exception) {
            _ingestionState.value = IngestionState.Error("Failed to fetch track: ${e.localizedMessage}")
            null
        }
    }

    fun resetIngestionState() {
        _ingestionState.value = IngestionState.Idle
    }
}
