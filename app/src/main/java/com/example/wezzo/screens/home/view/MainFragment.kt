package com.example.wezzo.screens.home.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wezzo.databinding.FragmentMainBinding
import com.example.wezzo.model.Repository
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.screens.home.viewModel.ResponseStatus
import com.example.wezzo.screens.home.viewModel.WeatherViewModel
import com.example.wezzo.screens.home.viewModel.ViewModelFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
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
            viewModel.responseStatus.collect { responseStatus ->
                when(responseStatus) {
                    is ResponseStatus.Loading -> {
                        binding.textviewFirst.text = "Loading..."
                    }
                    is ResponseStatus.Success -> {
                        binding.textviewFirst.text = responseStatus.weatherResponse.toString()
                    }
                    is ResponseStatus.Error -> {
                        binding.textviewFirst.text = responseStatus.message
                    }
                }
            }
        }
        viewModel.fetchWeather("London,uk", "58016d418401e5a0e8e9baef8d569514")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}