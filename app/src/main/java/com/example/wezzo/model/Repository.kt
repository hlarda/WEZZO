package com.example.wezzo.model

import com.example.wezzo.model.remote.NetworkInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class Repository(private val networkService: NetworkInterface) {
    fun fetchWeather(location: String, appId: String): Flow<Response<Root>> = flow {
        emit(networkService.getWeather(location, appId))
    }
}
