package com.example.skybeat.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skybeat.screen.*
import com.example.skybeat.viewModel.PlaybackViewModel
import android.net.Uri
import androidx.compose.runtime.collectAsState
import com.example.taskdesk.screen.logingScreen.LoginScreen
import com.example.taskdesk.screen.logingScreen.SignUpScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkybeatApp(playbackViewModel: PlaybackViewModel = viewModel()) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute == BottomNavItems.Home.route ||
                currentRoute == BottomNavItems.Library.route ||
                currentRoute == BottomNavItems.Downloads.route
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val bottomNavItems = listOf(BottomNavItems.Home, BottomNavItems.Library, BottomNavItems.Downloads)
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(text = item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Start",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("Start") { SplashScreen(navController) }
            composable(BottomNavItems.Home.route) { HomeScreen(navController) }
            composable(BottomNavItems.Library.route) { LibraryScreen(navController) }
            composable(BottomNavItems.Downloads.route) { DownloadsScreen(navController) }
            composable("search") { SearchScreen(navController) }
            composable("Admin") { AdminScreen() }
            composable("Login") { LoginScreen(navController) }
            composable("SignUp") { SignUpScreen(navController) }
            composable(
                route = "detail/{songFile}",
                arguments = listOf(navArgument("songFile") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                val encodedFile = backStackEntry.arguments?.getString("songFile")
                val songFile = encodedFile?.let { Uri.decode(it) }
                val song = playbackViewModel.songs.collectAsState().value.find { it.file == songFile }
                if (song != null) {
                    SongPlaying(song = song)
                } else {
                    Text("Song not found")
                }
            }
        }
    }
}