package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemCartBinding
import com.rahul.natureplant.model.Plant

class CartAdapter(
    private val onRemoveClick: (Plant) -> Unit,
    private val onIncreaseQuantity: (Plant) -> Unit,
    private val onDecreaseQuantity: (Plant) -> Unit
) : ListAdapter<Plant, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val plant = getItem(position)
        holder.bind(plant)
        holder.binding.btnRemove.setOnClickListener { onRemoveClick(plant) }
        holder.binding.btnPlus.setOnClickListener { onIncreaseQuantity(plant) }
        holder.binding.btnMinus.setOnClickListener { onDecreaseQuantity(plant) }
    }

    inner class CartViewHolder(internal val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant) {
            binding.tvPlantName.text = plant.name
            binding.tvPlantPrice.text = "$${plant.price}"
            binding.tvQuantity.text = plant.quantity.toString()

            // Load image from URL using Glide
            Glide.with(binding.root.context)
                .load(plant.imageUrl)
                .placeholder(R.drawable.img_aloe)
                .error(R.drawable.img_aloe)
                .into(binding.ivPlant)
        }
    }

    private class CartDiffCallback : DiffUtil.ItemCallback<Plant>() {
        override fun areItemsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Plant, newItem: Plant): Boolean {
            return oldItem == newItem
        }
    }
}
