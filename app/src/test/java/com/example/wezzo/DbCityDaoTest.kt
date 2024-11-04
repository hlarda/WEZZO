package com.example.wezzo

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDao
import com.example.wezzo.model.local.dbCityDatabase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class DbCityDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: dbCityDatabase
    private lateinit var cityDao: dbCityDao

    @Before
    fun setup() {
        // Initialize in-memory database
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            dbCityDatabase::class.java
        ).allowMainThreadQueries().build()

        cityDao = database.cityDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun removeCityByName_andCheckIfItIsDeleted() = runTest {
        // Given a City
        val city = dbCity(
            name = "Test City",
            latitude = 10.0,
            longitude = 20.0,
            country = "Test Country")
        cityDao.insertCity(city)

        // When removing the city by name
        cityDao.deleteCity(city)

        // Then
        val cities = cityDao.getAllCities()
        assertThat(cities.first().isEmpty(), `is`(true))
    }

    @Test
    fun getAllCities_isEmpty() = runTest {
        // Given no cities in the database

        // When getting all cities
        val cities = cityDao.getAllCities()

        // Then
        assertThat(cities.first().isEmpty(), `is`(true))
    }

}