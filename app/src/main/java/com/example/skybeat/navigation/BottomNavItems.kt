package com.example.skybeat.navigation


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItems(val route: String, val label: String, val icon: ImageVector) {
    object Home : BottomNavItems("home", "Home", Icons.Default.Home)
    object Library : BottomNavItems("library", "Library", Icons.Default.LibraryMusic)
    object Downloads : BottomNavItems("downloads", "Downloads", Icons.Default.FileDownload)
}