package com.example.skybeat.screen

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skybeat.viewModel.AdminViewModel

@Composable
fun AdminScreen(vm: AdminViewModel = viewModel()) {

    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var file by remember { mutableStateOf("") }
    var bannerUrl by remember { mutableStateOf("") }
    var message by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }
    val songs by vm.songs.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Add New User", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Song Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = artist,
            onValueChange = { artist = it },
            label = { Text("Artist Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = file,
            onValueChange = { file = it },
            label = { Text("Url") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = bannerUrl,
            onValueChange = { bannerUrl = it },
            label = { Text("Banner Url") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && artist.isNotEmpty()) {
                    loading = true
                    vm.addSongToFirestore(title, artist, file, bannerUrl) { success, error ->
                        loading = false
                        message = if (success) "User added successfully!" else "Error: $error"
                        if (success) {
                            title = ""
                            artist = ""
                            file = ""
                            bannerUrl = ""
                        }
                    }
                } else {
                    message = "Please fill all fields"
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (loading) "Adding..." else "Add User")
        }

        Spacer(Modifier.height(16.dp))

        message?.let {
            Text(it, color = if (it.startsWith("Error")) Color.Red else Color.Green)
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(songs) { song ->
                Text(text = song.title)
            }
        }
    }
}