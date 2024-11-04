package com.example.wezzo.model

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.wezzo.model.POJOs.AirPollution
import com.example.wezzo.model.POJOs.Current
import com.example.wezzo.model.POJOs.Forecast
import com.example.wezzo.model.POJOs.GeocodingResponse
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDao
import com.example.wezzo.model.remote.NetworkInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class Repository(
    private val networkService: NetworkInterface,
    private val cityDatabase: dbCityDao,
    context: Context
) : IRepository {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private val units: String = sharedPreferences.getString("units", "metric") ?: "metric"
    private val language: String = sharedPreferences.getString("language", "en") ?: "en"

    override fun getWeatherByCity(location: String, appId: String): Flow<Response<Current>> = flow {
        emit(networkService.getWeather(location, appId))
    }

    override fun getWeatherByLatAndLong(lat: Double, long: Double, appId: String): Flow<Response<Current>> = flow {
        emit(networkService.getWeatherByLatAndLong(lat, long, appId, units, language))
    }

    override fun get5DaysForecast(lat: Double, long: Double, appId: String): Flow<Response<Forecast>> = flow {
        emit(networkService.get5DaysForecast(lat, long, appId, units, language))
    }

    override fun getAirPollution(lat: Double, long: Double, appId: String): Flow<Response<AirPollution>> = flow {
        emit(networkService.getAirPollution(lat, long, appId))
    }

    override fun getCities(): Flow<List<dbCity>> = cityDatabase.getAllCities()

    override suspend fun insertCity(city: dbCity) = cityDatabase.insertCity(city)

    override suspend fun deleteCity(city: dbCity) = cityDatabase.deleteCity(city)

    override fun getCityAndCountry(lat: Double, lon: Double, appId: String): Flow<Response<List<GeocodingResponse>>> = flow {
        emit(networkService.getCityAndCountry(lat, lon, appId))
    }
}
