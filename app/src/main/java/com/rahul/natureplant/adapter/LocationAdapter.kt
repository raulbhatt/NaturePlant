package com.rahul.natureplant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rahul.natureplant.R
import com.rahul.natureplant.model.Location

class LocationAdapter(
    private var locations: List<Location>,
    private val onLocationClick: (Location) -> Unit
) :
    RecyclerView.Adapter<LocationAdapter.LocationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_location_result, parent, false)
        return LocationViewHolder(view)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val location = locations[position]
        holder.bind(location)
        holder.itemView.setOnClickListener {
            onLocationClick(location)
        }
    }

    override fun getItemCount(): Int {
        return locations.size
    }

    fun updateLocations(newLocations: List<Location>) {
        locations = newLocations
        notifyDataSetChanged()
    }

    class LocationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val locationNameTextView: TextView =
            itemView.findViewById(R.id.location_name_text_view)
        private val locationAddressTextView: TextView =
            itemView.findViewById(R.id.location_address_text_view)

        fun bind(location: Location) {
            locationNameTextView.text = location.name
            locationAddressTextView.text = location.address
        }
    }
}