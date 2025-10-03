package com.example.skybeat.model

import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Song(
    val sId: String,
    val title: String,
    val artist: String,
    val file: String,
    val bannerUrl: String? = null
) : Parcelable

data class SongResponse(
    val songs: List<Song>
)


