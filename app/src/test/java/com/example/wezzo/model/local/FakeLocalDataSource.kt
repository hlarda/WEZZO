package com.example.wezzo.model

import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeLocalDataSource : dbCityDao {

    var cities = mutableListOf<dbCity>()

    override fun getAllCities(): Flow<List<dbCity>> = flow {
        emit(cities)
    }

    override suspend fun insertCity(city: dbCity) {
        cities.add(city)
    }

    override suspend fun deleteCity(city: dbCity) {
        cities.remove(city)
    }
}