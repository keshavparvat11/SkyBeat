package com.example.skybeat

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.skybeat.navigation.SkybeatApp
import com.example.skybeat.ui.theme.SkybeatTheme
import com.google.firebase.auth.FirebaseAuth


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }

        // Hide status bar, navigation bar, and enable immersive mode
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            val prefs = remember { getSharedPreferences("skybeat_settings", MODE_PRIVATE) }
            var isDarkTheme by remember { mutableStateOf(prefs.getBoolean("dark_theme", false)) }
            var textScale by remember { mutableFloatStateOf(prefs.getFloat("text_scale", 1f)) }
            var notificationsEnabled by remember {
                mutableStateOf(prefs.getBoolean("notifications_enabled", true))
            }

            val baseDensity = LocalDensity.current
            val scaledDensity = Density(
                density = baseDensity.density,
                fontScale = textScale
            )

            CompositionLocalProvider(LocalDensity provides scaledDensity) {
                SkybeatTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SkybeatApp(
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isEnabled ->
                                isDarkTheme = isEnabled
                                prefs.edit().putBoolean("dark_theme", isEnabled).apply()
                            },
                            textScale = textScale,
                            onTextScaleChange = { scale ->
                                textScale = scale
                                prefs.edit().putFloat("text_scale", scale).apply()
                            },
                            notificationsEnabled = notificationsEnabled,
                            onNotificationsChange = { isEnabled ->
                                notificationsEnabled = isEnabled
                                prefs.edit().putBoolean("notifications_enabled", isEnabled).apply()
                            },
                            onLogout = {
                                FirebaseAuth.getInstance().signOut()
                            }
                        )
                    }
                }
            }
        }
    }
}
