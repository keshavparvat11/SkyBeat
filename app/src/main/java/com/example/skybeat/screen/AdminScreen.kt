package com.example.skybeat.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skybeat.component.addSongDialog
import com.example.skybeat.viewModel.PlaybackViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(vm: PlaybackViewModel = viewModel()) {

    val context = LocalContext.current
    var message by remember { mutableStateOf<String?>(null) }
    val songs by vm.songs.collectAsState()

    var addSong by remember { mutableStateOf(false) }


    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row {
                    Text("SkyBeat")
                    Spacer(Modifier.width(180.dp))

                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    addSong = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { innerpadding ->


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if(addSong) {
                        addSongDialog(onDismiss = { addSong = false },
                            onSave = { a, b, c, d ->
                            vm.addSongToFirestore(a, b, c, d) { success, e ->
                                message = e

                                if (success) {
                                    Toast.makeText(
                                        context,
                                        "Song Added Successfully! 🎉",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    addSong = false
                                } else {
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                }

                            }
                        })
                    }

                }
            }


            Text(
                "Current Songs",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(top = 8.dp)
            ) {
                items(songs, key = { it.title + it.artist }) { song -> // Use a unique key
                    SongListItem(song = song)
                }
                if (songs.isEmpty()) {
                    item {
                        Text(
                            "No songs found. Add one above!",
                            modifier = Modifier.fillMaxWidth().padding(32.dp)
                                .wrapContentSize(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

// --- Reusable Composable for Text Fields ---
@Composable
fun SongInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isError: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = isError,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        trailingIcon = {
            if (isError) {
                Icon(Icons.Filled.Warning, "Error", tint = MaterialTheme.colorScheme.error)
            }
        },
        supportingText = {
            if (isError) {
                Text("Field cannot be empty", color = MaterialTheme.colorScheme.error)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

// --- Reusable Composable for Song List Item ---
// This assumes 'song' has 'title' and 'artist' properties.
@Composable
fun SongListItem(song: Any) { // Replace 'Any' with your actual Song data class
    // Using a Surface for better visual separation and a subtle click effect
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Implement action like 'Edit' or 'Remove' */ },
        shape = MaterialTheme.shapes.small,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Song Title
                Text(
                    text = (song as? com.example.skybeat.model.Song)?.title ?: "Unknown Title", // Replace with your actual Song class reference
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                // Artist Name
                Text(
                    text = "by " + ((song as? com.example.skybeat.model.Song)?.artist ?: "Unknown Artist"), // Replace with your actual Song class reference
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // A placeholder for a context menu or action icon
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Song options",
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}