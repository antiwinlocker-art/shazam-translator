package com.example.shazamtrans.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.shazamtrans.data.TrackRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TranslateViewModel(private val repo: TrackRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState>(UiState.Idle)
    val state: StateFlow<UiState> = _state.asStateFlow()

    private var currentUrl: String? = null

    fun load(url: String?, forceRefresh: Boolean = false) {
        if (url.isNullOrBlank()) {
            _state.value = UiState.Error(
                "Ссылка не получена.\nОткрой Shazam → трек → Поделиться → Shazam Переводчик"
            )
            return
        }
        currentUrl = url
        viewModelScope.launch {
            _state.value = UiState.Loading("Загружаю страницу Shazam...")
            try {
                _state.value = UiState.Loading("Ищу текст песни...")
                val track = repo.load(url, forceRefresh)
                _state.value = UiState.Success(track)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Неизвестная ошибка")
            }
        }
    }

    fun retry() {
        currentUrl?.let { load(it, forceRefresh = true) }
    }

    class Factory(private val repo: TrackRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            TranslateViewModel(repo) as T
    }
}