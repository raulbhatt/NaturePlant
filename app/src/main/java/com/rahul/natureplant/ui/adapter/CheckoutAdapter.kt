package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemCheckoutOrderBinding
import com.rahul.natureplant.model.Plant

class CheckoutAdapter : ListAdapter<Plant, CheckoutAdapter.CheckoutViewHolder>(PlantDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckoutViewHolder {
        val binding = ItemCheckoutOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CheckoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CheckoutViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CheckoutViewHolder(private val binding: ItemCheckoutOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.tvPlantName.text = plant.name
            binding.tvPlantCategory.text = plant.category
            binding.tvPlantPrice.text = "$${String.format("%.2f", plant.price.toDouble())}"
            
            Glide.with(binding.root.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.img_aloe)
                .error(R.drawable.img_aloe)
                .into(binding.ivPlant)
        }
    }

    private class PlantDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem == newItem
    }
}
