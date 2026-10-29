package com.example.shazamtrans

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shazamtrans.ui.TranslateScreen
import com.example.shazamtrans.ui.TranslateViewModel
import com.example.shazamtrans.ui.theme.ShazamTransTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sharedUrl = intent?.getStringExtra(Intent.EXTRA_TEXT)
            ?.trim()
            ?.substringBefore("?")

        val app = application as App

        setContent {
            ShazamTransTheme {
                val vm: TranslateViewModel = viewModel(
                    factory = TranslateViewModel.Factory(app.repository)
                )
                TranslateScreen(vm)
                // Автозапуск при открытии через «Поделиться»
                androidx.compose.runtime.LaunchedEffect(sharedUrl) {
                    vm.load(sharedUrl)
                }
            }
        }
    }
}