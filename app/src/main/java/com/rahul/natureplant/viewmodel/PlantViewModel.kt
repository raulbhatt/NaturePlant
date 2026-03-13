package com.rahul.natureplant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.natureplant.model.Address
import com.rahul.natureplant.model.Category
import com.rahul.natureplant.model.Plant
import com.rahul.natureplant.model.ShippingType
import com.rahul.natureplant.repository.PlantRepository
import com.rahul.natureplant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response

class PlantViewModel : ViewModel() {

    private val repository = PlantRepository()

    private val _plantsApi = MutableLiveData<Resource<List<Plant>>>()
    val plantsApi: LiveData<Resource<List<Plant>>> = _plantsApi

    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _cartItems = MutableLiveData<List<Plant>>()
    val cartItems: LiveData<List<Plant>> = _cartItems

    private val _lastOrderItems = MutableLiveData<List<Plant>>()
    val lastOrderItems: LiveData<List<Plant>> = _lastOrderItems

    private val _wishlistItems = MutableLiveData<List<Plant>>(emptyList())
    val wishlistItems: LiveData<List<Plant>> = _wishlistItems

    private val _addresses = MutableLiveData<List<Address>>()
    val addresses: LiveData<List<Address>> = _addresses

    private val _selectedAddress = MutableLiveData<Address>()
    val selectedAddress: LiveData<Address> = _selectedAddress

    private val _shippingTypes = MutableLiveData<List<ShippingType>>()
    val shippingTypes: LiveData<List<ShippingType>> = _shippingTypes

    private val _selectedShippingType = MutableLiveData<ShippingType>()
    val selectedShippingType: LiveData<ShippingType> = _selectedShippingType

    private val cart = mutableListOf<Plant>()
    private val wishlist = mutableListOf<Plant>()

    init {
        loadApiPlantData()
        loadShippingTypes()
    }

    private fun loadShippingTypes() {
        val types = listOf(
            ShippingType(1, "Economy", "Estimated Arrival 22 Dec 2023", 25.0, true),
            ShippingType(2, "Regular", "Estimated Arrival 21 Dec 2023", 35.0),
            ShippingType(3, "Express", "Estimated Arrival 20 Dec", 45.0)
        )
        _shippingTypes.value = types
        _selectedShippingType.value = types[0]
    }

    fun selectShippingType(shippingType: ShippingType) {
        val updatedList = _shippingTypes.value?.map {
            it.copy(isSelected = it.id == shippingType.id)
        }
        _shippingTypes.value = updatedList ?: emptyList()
        _selectedShippingType.value = shippingType.copy(isSelected = true)
    }

    fun addCurrentLocationAddress(fullAddress: String) {
        val currentList = _addresses.value?.toMutableList() ?: mutableListOf()
        val exists = currentList.any { it.title == "Current Location" }
        
        if (!exists) {
            val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
            val newAddress = Address(newId, "Current Location", fullAddress, false)
            currentList.add(0, newAddress)
            _addresses.value = currentList
        } else {
            val updatedList = currentList.map {
                if (it.title == "Current Location") it.copy(detail = fullAddress) else it
            }
            _addresses.value = updatedList
        }
    }

    fun addAddress(fullAddress: String) {
        val currentList = _addresses.value?.toMutableList() ?: mutableListOf()
        val newId = (currentList.maxOfOrNull { it.id } ?: 0) + 1
        val newAddress = Address(newId, "New Address", fullAddress, false)
        currentList.add(newAddress)
        _addresses.value = currentList
    }

    fun selectAddress(address: Address) {
        val updatedList = _addresses.value?.map {
            it.copy(isSelected = it.id == address.id)
        }
        _addresses.value = updatedList ?: emptyList()
        _selectedAddress.value = address.copy(isSelected = true)
    }

    fun addToCart(plant: Plant, quantity: Int) {
        val existingPlant = cart.find { it.id == plant.id }
        if (existingPlant != null) {
            val updatedPlant = existingPlant.copy(quantity = existingPlant.quantity + quantity)
            val index = cart.indexOf(existingPlant)
            cart[index] = updatedPlant
        } else {
            cart.add(plant.copy(quantity = quantity))
        }
        _cartItems.value = ArrayList(cart)
    }

    fun clearCart() {
        _lastOrderItems.value = ArrayList(cart)
        cart.clear()
        _cartItems.value = ArrayList(cart)
    }

    fun removeFromCart(plant: Plant) {
        cart.remove(plant)
        _cartItems.value = ArrayList(cart)
    }

    fun toggleWishlist(plant: Plant) {
        val existing = wishlist.find { it.id == plant.id }
        if (existing != null) {
            wishlist.remove(existing)
        } else {
            wishlist.add(plant.copy(isFavorite = true))
        }
        _wishlistItems.value = ArrayList(wishlist)
        
        val currentData = _plantsApi.value?.data
        if (currentData != null) {
            val updatedList = currentData.map {
                if (it.id == plant.id) it.copy(isFavorite = !it.isFavorite) else it
            }
            _plantsApi.value = Resource.Success(updatedList)
        }
    }

    fun increaseQuantity(plant: Plant) {
        val existingPlant = cart.find { it.id == plant.id }
        if (existingPlant != null) {
            val updatedPlant = existingPlant.copy(quantity = existingPlant.quantity + 1)
            val index = cart.indexOf(existingPlant)
            cart[index] = updatedPlant
            _cartItems.value = ArrayList(cart)
        }
    }

    fun decreaseQuantity(plant: Plant) {
        val existingPlant = cart.find { it.id == plant.id }
        if (existingPlant != null && existingPlant.quantity > 1) {
            val updatedPlant = existingPlant.copy(quantity = existingPlant.quantity - 1)
            val index = cart.indexOf(existingPlant)
            cart[index] = updatedPlant
            _cartItems.value = ArrayList(cart)
        } else if (existingPlant != null && existingPlant.quantity == 1) {
            cart.remove(existingPlant)
            _cartItems.value = ArrayList(cart)
        }
    }

    fun loadApiPlantData() {
        viewModelScope.launch(Dispatchers.Main) {
            val categoryList = listOf(
                Category("All", "https://images.unsplash.com/photo-1470058869958-2a77a67d123f?w=1000&q=80"),
                Category("Succulents", "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"),
                Category("Low Light", "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"),
                Category("Tropical", "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"),
                Category("Trailing", "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"),
                Category("Trees", "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"),
                Category("Flowering", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"),
                Category("Ferns", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"),
                Category("Small Plants", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80"),
                Category("Herbs", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80")
            )
            _categories.value = categoryList
            _plantsApi.value = Resource.Loading()
            try {
                val response = withContext(Dispatchers.IO) {
                    repository.getPlants()
                }
                _plantsApi.value = handleProductResponse(response)
            } catch (t: Throwable) {
                _plantsApi.value = Resource.Error("Something went wrong")
            }
        }
    }

    private fun handleProductResponse(response: Response<List<Plant>>): Resource<List<Plant>> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                return Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
