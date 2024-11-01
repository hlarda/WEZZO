package com.example.wezzo.screens.cities.manager.view

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.example.wezzo.databinding.CardCityBinding
import com.example.wezzo.model.local.dbCity

class LocationRecyclerViewAdapter(
    private var values: MutableList<dbCity>,
    private val onItemClick: (Int) -> Unit
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
        holder.itemView.setOnClickListener{
            onItemClick(position)
        }
    }

    override fun getItemCount(): Int = values.size

    @SuppressLint("NotifyDataSetChanged")
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