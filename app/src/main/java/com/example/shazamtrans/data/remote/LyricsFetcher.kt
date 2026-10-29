package com.example.shazamtrans.data.remote

import com.example.shazamtrans.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import org.jsoup.Jsoup
import java.net.URLEncoder

class LyricsFetcher(private val client: OkHttpClient) {

    suspend fun fetch(artist: String, title: String): String? = withContext(Dispatchers.IO) {
        fetchLyricsOvh(artist, title) ?: fetchGenius(artist, title)
    }

    private fun fetchLyricsOvh(artist: String, title: String): String? = try {
        val url = "https://api.lyrics.ovh/v1/" +
                URLEncoder.encode(artist, "UTF-8") + "/" +
                URLEncoder.encode(title, "UTF-8")
        val body = client.newCall(Request.Builder().url(url).build())
            .execute().use { if (!it.isSuccessful) return null; it.body?.string() }
        body?.let { JSONObject(it).optString("lyrics").trim().ifBlank { null } }
    } catch (e: Exception) { null }

    private fun fetchGenius(artist: String, title: String): String? = try {
        val token = BuildConfig.GENIUS_TOKEN
        if (token.isBlank()) return null

        val query = URLEncoder.encode("$artist $title", "UTF-8")
        val searchBody = client.newCall(
            Request.Builder()
                .url("https://api.genius.com/search?q=$query")
                .header("Authorization", "Bearer $token")
                .build()
        ).execute().use { if (!it.isSuccessful) return null; it.body?.string() } ?: return null

        val hits = JSONObject(searchBody)
            .getJSONObject("response").getJSONArray("hits")
        if (hits.length() == 0) return null

        val path = hits.getJSONObject(0)
            .getJSONObject("result").getString("path")

        val html = client.newCall(Request.Builder().url("https://genius.com$path").build())
            .execute().use { it.body?.string() } ?: return null

        val containers = Jsoup.parse(html).select("[data-lyrics-container=true]")
        if (containers.isEmpty()) return null

        containers.joinToString("\n\n") { it.wholeText().trim() }.trim()
    } catch (e: Exception) { null }
}