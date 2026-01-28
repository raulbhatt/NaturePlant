package com.rahul.natureplant.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemPlantCategoryBinding
import com.rahul.natureplant.model.Plant

class PlantCategoryAdapter(
    private val plantList: List<Plant>
) : RecyclerView.Adapter<PlantCategoryAdapter.PlantCategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantCategoryViewHolder {
        val binding = ItemPlantCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlantCategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlantCategoryViewHolder, position: Int) {
        holder.bind(plantList[position])
    }

    override fun getItemCount() = plantList.size

    inner class PlantCategoryViewHolder(private val binding: ItemPlantCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(plant: Plant) {
            binding.plantNameTextView.text = plant.name
            binding.plantDescriptionTextView.text = plant.description
            Glide.with(binding.root.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.plantImageView)
        }
    }
}