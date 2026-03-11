package com.rahul.natureplant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ShippingType(
    val id: Int,
    val title: String,
    val estimatedArrival: String,
    val price: Double,
    var isSelected: Boolean = false
) : Parcelable
