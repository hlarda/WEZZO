package com.example.wezzo.screens.home.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wezzo.model.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository) : ViewModel() {

    private val _responseStatus = MutableStateFlow<ResponseStatus>(ResponseStatus.Loading)
    val responseStatus: StateFlow<ResponseStatus> = _responseStatus

    fun fetchWeather(location: String, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _responseStatus.value = ResponseStatus.Loading
            try {
                repository.fetchWeather(location, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _responseStatus.value = ResponseStatus.Success(it)
                        }
                    } else {
                        _responseStatus.value = ResponseStatus.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _responseStatus.value = ResponseStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
