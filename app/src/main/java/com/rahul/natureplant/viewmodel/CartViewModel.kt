package com.rahul.natureplant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rahul.natureplant.model.Plant

class CartViewModel : ViewModel() {

    private val _cartItems = MutableLiveData<MutableList<Plant>>(mutableListOf())
    val cartItems: LiveData<MutableList<Plant>> get() = _cartItems

    fun addToCart(plant: Plant) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.id == plant.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            currentItems.add(plant)
        }
        _cartItems.value = currentItems
    }

    fun removeFromCart(plant: Plant) {
        val currentItems = _cartItems.value ?: mutableListOf()
        currentItems.remove(plant)
        _cartItems.value = currentItems
    }

    fun increaseQuantity(plant: Plant) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.id == plant.id }
        if (existingItem != null) {
            existingItem.quantity++
            _cartItems.value = currentItems
        }
    }

    fun decreaseQuantity(plant: Plant) {
        val currentItems = _cartItems.value ?: mutableListOf()
        val existingItem = currentItems.find { it.id == plant.id }
        if (existingItem != null && existingItem.quantity > 1) {
            existingItem.quantity--
            _cartItems.value = currentItems
        }
    }
}
