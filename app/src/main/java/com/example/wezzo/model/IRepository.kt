package com.example.wezzo.model

import com.example.wezzo.model.POJOs.AirPollution
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Forecast
import com.example.wezzo.model.POJOs.GeocodingResponse
import com.example.wezzo.model.local.dbCity
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IRepository {
    fun getWeatherByCity(location: String, appId: String): Flow<Response<Current>>
    fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String): Flow<Response<Current>>
    fun get5DaysForecast(lat: Double, long: Double, appId: String): Flow<Response<Forecast>>
    fun getAirPollution(lat: Double, long: Double, appId: String): Flow<Response<AirPollution>>
    fun getCities(): Flow<List<dbCity>>
    suspend fun insertCity(city: dbCity)
    suspend fun deleteCity(city: dbCity)
    fun getCityAndCountry(lat: Double, lon: Double, appId: String): Flow<Response<List<GeocodingResponse>>>
}
