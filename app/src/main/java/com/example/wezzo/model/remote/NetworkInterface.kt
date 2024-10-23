package com.example.wezzo.model.remote

import com.example.wezzo.model.POJOs.AirPollution
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Forecast
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NetworkInterface {
    @GET("data/2.5/weather")
    suspend fun getWeather(
        @Query("q") location: String,
        @Query("appid") appId: String
    ): Response<Current>

    @GET("data/2.5/weather")
    suspend fun getWeatherByLatAndLong(
        @Query("lat") lat:  Double,
        @Query("lon") long: Double,
        @Query("appid") appId: String
    ):Response<Current>

    @GET("data/2.5/forecast")
    suspend fun get5DaysForecast(
        @Query("lat") lat:  Double,
        @Query("lon") long: Double,
        @Query("appid") appId: String
    ):Response<Forecast>

    @GET("data/2.5/air_pollution")
    suspend fun getAirPollution(
        @Query("lat") lat:  Double,
        @Query("lon") long: Double,
        @Query("appid") appId: String
    ):Response<AirPollution>
}

/*
* https://openweathermap.org/api
* https://openweathermap.org/current
* https://openweathermap.org/weather-conditions
* https://openweathermap.org/forecast5
* https://openweathermap.org/api/air-pollution
* https://openweathermap.org/api/geocoding-api
* API: 58016d418401e5a0e8e9baef8d569514
* */
