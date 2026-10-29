package com.example.shazamtrans.data

import com.example.shazamtrans.data.local.TrackDao
import com.example.shazamtrans.data.local.TrackEntity
import com.example.shazamtrans.data.remote.LyricsFetcher
import com.example.shazamtrans.data.remote.ShazamParser
import com.example.shazamtrans.translation.Translator

class TrackRepository(
    private val dao: TrackDao,
    private val shazamParser: ShazamParser,
    private val lyricsFetcher: LyricsFetcher,
    private val translator: Translator
) {
    suspend fun load(url: String, forceRefresh: Boolean = false): TrackEntity {
        if (!forceRefresh) dao.get(url)?.let { return it }

        val meta = shazamParser.parse(url)
        val original = lyricsFetcher.fetch(meta.artist, meta.title)
            ?: error("Текст песни не найден ни в одном источнике")

        val translated = translator.translate(original)

        val entity = TrackEntity(
            shazamUrl = url,
            artist = meta.artist,
            title = meta.title,
            coverUrl = meta.coverUrl,
            year = meta.year,
            originalLyrics = original,
            translatedLyrics = translated
        )
        dao.insert(entity)
        return entity
    }
}