package com.example.wezzo.screens.home.view

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wezzo.databinding.FragmentMainBinding
import com.example.wezzo.model.Repository
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.screens.home.viewModel.*
import kotlinx.coroutines.launch

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!


    private val viewModel: WeatherViewModel by viewModels {
        ViewModelFactory(
            Repository(NetworkService.retrofitService)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            viewModel.weatherResponseStatus.collect { responseStatus ->
                when(responseStatus) {
                    is WeatherResponseStatus.Loading -> {
                        binding.textviewWeather.text = "Loading..."
                    }
                    is WeatherResponseStatus.Success -> {
                        binding.textviewWeather.text = responseStatus.weatherResponse.toString()
                        Log.i(TAG, "Response: "+responseStatus.weatherResponse.toString())
                    }
                    is WeatherResponseStatus.Error -> {
                        binding.textviewWeather.text = responseStatus.message
                    }
                }
            }
        }
        viewModel.getWeatherByCity("London,uk", "58016d418401e5a0e8e9baef8d569514")

        lifecycleScope.launch {
            viewModel.airPollutionResponseStatus.collect { responseStatus ->
                when(responseStatus) {
                    is AirPollutionResponseStatus.Loading -> {
                        binding.textviewAirPollution.text = "Loading..."
                    }
                    is AirPollutionResponseStatus.Success -> {
                        binding.textviewAirPollution.text = responseStatus.airPollutionResponse.toString()
                        Log.i(TAG, "Response: "+responseStatus.airPollutionResponse.toString())
                    }
                    is AirPollutionResponseStatus.Error -> {
                        binding.textviewAirPollution.text = responseStatus.message
                        Log.i(TAG, "Failure: " + responseStatus.message)
                    }
                }
            }
        }
        viewModel.getAirPollution(51.51, -0.13, "58016d418401e5a0e8e9baef8d569514")

        lifecycleScope.launch {
            viewModel.forecastResponseStatus.collect { responseStatus ->
                when(responseStatus) {
                    is ForecastResponseStatus.Loading -> {
                        binding.textviewForecast.text = "Loading..."
                    }
                    is ForecastResponseStatus.Success -> {
                        binding.textviewForecast.text = responseStatus.forecastResponse.toString()
                        Log.i(TAG, "Response: "+responseStatus.forecastResponse.toString())
                    }
                    is ForecastResponseStatus.Error -> {
                        binding.textviewForecast.text = responseStatus.message
                    }
                }
            }
        }
        viewModel.get5DaysForecast(51.51, -0.13, "58016d418401e5a0e8e9baef8d569514")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}