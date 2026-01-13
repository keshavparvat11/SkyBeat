package com.example.skybeat.screen

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import com.example.skybeat.component.DownloadHelper
import com.example.skybeat.model.Song
import com.example.skybeat.viewModel.PlaybackViewModel

@androidx.annotation.OptIn(UnstableApi::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    playbackViewModel: PlaybackViewModel = viewModel()
) {
    val songs by playbackViewModel.songs.collectAsState()
    val currentSong by playbackViewModel.currentSong.collectAsState()
    val downloadedSongs by playbackViewModel.downloadedSongs.collectAsState()
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Skybeat",
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                actions = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.Search, null)
                    }
                    IconButton(onClick = { navController.navigate("Login") }) {
                        Icon(Icons.Default.MoreVert, null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0F2027),
                            Color(0xFF203A43),
                            Color(0xFF2C5364)
                        )
                    )
                )
                .padding(padding)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {

                /* ---------- GREETING ---------- */
                item {
                    Column {
                        Text(
                            text = "Welcome back 🎧",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Enjoy your music",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

                /* ---------- FEATURED ---------- */
                item {
                    SectionTitle("Featured")
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(top = 12.dp)
                    ) {
                        items(songs.take(5)) { song ->
                            FeaturedCard(
                                song = song,
                                isPlaying = currentSong?.file == song.file,
                                onClick = {
                                    navController.navigate(
                                        "detail/${Uri.encode(song.file)}"
                                    )
                                }
                            )
                        }
                    }
                }

                /* ---------- RECENT ---------- */
                item {
                    SectionTitle("Recently Played")
                }

                items(songs.take(10)) { song ->
                    val playlistSongs by playbackViewModel.playlistSongs.collectAsState()
                    val isInPlaylist = playlistSongs.any { it.sId == song.sId }

                    val isDownloaded = downloadedSongs.contains(song.sId)
                    val isPlaying = currentSong?.file == song.file

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isPlaying)
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)
                        else
                            MaterialTheme.colorScheme.surface.copy(alpha = 0.15f),
                        tonalElevation = if (isPlaying) 6.dp else 1.dp
                    ) {
                        SongItem(
                            song = song,
                            isPlaying = isPlaying,
                            isInPlaylist = isInPlaylist,
                            isDownloaded = isDownloaded,
                            onClick = {
                                navController.navigate(
                                    "detail/${Uri.encode(song.file)}"
                                )
                            },
                            onDownloadClick = {
                                if (!isDownloaded) {
                                    DownloadHelper.downloadSong(
                                        context,
                                        it.file,
                                        it.title
                                    )
                                    playbackViewModel.markSongDownloaded(it.sId)
                                }
                            },
                            onPlaylistClick = { clickedSong, inPlaylist ->
                                if (inPlaylist) {
                                    playbackViewModel.removeSongFromPlaylist(clickedSong)
                                } else {
                                    playbackViewModel.addSongToPlaylist(clickedSong)
                                }
                            }
                        )
                    }
                }

                item { Spacer(Modifier.height(120.dp)) }
            }
        }
    }
}

/* ---------- SECTION TITLE ---------- */

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontWeight = FontWeight.ExtraBold,
        style = MaterialTheme.typography.titleLarge,
        color = Color.White
    )
}

/* ---------- FEATURED CARD ---------- */

@Composable
fun FeaturedCard(
    song: Song,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(220.dp)
            .height(280.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        Color.Black.copy(alpha = 0.6f)
                    )
                )
            )
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(120.dp))

            Column {
                Text(
                    song.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    song.artist,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            if (isPlaying) {
                Text(
                    "Now Playing",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
