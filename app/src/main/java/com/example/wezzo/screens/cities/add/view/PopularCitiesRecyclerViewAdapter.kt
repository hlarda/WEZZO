package com.example.wezzo.screens.cities.add.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.wezzo.R

import com.example.wezzo.screens.cities.add.view.placeholder.PlaceholderContent.PlaceholderItem
import com.example.wezzo.databinding.FragmentAddCityBinding

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class PopularCitiesRecyclerViewAdapter(
    private val values: List<PlaceholderItem>
) : RecyclerView.Adapter<PopularCitiesRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentAddCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(binding: FragmentAddCityBinding) :
        RecyclerView.ViewHolder(binding.root) {


    }

}