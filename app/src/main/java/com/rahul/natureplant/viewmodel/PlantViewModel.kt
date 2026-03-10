package com.rahul.natureplant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.natureplant.model.Address
import com.rahul.natureplant.model.Category
import com.rahul.natureplant.model.Plant
import com.rahul.natureplant.repository.PlantRepository
import com.rahul.natureplant.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import kotlin.math.roundToInt

class PlantViewModel : ViewModel() {

    private val repository = PlantRepository()

    // Mock Data
    private val _plants = MutableLiveData<List<Plant>>()
    val plants: LiveData<List<Plant>> = _plants


    //Api Data
    private val _plantsApi = MutableLiveData<Resource<List<Plant>>>()

    val plantsApi: LiveData<Resource<List<Plant>>> = _plantsApi


    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    private val _cartItems = MutableLiveData<List<Plant>>()
    val cartItems: LiveData<List<Plant>> = _cartItems

    private val _wishlistItems = MutableLiveData<List<Plant>>(emptyList())
    val wishlistItems: LiveData<List<Plant>> = _wishlistItems

    private val _addresses = MutableLiveData<List<Address>>()
    val addresses: LiveData<List<Address>> = _addresses

    private val _selectedAddress = MutableLiveData<Address>()
    val selectedAddress: LiveData<Address> = _selectedAddress


    private val cart = mutableListOf<Plant>()
    private val wishlist = mutableListOf<Plant>()

    init {
        //loadMockData()
        loadApiPlantData()
        loadAddresses()
    }

    private fun loadAddresses() {
        val addressList = listOf(
            Address(1, "Home", "1901 Thornridge Cir. Shiloh, Hawaii 81063", true),
            Address(2, "Office", "4517 Washington Ave. Manchester, Kentucky 39495"),
            Address(3, "Parent's House", "8502 Preston Rd. Inglewood, Maine 98380"),
            Address(4, "Friend's House", "2464 Royal Ln. Mesa, New Jersey 45463"),
            Address(5, "Friend's House", "2464 Royal Ln. Mesa, New Jersey 45463"),
            Address(6, "Friend's House", "2464 Royal Ln. Mesa, New Jersey 45463"),
            Address(7, "Friend's House", "2464 Royal Ln. Mesa, New Jersey 45463"),
        )
        _addresses.value = addressList
        _selectedAddress.value = addressList[0]
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
        
        // Also update the main list if needed
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
