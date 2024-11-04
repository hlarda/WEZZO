package com.example.wezzo.screens.cities.add.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wezzo.databinding.CardCitySuggestionBinding
import com.example.wezzo.model.local.dbCity

class CitySuggestionsAdapter(
    private var suggestions: List<dbCity>,
    private val onItemClick: (dbCity) -> Unit
) : RecyclerView.Adapter<CitySuggestionsAdapter.CitySuggestionViewHolder>() {

    inner class CitySuggestionViewHolder(private val binding: CardCitySuggestionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(suggestion: dbCity) {
            binding.cityName.text = suggestion.name
            binding.root.setOnClickListener {
                onItemClick(suggestion)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CitySuggestionViewHolder {
        val binding = CardCitySuggestionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CitySuggestionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CitySuggestionViewHolder, position: Int) {
        holder.bind(suggestions[position])
    }

    override fun getItemCount(): Int = suggestions.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateSuggestions(newSuggestions: List<dbCity>) {
        suggestions = newSuggestions
        notifyDataSetChanged()
    }
}