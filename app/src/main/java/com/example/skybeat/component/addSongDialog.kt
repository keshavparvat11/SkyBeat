package com.example.skybeat.component

import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun addSongDialog(
    onDismiss : () -> Unit,
    onSave : (String, String, String, String) -> Unit
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
        title = {},
        confirmButton = {
            Button(
                onClick = {
                    var isValid = isFormValid
                    validationError = !isValid
                    if (isValid) {
                        loading = true
                        onSave(title, artist, file, bannerUrl)
                        loading = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !loading,

                ) {
                if (loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add Song")
                }

            }
        },
        dismissButton = {
            TextButton(
                onClick = { if (!loading) onDismiss() }
            ) {
                Text("Cancel")
            }
        },
        text = {
            Column(modifier = Modifier.padding(16.dp)) {

                SongInputField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Song Name",
                    isError = validationError && !isTitleValid
                )

                SongInputField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = "Artist Name",
                    isError = validationError && !isArtistValid
                )

                SongInputField(
                    value = file,
                    onValueChange = { file = it },
                    label = "Audio File URL",
                    isError = validationError && !isFileValid,
                    keyboardType = KeyboardType.Uri
                )

                SongInputField(
                    value = bannerUrl,
                    onValueChange = { bannerUrl = it },
                    label = "Banner URL (Cover Art)",
                    isError = validationError && !isBannerValid,
                    keyboardType = KeyboardType.Uri
                )

                Spacer(Modifier.height(16.dp))


            }
        }
    )
}

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