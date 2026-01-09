package com.example.skybeat.viewModel

import android.content.Context
import android.os.Environment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.skybeat.model.Song
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

class PlaybackViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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

    init {
        loadSongs()
    }

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
        val currentIndex = list.indexOfFirst { it.file == _currentSong.value?.file }
        if (currentIndex == -1 || list.isEmpty()) return

        playSong(list[(currentIndex + 1) % list.size], context)
    }

    fun playPreviousSong(context: Context) {
        val list = _songs.value
        val currentIndex = list.indexOfFirst { it.file == _currentSong.value?.file }
        if (currentIndex == -1 || list.isEmpty()) return

        val prevIndex = if (currentIndex - 1 < 0) list.lastIndex else currentIndex - 1
        playSong(list[prevIndex], context)
    }

    fun stopSong() {
        exoPlayer?.stop()
        _currentSong.value = null
        _isPlaying.value = false
        _progress.value = 0f
        stopProgressUpdate()
    }

    private fun startProgressUpdate() {
        progressJob?.cancel()
        progressJob = viewModelScope.launch {
            while (_isPlaying.value) {
                exoPlayer?.let {
                    if (it.duration > 0) {
                        _progress.value =
                            it.currentPosition.toFloat() / it.duration.toFloat()
                    }
                }
                delay(100)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
        progressJob = null
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }

    /* -------------------- PLAYLIST -------------------- */

    fun loadPlaylist(playlistName: String = "favorites") {
        val userId = auth.currentUser?.uid ?: return

        db.collection("users")
            .document(userId)
            .collection("playlists")
            .document(playlistName)
            .collection("songs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

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

        val data = mapOf(
            "title" to song.title,
            "artist" to song.artist,
            "file" to song.file,
            "bannerUrl" to song.bannerUrl
        )

        db.collection("users")
            .document(userId)
            .collection("playlists")
            .document(playlistName)
            .collection("songs")
            .document(song.sId)
            .set(data)
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

    /* -------------------- ADMIN (OPTIONAL) -------------------- */

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

        val downloadedTitles = dir.listFiles()
            ?.filter { it.extension == "mp3" }
            ?.map { it.nameWithoutExtension }
            ?.toSet()
            ?: emptySet()

        // Match file names with songs
        val downloadedIds = _songs.value
            .filter { downloadedTitles.contains(it.title) }
            .map { it.sId }
            .toSet()

        _downloadedSongs.value = downloadedIds
    }

}
