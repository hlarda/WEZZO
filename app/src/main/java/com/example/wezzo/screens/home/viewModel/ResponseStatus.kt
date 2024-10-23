package com.example.wezzo.screens.home.viewModel

import com.example.wezzo.model.POJOs.AirPollution
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Forecast

sealed class WeatherResponseStatus {
    data object Loading : WeatherResponseStatus()
    data class Success(val weatherResponse: Current) : WeatherResponseStatus()
    data class Error(val message: String) : WeatherResponseStatus()
}
sealed class AirPollutionResponseStatus {
    data object Loading : AirPollutionResponseStatus()
    data class Success(val airPollutionResponse: AirPollution) : AirPollutionResponseStatus()
    data class Error(val message: String) : AirPollutionResponseStatus()
}
sealed class ForecastResponseStatus {
    data object Loading : ForecastResponseStatus()
    data class Success(val forecastResponse: Forecast) : ForecastResponseStatus()
    data class Error(val message: String) : ForecastResponseStatus()
}

