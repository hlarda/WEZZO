package com.example.wezzo.screens.cities.add.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wezzo.R
import com.example.wezzo.databinding.FragmentAddCityBinding
import com.google.android.gms.location.LocationServices

class AddCityFragment : Fragment() {

    private var _binding: FragmentAddCityBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddCityBinding.inflate(inflater, container, false)
        val view = binding.root

        binding.chooseFromMapButton.setOnClickListener {
            findNavController().navigate(R.id.action_addCityFragment_to_mapFragment)
        }

        binding.currentLocationButton.setOnClickListener {
            // Fetch the current location
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1
                )
            } else {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    val bundle = Bundle()
                    bundle.putDouble("latitude", location.latitude)
                    bundle.putDouble("longitude", location.longitude)
                    findNavController().navigate(R.id.action_SearchFragment_to_FirstFragment, bundle)
                }
            }

        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}