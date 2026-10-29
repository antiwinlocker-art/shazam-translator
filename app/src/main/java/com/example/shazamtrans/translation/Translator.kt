package com.example.shazamtrans.translation

import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Translator {
    private val client = Translation.getClient(
        TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.RUSSIAN)
            .build()
    )

    suspend fun translate(text: String): String = suspendCancellableCoroutine { cont ->
        client.downloadModelIfNeeded()
            .addOnSuccessListener {
                client.translate(text)
                    .addOnSuccessListener { result -> if (cont.isActive) cont.resume(result) }
                    .addOnFailureListener { e -> if (cont.isActive) cont.resumeWithException(e) }
            }
            .addOnFailureListener { e -> if (cont.isActive) cont.resumeWithException(e) }
    }
}