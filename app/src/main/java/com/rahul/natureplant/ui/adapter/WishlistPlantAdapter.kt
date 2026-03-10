package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemWishlistPlantBinding
import com.rahul.natureplant.model.Plant

class WishlistPlantAdapter(
    private val onPlantClick: (Plant) -> Unit,
    private val onWishlistClick: (Plant) -> Unit
) : ListAdapter<Plant, WishlistPlantAdapter.WishlistViewHolder>(WishlistDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WishlistViewHolder {
        val binding = ItemWishlistPlantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WishlistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WishlistViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
        holder.itemView.setOnClickListener { onPlantClick(plant) }
        holder.binding.btnWishlist.setOnClickListener { onWishlistClick(plant) }
    }

    inner class WishlistViewHolder(val binding: ItemWishlistPlantBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.tvPlantName.text = plant.name
            binding.tvPlantPrice.text = "$${plant.price}.00"
            binding.tvRating.text = plant.rating.toString()
            
            Glide.with(binding.root.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.img_aloe)
                .into(binding.ivPlant)
            
            // In wishlist, it should be filled, but image shows a specific style
            binding.btnWishlist.setImageResource(R.drawable.ic_heart)
            binding.btnWishlist.setColorFilter(binding.root.context.getColor(R.color.black))
        }
    }

    private class WishlistDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean = oldItem == newItem
    }
}