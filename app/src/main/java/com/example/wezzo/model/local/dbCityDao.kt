package com.example.wezzo.model.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface dbCityDao {
    @Query("SELECT * FROM cities")
    fun getAllCities(): Flow<List<dbCity>>

    @Insert (onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: dbCity)

    @Delete
    suspend fun deleteCity(city: dbCity)
}