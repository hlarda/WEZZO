package com.example.wezzo.screens.map.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.wezzo.databinding.FragmentMapBinding
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDatabase
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.viewModel.CityViewModel
import com.example.wezzo.viewModel.ViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.MapEventsOverlay

class MapFragment : Fragment(), MapEventsReceiver {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private var selectedLocation: Pair<Double, Double>? = null
    private var marker: Marker? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val cityViewModel: CityViewModel by viewModels {
        ViewModelFactory(
            Repository(
                NetworkService.retrofitService,
                dbCityDatabase.getDatabase(requireContext()).cityDao(),
                requireContext()
            ),
            requireContext()
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().userAgentValue = requireActivity().packageName
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.mapView.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK)
        binding.mapView.setBuiltInZoomControls(true)
        binding.mapView.setMultiTouchControls(true)

        val mapEventsOverlay = MapEventsOverlay(this)
        binding.mapView.overlays.add(mapEventsOverlay)

        binding.confirmButton.setOnClickListener {
            selectedLocation?.let { location ->
                cityViewModel.getCityAndCountry(location.first, location.second, "58016d418401e5a0e8e9baef8d569514")
            }
        }

        lifecycleScope.launch {
            cityViewModel.geocodingResponseStatus.collect { responseStatus ->
                when (responseStatus) {
                    is GeocodingResponseStatus.Loading -> {
                        // Handle loading state if needed
                    }
                    is GeocodingResponseStatus.Success -> {
                        val city = responseStatus.geocodingResponse.firstOrNull()?.name ?: "Unknown"
                        val country = responseStatus.geocodingResponse.firstOrNull()?.country ?: "Unknown"
                        saveCity(city, country)
                    }
                    is GeocodingResponseStatus.Error -> {
                        Log.e("MapFragment", "Error: ${responseStatus.message}")
                    }
                }
            }
        }

        // Check location permissions and get current location
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        } else {
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val geoPoint = GeoPoint(it.latitude, it.longitude)
                binding.mapView.controller.setZoom(18.0)
                binding.mapView.controller.setCenter(geoPoint)
                addMarker(it.latitude, it.longitude)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
        p?.let {
            selectedLocation = Pair(it.latitude, it.longitude)
            addMarker(it.latitude, it.longitude)
        }
        return true
    }

    override fun longPressHelper(p: GeoPoint?): Boolean {
        return false
    }

    private fun addMarker(latitude: Double, longitude: Double) {
        marker?.let {
            binding.mapView.overlays.remove(it)
        }

        marker = Marker(binding.mapView).apply {
            position = GeoPoint(latitude, longitude)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        }

        binding.mapView.overlays.add(marker)
        binding.mapView.invalidate()
    }

    private fun saveCity(city: String, country: String) {
        val newCity = dbCity(name = city, country = country, latitude = selectedLocation?.first ?: 0.0, longitude = selectedLocation?.second ?: 0.0)
        lifecycleScope.launch {
            cityViewModel.insertCity(newCity)
        }
    }
}