package com.example.shazamtrans

import android.app.Application
import com.example.shazamtrans.data.TrackRepository
import com.example.shazamtrans.data.local.AppDatabase
import com.example.shazamtrans.data.remote.LyricsFetcher
import com.example.shazamtrans.data.remote.ShazamParser
import com.example.shazamtrans.translation.Translator
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class App : Application() {
    lateinit var repository: TrackRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val http = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(45, TimeUnit.SECONDS)
            .build()

        repository = TrackRepository(
            dao = AppDatabase.get(this).trackDao(),
            shazamParser = ShazamParser(http),
            lyricsFetcher = LyricsFetcher(http),
            translator = Translator()
        )
    }
}