package com.example.wezzo.model

import com.example.wezzo.model.POJOs.AirPollution
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Forecast
import com.example.wezzo.model.remote.NetworkInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class Repository(private val networkService: NetworkInterface) {
    fun getWeatherByCity(location: String, appId: String): Flow<Response<Current>> = flow {
        emit(networkService.getWeather(location, appId))
    }
    fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String): Flow<Response<Current>> = flow {
            emit(networkService.getWeatherByLatAndLong(lat, long, appId))
    }
    fun get5DaysForecast(lat: Double, long: Double, appId: String): Flow<Response<Forecast>> = flow {
        emit(networkService.get5DaysForecast(lat, long, appId))
    }
    fun getAirPollution(lat: Double, long: Double, appId: String): Flow<Response<AirPollution>> = flow {
        emit(networkService.getAirPollution(lat, long, appId))
    }
}
