package com.example.wezzo.model

import android.content.Context
import com.example.wezzo.model.POJOs.*
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDao
import com.example.wezzo.model.remote.NetworkInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeRepository(
    private val fakeRemoteDataSource: NetworkInterface,
    private val fakeLocalDataSource: dbCityDao
) : IRepository {

    override fun getWeatherByCity(location: String, appId: String): Flow<Response<Current>> = flow {
        emit(fakeRemoteDataSource.getWeather(location, appId))
    }

    override fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String): Flow<Response<Current>> = flow {
        emit(fakeRemoteDataSource.getWeatherByLatAndLong(lat, long, appId, "metric", "en"))
    }

    override fun get5DaysForecast(lat: Double, long: Double, appId: String): Flow<Response<Forecast>> = flow {
        emit(fakeRemoteDataSource.get5DaysForecast(lat, long, appId, "metric", "en"))
    }

    override fun getAirPollution(lat: Double, long: Double, appId: String): Flow<Response<AirPollution>> = flow {
        emit(fakeRemoteDataSource.getAirPollution(lat, long, appId))
    }

    override fun getCities(): Flow<List<dbCity>> = fakeLocalDataSource.getAllCities()

    override suspend fun insertCity(city: dbCity) = fakeLocalDataSource.insertCity(city)

    override suspend fun deleteCity(city: dbCity) = fakeLocalDataSource.deleteCity(city)

    override fun getCityAndCountry(lat: Double, lon: Double, appId: String): Flow<Response<List<GeocodingResponse>>> = flow {
        emit(fakeRemoteDataSource.getCityAndCountry(lat, lon, appId))
    }
}