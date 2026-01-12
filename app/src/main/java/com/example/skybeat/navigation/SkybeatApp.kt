package com.example.skybeat.navigation

import android.net.Uri
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.skybeat.screen.AdminScreen
import com.example.skybeat.screen.DownloadsScreen
import com.example.skybeat.screen.HomeScreen
import com.example.skybeat.screen.LibraryScreen
import com.example.skybeat.screen.SearchScreen
import com.example.skybeat.screen.SongPlaying
import com.example.skybeat.screen.SplashScreen
import com.example.skybeat.viewModel.PlaybackViewModel
import com.example.taskdesk.screen.logingScreen.ForgetPasswordScreen
import com.example.taskdesk.screen.logingScreen.LoginScreen
import com.example.taskdesk.screen.logingScreen.SignUpScreen

@androidx.annotation.OptIn(UnstableApi::class)
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
            composable("Admin") { AdminScreen(navController=navController)}
            composable("Login") { LoginScreen(navController) }
            composable("SignUp") { SignUpScreen(navController) }
            composable("ForgetPass") { ForgetPasswordScreen(navController) }

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
                    SongPlaying(song = song, navController = navController)
                } else {
                    Text("Song not found")
                }
            }
        }
    }
}