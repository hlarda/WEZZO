package com.example.wezzo.screens.cities.manager.view

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieAnimationView
import com.example.wezzo.R
import com.example.wezzo.model.Repository
import com.example.wezzo.model.local.dbCity
import com.example.wezzo.model.local.dbCityDatabase
import com.example.wezzo.model.remote.NetworkService
import com.example.wezzo.viewModel.CityViewModel
import com.example.wezzo.viewModel.ViewModelFactory
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class LocationFragment : Fragment() {
    private val cityViewModel: CityViewModel by viewModels {
        ViewModelFactory(
            Repository(
                NetworkService.retrofitService,
                dbCityDatabase.getDatabase(requireContext()).cityDao()
            )
        )
    }
    private var columnCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location_list, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        val emptyLocationAnimation = view.findViewById<LottieAnimationView>(R.id.emptyLocationAnimation)

        // Set the adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = LocationRecyclerViewAdapter(mutableListOf())
        recyclerView.adapter = adapter

        lifecycleScope.launch {
            cityViewModel.getSavedLocations().collect { locations ->
                if (locations.isEmpty()) {
                    recyclerView.visibility = View.GONE
                    emptyLocationAnimation.visibility = View.VISIBLE
                } else {
                    recyclerView.visibility = View.VISIBLE
                    emptyLocationAnimation.visibility = View.GONE
                    adapter.updateLocations(locations)
                }
            }
        }

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val city = adapter.getCityAtPosition(position)
                showDeleteConfirmationDialog(city, adapter, position)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }

    companion object {
        const val ARG_COLUMN_COUNT = "column-count"

        @JvmStatic
        fun newInstance(columnCount: Int) =
            LocationFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    private fun showDeleteConfirmationDialog(city: dbCity, adapter: LocationRecyclerViewAdapter, position: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete ${city.name}, ${city.country}?")
            .setPositiveButton("Yes") { dialog, _ ->
                lifecycleScope.launch {
                    cityViewModel.deleteCity(city)
                    adapter.notifyItemRemoved(position)
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                adapter.notifyItemChanged(position)
                dialog.dismiss()
            }
            .create()
            .show()
    }
}