package com.example.skybeat.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val sId: String,
    val title: String,
    val artist: String,
    val file: String,
    val bannerUrl: String? = null,
    var download: Boolean = false,
    var playList: Boolean = false,
) : Parcelable

data class SongResponse(
    val songs: List<Song>
)


