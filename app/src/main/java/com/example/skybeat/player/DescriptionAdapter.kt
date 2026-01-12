package com.example.skybeat.player

import android.content.Context
import android.graphics.Bitmap
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager

@UnstableApi
class DescriptionAdapter(
    private val context: Context,
    private val mediaSession: MediaSession
) : PlayerNotificationManager.MediaDescriptionAdapter {

    override fun getCurrentContentTitle(player: androidx.media3.common.Player): CharSequence {
        return mediaSession.player.mediaMetadata.title ?: "SkyBeat"
    }

    override fun getCurrentContentText(player: androidx.media3.common.Player): CharSequence? {
        return mediaSession.player.mediaMetadata.artist
    }

    override fun getCurrentLargeIcon(
        player: androidx.media3.common.Player,
        callback: PlayerNotificationManager.BitmapCallback
    ): Bitmap? {
        return null // you can load album art later
    }

    override fun createCurrentContentIntent(player: androidx.media3.common.Player) =
        null
}
