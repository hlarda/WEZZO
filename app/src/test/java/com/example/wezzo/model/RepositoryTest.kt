package com.example.wezzo.model

import android.content.Context
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.remote.FakeRemoteDataSource
import junit.framework.TestCase
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import retrofit2.Response

class RepositoryTest {

    private lateinit var repository: Repository
    private lateinit var fakeNetworkService: FakeRemoteDataSource
    private lateinit var fakeCityDatabase: FakeLocalDataSource

    @Before
    fun setup() {
        fakeNetworkService = FakeRemoteDataSource()
        fakeCityDatabase = FakeLocalDataSource()

        // Use the fake data sources for repository
        repository = Repository(fakeNetworkService, fakeCityDatabase, mockContext())
    }

    private fun mockContext(): Context {
        // This is just a mock context, used only for initializing the repository
        return mock(Context::class.java)
    }

    @Test
    fun testGetWeatherByCity() = runBlocking {
        val response = repository.getWeatherByCity("London", "58016d418401e5a0e8e9baef8d569514").toList()
        assertTrue(response.isNotEmpty())
    }

    @Test
    fun testGetCityAndCountry() = runBlocking {
        // Explicitly specify the type for the flow
        val response = repository.getCityAndCountry(51.509, -0.1180, "58016d418401e5a0e8e9baef8d569514").toList()
        TestCase.assertEquals(200, response.first().code())
        TestCase.assertEquals("City of Westminster", response.first().body()?.get(0)?.name)
    }


}
