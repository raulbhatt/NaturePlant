package com.rahul.natureplant.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val id: Int,
    val title: String,
    val detail: String,
    val isSelected: Boolean = false
) : Parcelable
