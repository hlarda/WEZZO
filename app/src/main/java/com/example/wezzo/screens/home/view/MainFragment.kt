package com.example.wezzo.screens.home.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.example.wezzo.databinding.FragmentMainBinding
import com.example.wezzo.model.POJOs.Coord
import com.example.wezzo.model.Repository
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.screens.home.viewModel.WeatherViewModel
import com.example.wezzo.screens.home.viewModel.ViewModelFactory
import com.google.android.material.tabs.TabLayoutMediator
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentLocation = Coord(lat = arguments?.getDouble("latitude") ?: 51.51, lon = arguments?.getDouble("longitude") ?: -0.13)

        lifecycleScope.launch {
            val savedLocations = viewModel.getSavedLocations()
            val locations = mutableListOf(currentLocation)
            locations.addAll(savedLocations)
            setupViewPager(locations)
        }
    }

    private fun setupViewPager(locations: List<Coord>) {
        val adapter = ViewPagerAdapter(requireActivity(), locations)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            // Optionally set tab text or icon here
        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateTitle(position)
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateTitle(binding.viewPager.currentItem)
    }

    private fun updateTitle(position: Int) {
        val fragment = childFragmentManager.findFragmentByTag("f$position") as? WeatherFragment
        fragment?.let {
            (activity as? AppCompatActivity)?.supportActionBar?.title = it.getCityName()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}