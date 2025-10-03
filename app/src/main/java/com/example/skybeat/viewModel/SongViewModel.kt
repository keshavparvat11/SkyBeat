package com.example.skybeat.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.skybeat.model.Song
import com.example.skybeat.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PlaybackViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

    init {
        loadSongs()
    }

    private fun setupPlayer(context: Context) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply {
                addListener(object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        _isPlaying.value = playbackState == Player.STATE_READY && this@apply.playWhenReady
                        if (playbackState == Player.STATE_ENDED) {
                            playNextSong(context)
                        }
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        _isPlaying.value = isPlaying
                        if (isPlaying) {
                            startProgressUpdate()
                        } else {
                            stopProgressUpdate()
                        }
                    }

                    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                        if (mediaItem != null) {
                            val song = _songs.value.find { it.file == mediaItem.mediaId }
                            _currentSong.value = song
                        }
                    }
                })
            }
        }
    }

    fun loadSongs() {
        db.collection("songs")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    error.printStackTrace()
                    return@addSnapshotListener
                }

                val songsList = value?.documents?.map { doc ->
                    Song(
                        sId = doc.id,
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        file = doc.getString("file") ?: "",
                        bannerUrl = doc.getString("bannerUrl") ?: ""
                    )
                } ?: emptyList()

                _songs.value = songsList
            }
    }

    fun playSong(song: Song, context: Context) {
        setupPlayer(context)

        val mediaItem = MediaItem.Builder()
            .setMediaId(song.file)
            .setUri(song.file)
            .build()

        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        exoPlayer?.playWhenReady = true

        _currentSong.value = song
    }

    fun togglePlayPause() {
        if (exoPlayer?.isPlaying == true) {
            exoPlayer?.pause()
        } else {
            exoPlayer?.play()
        }
    }

    fun playNextSong(context: Context) {
        setupPlayer(context)
        val currentIndex = _songs.value.indexOfFirst { it.file == _currentSong.value?.file }
        val nextIndex = (currentIndex + 1) % _songs.value.size
        playSong(_songs.value[nextIndex], context)
    }

    fun playPreviousSong(context: Context) {
        setupPlayer(context)
        val currentIndex = _songs.value.indexOfFirst { it.file == _currentSong.value?.file }
        val previousIndex = if (currentIndex - 1 < 0) _songs.value.size - 1 else currentIndex - 1
        playSong(_songs.value[previousIndex], context)
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
                        _progress.value = it.currentPosition.toFloat() / it.duration.toFloat()
                    }
                }
                kotlinx.coroutines.delay(100)
            }
        }
    }

    private fun stopProgressUpdate() {
        progressJob?.cancel()
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
    fun addSongToFirestore(
        title: String,
        artist: String,
        file: String,
        bannerUrl: String? = null,
        onResult: (Boolean, String?) -> Unit
    ) {
        val song = hashMapOf(
            "title" to title,
            "artist" to artist,
            "file" to file,
            "bannerUrl" to bannerUrl
        )

        db.collection("songs")
            .add(song)
            .addOnSuccessListener {
                onResult(true, null)
            }
            .addOnFailureListener { e ->
                onResult(false, e.message)
            }
    }
    fun updateSong(
        songId: String,
        title: String,
        artist: String,
        file: String,
        bannerUrl: String?,
        onResult: (Boolean, String?) -> Unit
    ) {
        val songData = hashMapOf(
            "title" to title,
            "artist" to artist,
            "file" to file,
            "bannerUrl" to bannerUrl
        )

        db.collection("songs").document(songId)
            .set(songData)
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
    fun deleteSong(songId: String, onResult: (Boolean, String?) -> Unit) {
        db.collection("songs").document(songId)
            .delete()
            .addOnSuccessListener { onResult(true, null) }
            .addOnFailureListener { e -> onResult(false, e.message) }
    }
}
