package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemPlantBinding
import com.rahul.natureplant.model.Plant

class PlantAdapter(
    private val onPlantClick: (Plant) -> Unit
) : ListAdapter<Plant, PlantAdapter.PlantViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantViewHolder {
        val binding = ItemPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
        holder.itemView.setOnClickListener { onPlantClick(plant) }
    }

    inner class PlantViewHolder(private val binding: ItemPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.tvPlantName.text = plant.name
            binding.tvPlantPrice.text = "$${plant.price}"
            binding.tvRating.text = "${plant.rating} (${plant.reviewCount})"
            
            // Load image from URL using Glide
            Glide.with(binding.root.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.img_aloe) // Use existing image as placeholder
                .error(R.drawable.img_aloe)
                .into(binding.ivPlant)
        }
    }

    private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem == newItem
        }
    }
}
