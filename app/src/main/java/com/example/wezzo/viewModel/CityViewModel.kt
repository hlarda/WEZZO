package com.example.wezzo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wezzo.model.POJOs.GeocodingResponse
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.screens.map.view.GeocodingResponseStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class CityViewModel(private val repository: Repository) : ViewModel() {

    private val _geocodingResponseStatus = MutableStateFlow<GeocodingResponseStatus>(GeocodingResponseStatus.Loading)
    val geocodingResponseStatus: StateFlow<GeocodingResponseStatus> = _geocodingResponseStatus

    fun getCityAndCountry(lat: Double, lon: Double, appId: String) {
        viewModelScope.launch {
            _geocodingResponseStatus.value = GeocodingResponseStatus.Loading
            try {
                repository.getCityAndCountry(lat, lon, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _geocodingResponseStatus.value = GeocodingResponseStatus.Success(it)
                        } ?: run {
                            _geocodingResponseStatus.value = GeocodingResponseStatus.Error("No data")
                        }
                    } else {
                        _geocodingResponseStatus.value = GeocodingResponseStatus.Error(response.message())
                    }
                }
            } catch (e: Exception) {
                _geocodingResponseStatus.value = GeocodingResponseStatus.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getSavedLocations() = repository.getCities()

    fun insertCity(city: dbCity) {
        viewModelScope.launch {
            repository.insertCity(city)
        }
    }

    fun deleteCity(city: dbCity) {
        viewModelScope.launch {
            repository.deleteCity(city)
        }
    }
}