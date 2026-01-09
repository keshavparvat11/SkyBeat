package com.example.skybeat.component


import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment

object DownloadHelper {

    fun downloadSong(
        context: Context,
        songUrl: String,
        songTitle: String
    ) {
        val request = DownloadManager.Request(Uri.parse(songUrl))
            .setTitle(songTitle)
            .setDescription("Downloading song")
            .setNotificationVisibility(
                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
            )
            .setDestinationInExternalPublicDir(
                Environment.DIRECTORY_MUSIC,
                "SkyBeat/$songTitle.mp3"
            )
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        downloadManager.enqueue(request)
    }
}
