package com.example.skybeat.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skybeat.model.Song
import com.example.skybeat.network.RetrofitInstance
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AdminViewModel: ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private var _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    init {
        loadSongs()
    }

    fun loadSongs() {
        viewModelScope.launch {
            try {
                val snapshot = db.collection("songs").get().await()   // need kotlinx-coroutines-play-services
                val songsList = snapshot.documents.map { doc ->
                    Song(
                        title = doc.getString("title") ?: "",
                        artist = doc.getString("artist") ?: "",
                        file = doc.getString("file") ?: "",
                        bannerUrl = doc.getString("bannerUrl") ?: ""
                    )
                }
                _songs.value = songsList
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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

}