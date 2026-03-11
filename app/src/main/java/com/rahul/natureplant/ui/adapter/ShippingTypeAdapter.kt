package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rahul.natureplant.databinding.ItemShippingTypeBinding
import com.rahul.natureplant.model.ShippingType

class ShippingTypeAdapter(private val onTypeSelected: (ShippingType) -> Unit) :
    ListAdapter<ShippingType, ShippingTypeAdapter.ShippingTypeViewHolder>(ShippingTypeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShippingTypeViewHolder {
        val binding = ItemShippingTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShippingTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShippingTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ShippingTypeViewHolder(private val binding: ItemShippingTypeBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(shippingType: ShippingType) {
            binding.apply {
                tvShippingTitle.text = shippingType.title
                tvShippingDetail.text = shippingType.estimatedArrival
                tvShippingPrice.text = "$${shippingType.price.toInt()}"
                rbSelect.isChecked = shippingType.isSelected

                root.setOnClickListener {
                    onTypeSelected(shippingType)
                }
            }
        }
    }

    class ShippingTypeDiffCallback : DiffUtil.ItemCallback<ShippingType>() {
        override fun areItemsTheSame(oldItem: ShippingType, newItem: ShippingType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShippingType, newItem: ShippingType): Boolean {
            return oldItem == newItem
        }
    }
}
