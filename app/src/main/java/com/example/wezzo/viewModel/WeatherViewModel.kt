package com.example.wezzo.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.screens.home.view.AirPollutionResponseStatus
import com.example.wezzo.screens.home.view.ForecastResponseStatus
import com.example.wezzo.screens.home.view.WeatherResponseStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(private val repository: Repository) : ViewModel() {

    private val _weatherResponseStatus = MutableStateFlow<WeatherResponseStatus>(
        WeatherResponseStatus.Loading
    )
    val weatherResponseStatus: StateFlow<WeatherResponseStatus> = _weatherResponseStatus

    private val _airPollutionResponseStatus = MutableStateFlow<AirPollutionResponseStatus>(
        AirPollutionResponseStatus.Loading
    )
    val airPollutionResponseStatus: StateFlow<AirPollutionResponseStatus> = _airPollutionResponseStatus

    private val _forecastResponseStatus = MutableStateFlow<ForecastResponseStatus>(
        ForecastResponseStatus.Loading
    )
    val forecastResponseStatus: StateFlow<ForecastResponseStatus> = _forecastResponseStatus

    fun getWeatherByCity(city: String, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherResponseStatus.value = WeatherResponseStatus.Loading
            try {
                repository.getWeatherByCity(city, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherResponseStatus.value = WeatherResponseStatus.Success(it)
                        }
                    } else {
                        _weatherResponseStatus.value =
                            WeatherResponseStatus.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _weatherResponseStatus.value =
                    WeatherResponseStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _weatherResponseStatus.value = WeatherResponseStatus.Loading
            try {
                repository.getWeatherByLatAndLong(lat, long, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _weatherResponseStatus.value = WeatherResponseStatus.Success(it)
                        }
                    } else {
                        _weatherResponseStatus.value =
                            WeatherResponseStatus.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _weatherResponseStatus.value =
                    WeatherResponseStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun get5DaysForecast(lat: Double, long: Double, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _forecastResponseStatus.value = ForecastResponseStatus.Loading
            try {
                repository.get5DaysForecast(lat, long, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _forecastResponseStatus.value = ForecastResponseStatus.Success(it)
                        }
                    } else {
                        _forecastResponseStatus.value =
                            ForecastResponseStatus.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _forecastResponseStatus.value =
                    ForecastResponseStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun getAirPollution(lat: Double, long: Double, appId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _airPollutionResponseStatus.value = AirPollutionResponseStatus.Loading
            try {
                repository.getAirPollution(lat, long, appId).collect { response ->
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _airPollutionResponseStatus.value =
                                AirPollutionResponseStatus.Success(it)
                        }
                    } else {
                        _airPollutionResponseStatus.value =
                            AirPollutionResponseStatus.Error("Error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _airPollutionResponseStatus.value =
                    AirPollutionResponseStatus.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun getSavedLocations() = repository.getCities();

}