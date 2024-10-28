package com.example.wezzo.screens.cities.manager.view

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.wezzo.databinding.CardCityBinding
import com.example.wezzo.model.local.dbCity

class LocationRecyclerViewAdapter(
    private var values: MutableList<dbCity>
) : RecyclerView.Adapter<LocationRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            CardCityBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.cityTextView.text = item.name
        holder.countryTextView.text = item.country
    }

    override fun getItemCount(): Int = values.size

    fun updateLocations(newValues: List<dbCity>) {
        values = newValues.toMutableList()
        notifyDataSetChanged()
    }

    fun getCityAtPosition(position: Int): dbCity {
        return values[position]
    }

    inner class ViewHolder(binding: CardCityBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val cityTextView: TextView = binding.cityTextView
        val countryTextView: TextView = binding.countryTextView
    }
}