package com.example.skybeat.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.example.skybeat.model.Song

@Composable
fun AddSongDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var artist by remember { mutableStateOf("") }
    var file by remember { mutableStateOf("") }
    var bannerUrl by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val isTitleValid = title.isNotBlank()
    val isArtistValid = artist.isNotBlank()
    val isFileValid = file.isNotBlank()
    val isBannerValid = bannerUrl.isNotBlank()
    val isFormValid = isTitleValid && isArtistValid && isFileValid && isBannerValid
    AlertDialog(

        onDismissRequest = { if (!loading) onDismiss() },
        title = {
            Text(
                text = "Add New Song",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {

                Text(
                    text = "Song Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SongInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Song Title *",
                    placeholder = "Enter song title",
                    isError = validationError && !isTitleValid,
                    leadingIcon = Icons.Default.MusicNote
                )

                SongInputField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = "Artist Name *",
                    placeholder = "Enter artist name",
                    isError = validationError && !isArtistValid,
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Media URLs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SongInputField(
                    value = file,
                    onValueChange = { file = it },
                    label = "Audio File URL *",
                    placeholder = "https://example.com/audio.mp3",
                    isError = validationError && !isFileValid,
                    keyboardType = KeyboardType.Uri,
                    leadingIcon = Icons.Default.Link
                )

                SongInputField(
                    value = bannerUrl,
                    onValueChange = { bannerUrl = it },
                    label = "Cover Art URL *",
                    placeholder = "https://example.com/cover.jpg",
                    isError = validationError && !isBannerValid,
                    keyboardType = KeyboardType.Uri,
                    leadingIcon = Icons.Default.Image
                )


                if (validationError && !isFormValid) {
                    Text(
                        text = "Please fill in all required fields",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isValid = isFormValid
                    validationError = !isValid
                    if (isValid) {
                        loading = true
                        onSave(title, artist, file, bannerUrl)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                shape = MaterialTheme.shapes.large
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Adding Song...")
                } else {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Song")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (!loading) onDismiss() },
                enabled = !loading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Cancel")
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 8.dp),
        properties = DialogProperties(dismissOnClickOutside = !loading,usePlatformDefaultWidth = false)
    )
}

@Composable
fun EditSongDialog(
    song: Song,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist) }
    var file by remember { mutableStateOf(song.file) }
    var bannerUrl by remember { mutableStateOf(song.bannerUrl ?: "") }

    var validationError by remember { mutableStateOf(false) }
    var loading by remember { mutableStateOf(false) }

    val isTitleValid = title.isNotBlank()
    val isArtistValid = artist.isNotBlank()
    val isFileValid = file.isNotBlank()
    val isFormValid = isTitleValid && isArtistValid && isFileValid

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        title = {
            Text(
                text = "Edit Song",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {

                Text(
                    text = "Song Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SongInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Song Title *",
                    placeholder = "Enter song title",
                    isError = validationError && !isTitleValid,
                    leadingIcon = Icons.Default.MusicNote
                )

                SongInputField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = "Artist Name *",
                    placeholder = "Enter artist name",
                    isError = validationError && !isArtistValid,
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(16.dp))


                Text(
                    text = "Media URLs",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                SongInputField(
                    value = file,
                    onValueChange = { file = it },
                    label = "Audio File URL *",
                    placeholder = "https://example.com/audio.mp3",
                    isError = validationError && !isFileValid,
                    keyboardType = KeyboardType.Uri,
                    leadingIcon = Icons.Default.Link
                )

                SongInputField(
                    value = bannerUrl,
                    onValueChange = { bannerUrl = it },
                    label = "Cover Art URL",
                    placeholder = "https://example.com/cover.jpg (Optional)",
                    isError = false, // Banner is optional in edit
                    keyboardType = KeyboardType.Uri,
                    leadingIcon = Icons.Default.Image
                )


                if (validationError && !isFormValid) {
                    Text(
                        text = "Please fill in all required fields",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val isValid = isFormValid
                    validationError = !isValid
                    if (isValid) {
                        loading = true
                        onSave(title, artist, file, bannerUrl)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,
                shape = MaterialTheme.shapes.large
            ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Updating...")
                } else {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Update",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Update Song")
                }
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = { if (!loading) onDismiss() },
                    enabled = !loading
                ) {
                    Text("Cancel")
                }
            }
        },
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .padding(horizontal = 8.dp),
        properties = DialogProperties(dismissOnClickOutside = !loading,usePlatformDefaultWidth = false)
    )
}

@Composable
fun SongInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    isError: Boolean,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            placeholder = {
                if (placeholder.isNotEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            },
            isError = isError,
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            leadingIcon = {
                if (leadingIcon != null) {
                    Icon(
                        leadingIcon,
                        contentDescription = null,
                        tint = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            trailingIcon = {
                if (isError) {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                } else if (value.isNotBlank()) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = "Valid",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            supportingText = {
                if (isError) {
                    Text(
                        "This field is required",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )
    }
}