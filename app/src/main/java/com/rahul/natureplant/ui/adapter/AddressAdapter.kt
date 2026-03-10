package com.rahul.natureplant.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rahul.natureplant.databinding.ItemShippingAddressBinding
import com.rahul.natureplant.model.Address

class AddressAdapter(private val onAddressSelected: (Address) -> Unit) :
    ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemShippingAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
        holder.itemView.setOnClickListener {
            onAddressSelected(address)
        }
    }

    class AddressViewHolder(private val binding: ItemShippingAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address) {
            binding.tvAddressTitle.text = address.title
            binding.tvAddressDetail.text = address.detail
            binding.rbSelected.isChecked = address.isSelected
        }
    }

    private class AddressDiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean = oldItem == newItem
    }
}
