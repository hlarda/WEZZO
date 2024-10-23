package com.example.wezzo.screens.cities.add.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.wezzo.R
import com.example.wezzo.databinding.FragmentAddCityBinding

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
            findNavController().navigate(R.id.action_SearchFragment_to_FirstFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}