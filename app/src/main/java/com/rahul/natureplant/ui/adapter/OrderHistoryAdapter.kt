package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ItemOrderHistoryBinding
import com.rahul.natureplant.model.Plant

class OrderHistoryAdapter(private val onTrackOrderClick: (Plant) -> Unit) : ListAdapter<Plant, OrderHistoryAdapter.OrderViewHolder>(PlantDiffCallback()) {

    var tabType: Int = 0 // 0: Active, 1: Completed, 2: Cancelled
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position), tabType, onTrackOrderClick)
    }

    class OrderViewHolder(private val binding: ItemOrderHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(plant: Plant, tabType: Int, onTrackOrderClick: (Plant) -> Unit) {
            binding.tvPlantName.text = plant.name
            binding.tvPlantCategoryQty.text = "${plant.category} | Qty. : ${String.format("%02d", plant.quantity)} pcs"
            binding.tvPlantPrice.text = "$${String.format("%.2f", (plant.price * plant.quantity).toDouble())}"
            
            binding.btnTrackOrder.text = when (tabType) {
                1 -> "Leave Review"
                2 -> "Re-Order"
                else -> "Track Order"
            }

            binding.btnTrackOrder.setOnClickListener {
                if (tabType == 0) {
                    onTrackOrderClick(plant)
                }
            }

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
