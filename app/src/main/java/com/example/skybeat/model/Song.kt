package com.example.skybeat.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Song(
    val sId: String = "",
    val title: String = "",
    val artist: String = "",
    val file: String = "",
    val bannerUrl: String? = null
) : Parcelable

data class SongResponse(
    val songs: List<Song> = emptyList()
)
