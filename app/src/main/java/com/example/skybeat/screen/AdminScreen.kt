package com.example.skybeat.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skybeat.component.AddSongDialog
import com.example.skybeat.component.EditSongDialog
import com.example.skybeat.model.Song
import com.example.skybeat.viewModel.PlaybackViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(vm: PlaybackViewModel = viewModel()) {

    val context = LocalContext.current
    var message by remember { mutableStateOf<String?>(null) }
    val songs by vm.songs.collectAsState()

    var showAddSongDialog by remember { mutableStateOf(false) }
    var editMode by remember { mutableStateOf(false) }
    var showEditSongDialog by remember { mutableStateOf(false) }
    var editingSong by remember { mutableStateOf<Song?>(null) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "SkyBeat Admin",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        floatingActionButton = {
            if (!editMode) {
                FloatingActionButton(
                    onClick = { showAddSongDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Song", tint = Color.White)
                }
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Songs (${songs.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FilterChip(
                    selected = editMode,
                    onClick = { editMode = !editMode },
                    label = {
                        Text(if (editMode) "Editing" else "Edit")
                    },
                    leadingIcon = if (editMode) {
                        {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "Editing",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )
            }

            if (songs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicOff,
                            contentDescription = "No songs",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "No songs yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                        Text(
                            "Add your first song to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Button(
                            onClick = { showAddSongDialog = true },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Text("Add First Song", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(songs, key = { it.title + it.artist }) { song ->
                        SongListItem(
                            song = song,
                            editMode = editMode,
                            onEdit = {
                                editingSong = song
                                showEditSongDialog = true
                            },
                            onDelete = { songToDelete ->
                                vm.deleteSong(songToDelete.sId) { success, error ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "Song deleted successfully",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Error: $error",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        if (showAddSongDialog) {
            AddSongDialog(
                onDismiss = { showAddSongDialog = false },
                onSave = { title, artist, file, banner ->
                    vm.addSongToFirestore(title, artist, "https://raw.githubusercontent.com/keshavparvat11/data/main/"+file, banner) { success, e ->
                        message = e
                        if (success) {
                            Toast.makeText(
                                context,
                                "Song Added Successfully! 🎉",
                                Toast.LENGTH_SHORT
                            ).show()
                            showAddSongDialog = false
                        } else {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        if (showEditSongDialog && editingSong != null) {
            EditSongDialog(
                song = editingSong!!,
                onDismiss = {
                    showEditSongDialog = false
                    editingSong = null
                },
                onSave = { title, artist, file, banner ->
                    vm.updateSong(
                        editingSong!!.sId,
                        title,
                        artist,
                        file,
                        banner
                    ) { success, error ->
                        Toast.makeText(
                            context,
                            if (success) "Song updated successfully!" else "Error: $error",
                            Toast.LENGTH_SHORT
                        ).show()
                        if (success) {
                            showEditSongDialog = false
                            editingSong = null
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun SongListItem(
    song: Song,
    editMode: Boolean,
    onEdit: (Song) -> Unit,
    onDelete: (Song) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (editMode) onEdit(song) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (editMode) MaterialTheme.colorScheme.surfaceVariant
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = "Album Art",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title ?: "Unknown Title",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "by ${song.artist ?: "Unknown Artist"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }


            if (editMode) {
                Row {

                    IconButton(
                        onClick = { onEdit(song) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { onDelete(song) },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            } else {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}