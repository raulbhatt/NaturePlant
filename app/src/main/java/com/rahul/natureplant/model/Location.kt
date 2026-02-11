package com.rahul.natureplant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Location(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
) : Parcelable