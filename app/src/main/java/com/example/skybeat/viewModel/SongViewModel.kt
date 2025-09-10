package com.example.skybeat.viewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skybeat.model.Song
import com.example.skybeat.network.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SongViewModel : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadSongs() {
        viewModelScope.launch {
            try {
                val response = RetrofitInstance.api.getSongs()
                _songs.value = response
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = e.localizedMessage ?: "Unknown error"
            }
        }
    }
}
