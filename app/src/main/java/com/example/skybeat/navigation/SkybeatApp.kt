package com.example.skybeat.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skybeat.screen.MusicScreen
import com.example.skybeat.screen.SongPlaying
import com.example.skybeat.viewModel.PlaybackViewModel
import android.net.Uri
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState

// Composable function that sets up the app's navigation.
@Composable
fun SkybeatApp(playbackViewModel: PlaybackViewModel = viewModel()){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home"){
        composable("home") {
            MusicScreen(navController = navController)
        }
        composable(
            route = "detail/{songFile}",
            arguments = listOf(navArgument("songFile") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            // Decode the URL from the navigation argument
            val encodedFile = backStackEntry.arguments?.getString("songFile")
            val songFile = encodedFile?.let { Uri.decode(it) }

            val song = playbackViewModel.songs.collectAsState().value.find { it.file == songFile }
            if (song != null) {
                SongPlaying(song = song)
            } else {
               // Text("Song not found")
            }
        }
    }
}
