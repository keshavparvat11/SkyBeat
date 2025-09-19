package com.example.skybeat.network

import android.telecom.Call
import com.example.skybeat.model.Song
import com.example.skybeat.model.SongResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("songs.json")
    suspend fun getSongs(): List<Song>
}