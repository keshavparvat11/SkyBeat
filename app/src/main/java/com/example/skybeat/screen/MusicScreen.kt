package com.example.skybeat.screen

import android.media.MediaPlayer
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skybeat.model.Song
import com.example.skybeat.viewModel.SongViewModel


@Composable
fun MusicScreen(viewModel: SongViewModel = viewModel()) {
    val context = LocalContext.current
    val songs by viewModel.songs.collectAsState()
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var currentSong by remember { mutableStateOf<Song?>(null) }
    var isPlaying by remember { mutableStateOf(false) }

    // Load songs once
    LaunchedEffect(Unit) {
        viewModel.loadSongs()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            "🎵 My Songs",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Song list
        songs.forEach { song ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable {
                        mediaPlayer?.release()

                        mediaPlayer = MediaPlayer().apply {
                            setDataSource(context, Uri.parse(song.file))
                            prepareAsync()
                            setOnPreparedListener {
                                start()
                                isPlaying = true
                            }
                        }

                        currentSong = song
                    },
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (currentSong?.title == song.title)
                        MaterialTheme.colorScheme.primaryContainer
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(
                        imageVector = if (currentSong?.title == song.title && isPlaying)
                            Icons.Default.PlayArrow else Icons.Default.MusicNote,
                        contentDescription = null,
                        tint = if (currentSong?.title == song.title)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(song.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // Push mini-player to bottom

        // Mini-player bar
        currentSong?.let { song ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(song.title, style = MaterialTheme.typography.titleMedium)
                        Text(
                            song.artist,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row {
                        IconButton(onClick = {
                            if (mediaPlayer?.isPlaying == true) {
                                mediaPlayer?.pause()
                                isPlaying = false
                            } else {
                                mediaPlayer?.start()
                                isPlaying = true
                            }
                        }) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause"
                            )
                        }

                        IconButton(onClick = {
                            mediaPlayer?.stop()
                            mediaPlayer?.release()
                            mediaPlayer = null
                            currentSong = null
                            isPlaying = false
                        }) {
                            Icon(
                                imageVector = Icons.Default.Stop,
                                contentDescription = "Stop"
                            )
                        }
                    }
                }
            }
        }
    }
}
