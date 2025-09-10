package com.example.skybeat.model

data class Song(
    val title: String,
    val artist: String,
    val file: String
)

data class SongResponse(
    val songs: List<Song>
)


