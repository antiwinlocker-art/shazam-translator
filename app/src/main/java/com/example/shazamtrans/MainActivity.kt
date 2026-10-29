package com.example.shazamtrans

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shazamtrans.ui.TranslateScreen
import com.example.shazamtrans.ui.TranslateViewModel
import com.example.shazamtrans.ui.theme.ShazamTransTheme

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "ShazamShare"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val raw = intent?.getStringExtra(Intent.EXTRA_TEXT)
        Log.d(TAG, "RAW EXTRA_TEXT = «${raw ?: "<null>"}»")

        val sharedUrl = extractShazamUrl(raw)
        Log.d(TAG, "Extracted URL  = «${sharedUrl ?: "<null>"}»")

        val app = application as App

        setContent {
            ShazamTransTheme {
                val vm: TranslateViewModel = viewModel(
                    factory = TranslateViewModel.Factory(app.repository)
                )
                TranslateScreen(vm)
                LaunchedEffect(sharedUrl) { vm.load(sharedUrl) }
            }
        }
    }

    /**
     * Достаёт из произвольного текста первую http(s)-ссылку и приводит
     * shazam:// к https://www.shazam.com/...
     *
     * Shazam обычно присылает:
     *   "Название - Исполнитель\nhttps://www.shazam.com/track/..."
     * но бывает и просто ссылка, или shazam://.
     */
    private fun extractShazamUrl(raw: String?): String? {
        if (raw.isNullOrBlank()) return null

        // 1. Первая http(s)-ссылка в любом месте строки
        val httpRegex = Regex("""https?://\S+""", RegexOption.IGNORE_CASE)
        httpRegex.find(raw)?.value?.let { url ->
            // подрезаем возможные хвостовые знаки препинания
            return url.trimEnd('.', ',', ')', ']', '«', '»', '"')
        }

        // 2. shazam://... → https://www.shazam.com/...
        val shazamRegex = Regex("""shazam(?:\.[a-z]+)?://(\S+)""", RegexOption.IGNORE_CASE)
        shazamRegex.find(raw)?.groupValues?.get(1)?.let { path ->
            return "https://www.shazam.com/" + path.trimStart('/').trimEnd('.', ',', ')', ']')
        }

        // 3. Ничего не нашли
        return null
    }
}
