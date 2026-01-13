package com.example.skybeat.viewModel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import com.example.skybeat.model.Song
import com.example.skybeat.player.MusicNotificationManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

@UnstableApi
class PlaybackViewModel : ViewModel() {

    /* -------------------- FIREBASE -------------------- */

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    /* -------------------- STATE -------------------- */

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _playlistSongs = MutableStateFlow<List<Song>>(emptyList())
    val playlistSongs: StateFlow<List<Song>> = _playlistSongs

    private val _downloadedSongs = MutableStateFlow<Set<String>>(emptySet())
    val downloadedSongs: StateFlow<Set<String>> = _downloadedSongs

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    /* -------------------- PLAYER -------------------- */

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

    /* -------------------- MEDIA SESSION + NOTIFICATION -------------------- */

    private var mediaSession: MediaSession? = null
    private var musicNotificationManager: MusicNotificationManager? = null
    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition

    init {
        loadSongs()
    }
    fun seekTo(progress: Float) {
        exoPlayer?.let { player ->
            val duration = player.duration
            if (duration > 0) {
                val seekPosition = (duration * progress).toLong()
                player.seekTo(seekPosition)
            }
        }
    }

    /* -------------------- PLAYER SETUP -------------------- */

    private fun setupPlayer(context: Context) {
        if (exoPlayer != null) return

        exoPlayer = ExoPlayer.Builder(context).build().apply {
            addListener(object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    _isPlaying.value = isPlaying
                    if (isPlaying) startProgressUpdate() else stopProgressUpdate()
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        playNextSong(context)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    mediaItem?.mediaId?.let { file ->
                        _currentSong.value = _songs.value.find { it.file == file }
                    }
                }
            })
        }

        mediaSession = MediaSession.Builder(context, exoPlayer!!)
            .setId("SkyBeatSession")
            .build()

        musicNotificationManager = MusicNotificationManager(
            context = context,
            player = exoPlayer!!,
            mediaSession = mediaSession!!
        )
    }

    /* -------------------- SONGS -------------------- */

    fun loadSongs() {
        db.collection("songs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                _songs.value = snapshot?.documents?.map { doc ->
                    Song(
                        sId = doc.id,
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        file = doc.getString("file") ?: "",
                        bannerUrl = doc.getString("bannerUrl")
                    )
                } ?: emptyList()

                loadDownloadedSongs()
            }
    }

    /* -------------------- PLAYBACK -------------------- */

    fun playSong(song: Song, context: Context) {
        setupPlayer(context)

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.file)
            .setUri(song.file)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .build()
            )
            .build()

        exoPlayer?.apply {
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true
        }

        _currentSong.value = song
    }

    fun togglePlayPause() {
        exoPlayer?.let {
            if (it.isPlaying) it.pause() else it.play()
        }
    }

    fun playNextSong(context: Context) {
        val list = _songs.value
        val index = list.indexOfFirst { it.file == _currentSong.value?.file }
        if (index == -1 || list.isEmpty()) return
        playSong(list[(index + 1) % list.size], context)
    }

    fun playPreviousSong(context: Context) {
        val list = _songs.value
        val index = list.indexOfFirst { it.file == _currentSong.value?.file }
        if (index == -1 || list.isEmpty()) return
        playSong(list[if (index - 1 < 0) list.lastIndex else index - 1], context)
    }

    fun stopSong() {
        exoPlayer?.stop()
        _currentSong.value = null
        _isPlaying.value = false
        _progress.value = 0f
        stopProgressUpdate()
    }

    /* -------------------- PROGRESS -------------------- */
    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (true) {
                exoPlayer?.let { player ->
                    val dur = player.duration
                    if (dur > 0) {
                        _duration.value = dur
                        _currentPosition.value = player.currentPosition
                        _progress.value = player.currentPosition.toFloat() / dur
                    }
                }
                delay(500)
            }
        }
    }


    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    /* -------------------- PLAYLIST -------------------- */

    fun loadPlaylist(playlistName: String = "favorites") {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("playlists")
            .document(playlistName)
            .collection("songs")
            .addSnapshotListener { snapshot, _ ->
                _playlistSongs.value = snapshot?.documents?.map { doc ->
                    Song(
                        sId = doc.id,
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        file = doc.getString("file") ?: "",
                        bannerUrl = doc.getString("bannerUrl")
                    )
                } ?: emptyList()
            }
    }

    fun addSongToPlaylist(song: Song, playlistName: String = "favorites") {
        val userId = auth.currentUser?.uid ?: return
        if (song.sId.isBlank()) return

        db.collection("users")
            .document(userId)
            .collection("playlists")
            .document(playlistName)
            .collection("songs")
            .document(song.sId)
            .set(song)
    }

    fun removeSongFromPlaylist(song: Song, playlistName: String = "favorites") {
        val userId = auth.currentUser?.uid ?: return
        if (song.sId.isBlank()) return

        db.collection("users")
            .document(userId)
            .collection("playlists")
            .document(playlistName)
            .collection("songs")
            .document(song.sId)
            .delete()
    }

    fun isSongInPlaylist(songId: String): Boolean {
        return _playlistSongs.value.any { it.sId == songId }
    }

    /* -------------------- DOWNLOADS -------------------- */

    fun markSongDownloaded(songId: String) {
        _downloadedSongs.value = _downloadedSongs.value + songId
    }

    private fun getDownloadDir(): File {
        return Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .resolve("SkyBeat")
    }

    fun loadDownloadedSongs() {
        val dir = getDownloadDir()
        if (!dir.exists()) {
            _downloadedSongs.value = emptySet()
            return
        }

        val titles = dir.listFiles()
            ?.filter { it.extension == "mp3" }
            ?.map { it.nameWithoutExtension }
            ?.toSet()
            ?: emptySet()

        _downloadedSongs.value = _songs.value
            .filter { titles.contains(it.title) }
            .map { it.sId }
            .toSet()
    }
    fun deleteDownloadedSong(song: Song) {
        val dir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)
            .resolve("SkyBeat")

        val file = File(dir, "${song.title}.mp3")

        if (file.exists()) {
            file.delete()
        }

        // Update state so UI refreshes
        _downloadedSongs.value = _downloadedSongs.value - song.sId
    }


    /* -------------------- CLEANUP -------------------- */

    override fun onCleared() {
        super.onCleared()
        musicNotificationManager?.hide()
        mediaSession?.release()
        exoPlayer?.release()

        musicNotificationManager = null
        mediaSession = null
        exoPlayer = null
    }


    /* -------------------- ADMIN  -------------------- */

    fun addSongToFirestore(
        title: String,
        artist: String,
        file: String,
        bannerUrl: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val song = mapOf(
            "title" to title,
            "artist" to artist,
            "file" to file,
            "bannerUrl" to bannerUrl
        )

        db.collection("songs")
            .add(song)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun updateSong(
        songId: String,
        title: String,
        artist: String,
        file: String,
        bannerUrl: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val data = mapOf(
            "title" to title,
            "artist" to artist,
            "file" to file,
            "bannerUrl" to bannerUrl
        )

        db.collection("songs")
            .document(songId)
            .set(data)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }

    fun deleteSong(songId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("songs")
            .document(songId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}