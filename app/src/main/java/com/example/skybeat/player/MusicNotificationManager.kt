package com.example.skybeat.player


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class MusicNotificationManager(
    context: Context,
    player: Player,
    mediaSession: MediaSession
) {

    private val notificationManager: PlayerNotificationManager =
        PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            CHANNEL_ID
        )
            .setMediaDescriptionAdapter(
                DescriptionAdapter(context, mediaSession)
            )
            .build()

    init {
        createNotificationChannel(context)
        notificationManager.setPlayer(player)
        notificationManager.setUseNextAction(true)
        notificationManager.setUsePreviousAction(true)
        notificationManager.setUsePlayPauseActions(true)
    }


    fun hide() {
        notificationManager.setPlayer(null)
    }
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SkyBeat Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Music playback controls"
                setShowBadge(false)
            }

            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "skybeat_music_channel"
        const val NOTIFICATION_ID = 1001
    }
}
