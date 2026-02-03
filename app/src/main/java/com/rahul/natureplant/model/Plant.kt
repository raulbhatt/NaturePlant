package com.rahul.natureplant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Plant(
    val id: Int,
    val name: String,
    val price: Int,
    val rating: Double,
    val reviewCount: Int,
    val description: String,
    val imageUrl: String, // Updated to String for internet images
    val category: String,
    val isFavorite: Boolean = false,
    var quantity: Int = 1
) : Parcelable
