package com.example.wezzo.screens.home.viewModel

import com.example.wezzo.model.Root

sealed class ResponseStatus {
    object Loading : ResponseStatus()
    data class Success(val weatherResponse: Root) : ResponseStatus()
    data class Error(val message: String) : ResponseStatus()
}