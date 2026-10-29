package com.example.shazamtrans.ui

import com.example.shazamtrans.data.local.TrackEntity

sealed interface UiState {
    data class Loading(val message: String) : UiState
    data class Error(val message: String) : UiState
    data class Success(val track: TrackEntity) : UiState
    data object Idle : UiState
}