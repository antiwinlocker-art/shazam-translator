package com.example.shazamtrans.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

class ShazamParser(private val client: OkHttpClient) {

    data class TrackMeta(
        val artist: String,
        val title: String,
        val coverUrl: String?,
        val year: String?
    )

    suspend fun parse(url: String): TrackMeta = withContext(Dispatchers.IO) {
        val html = client.newCall(Request.Builder().url(url).build())
            .execute().use { it.body?.string() ?: error("Не удалось загрузить страницу") }

        val doc = Jsoup.parse(html)
        val ogTitle = doc.selectFirst("meta[property=og:title]")?.attr("content").orEmpty()
        val cover   = doc.selectFirst("meta[property=og:image]")?.attr("content")
        val year    = doc.selectFirst("meta[property=music:release_date]")
            ?.attr("content")?.take(4)

        val (title, artist) = splitTitle(ogTitle)
        TrackMeta(artist, title, cover, year)
    }

    private fun splitTitle(og: String): Pair<String, String> {
        val by = og.indexOf(" by ")
        if (by > 0) return og.substring(0, by).trim() to og.substring(by + 4).trim()
        val dash = og.lastIndexOf(" - ")
        if (dash > 0) return og.substring(0, dash).trim() to og.substring(dash + 3).trim()
        return og.trim() to ""
    }
}