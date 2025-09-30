package com.example.skybeat.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skybeat.model.Song
import com.example.skybeat.viewModel.PlaybackViewModel
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    playbackViewModel: PlaybackViewModel = viewModel()
) {
    val context = LocalContext.current
    val songs by playbackViewModel.songs.collectAsState()
    val currentSong by playbackViewModel.currentSong.collectAsState()
    val isPlaying by playbackViewModel.isPlaying.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Skybeat",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            if (currentSong != null) {
                MiniPlayer(
                    currentSong = currentSong!!,
                    isPlaying = isPlaying,
                    progress = playbackViewModel.progress.collectAsState().value,
                    onPlayPause = { playbackViewModel.togglePlayPause() },
                    onNext = { playbackViewModel.playNextSong(context) },
                    onPrevious = { playbackViewModel.playPreviousSong(context) },
                    onClick = {
                        val encodedFile = Uri.encode(currentSong!!.file)
                        navController.navigate("detail/$encodedFile")
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Welcome, listener!",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (songs.isEmpty()) {
                    Text("No music available to stream.")
                } else {
                    Text(
                        "Featured",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(songs.take(3)) { song ->
                            SongItem(
                                song = song,
                                isPlaying = currentSong?.file == song.file,
                                onClick = {
                                    val encodedFile = Uri.encode(song.file)
                                    navController.navigate("detail/$encodedFile")
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        "Recently Played",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(songs.take(2)) { song ->
                            SongItem(
                                song = song,
                                isPlaying = currentSong?.file == song.file,
                                onClick = {
                                    val encodedFile = Uri.encode(song.file)
                                    navController.navigate("detail/$encodedFile")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}