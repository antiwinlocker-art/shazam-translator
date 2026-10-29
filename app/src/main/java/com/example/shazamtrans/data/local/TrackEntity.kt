package com.example.shazamtrans.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey val shazamUrl: String,
    val artist: String,
    val title: String,
    val coverUrl: String?,
    val year: String?,
    val originalLyrics: String,
    val translatedLyrics: String,
    val cachedAt: Long = System.currentTimeMillis()
)