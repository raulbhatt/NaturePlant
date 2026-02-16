package com.rahul.natureplant.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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


    private val cart = mutableListOf<Plant>()

    init {
        //loadMockData()
        loadApiPlantData()
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

    private fun loadMockData() {
        val categoryList = listOf(
            Category("All", "https://images.unsplash.com/photo-1470058869958-2a77a67d123f?w=1000&q=80"),

            Category("Succulent", "https://images.unsplash.com/photo-1509423350716-97f9360b4e09?w=1000&q=80"),

            Category("Indoor", "https://images.unsplash.com/photo-1517191434949-5e90cd67d2b6?w=1000&q=80"),

            Category("Decorative", "https://images.unsplash.com/photo-1516048015710-7a3b4c86be43?w=1000&q=80"),

            Category("Hanging", "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=1000&q=80"),

            Category("Office", "https://images.unsplash.com/photo-1497215728101-856f4ea42174?w=1000&q=80"),

            Category("Outdoor", "https://images.unsplash.com/photo-1585320806297-9794b3e4eeae?w=1000&q=80")
        )
        _categories.value = categoryList

        val plantNames = listOf(
            "Aloe Vera" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Snake Plant" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Monstera" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Pothos" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Jade Plant" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Fiddle Leaf Fig" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Peace Lily" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Spider Plant" to "https://images.unsplash.com/photo-1512428813824-f713cb752935?w=800&q=80",
            "Rubber Plant" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Boston Fern" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "ZZ Plant" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Philodendron" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Calathea" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Anthurium" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Bird of Paradise" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "English Ivy" to "https://images.unsplash.com/photo-1512428813824-f713cb752935?w=800&q=80",
            "Chinese Money Plant" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Dragon Tree" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Areca Palm" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Bamboo Palm" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Cast Iron Plant" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Prayer Plant" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Swiss Cheese Plant" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Devil's Ivy" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Maidenhair Fern" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "Rubber Tree" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Yucca" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Croton" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Schefflera" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Dracaena" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Money Tree" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Parlor Palm" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "Kentia Palm" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Sago Palm" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Pony Tail Palm" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "African Violet" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Begonia" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Geranium" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Cyclamen" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "Amaryllis" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Christmas Cactus" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Easter Cactus" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Kalanchoe" to "https://images.unsplash.com/photo-1512428813824-f713cb752935?w=800&q=80",
            "Orchid" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Lavender" to "https://images.unsplash.com/photo-1596547609652-9cf5d8d76921?w=800&q=80",
            "Rosemary" to "https.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80",
            "Mint" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Thyme" to "https://images.unsplash.com/photo-1597055410917-6868ec5fcf9a?w=800&q=80",
            "Basil" to "https://images.unsplash.com/photo-1632207691143-643e2a9a9b7a?w=800&q=80",
            "Chives" to "https://images.unsplash.com/photo-1512428813824-f713cb752935?w=800&q=80",
            "Parsley" to "https://images.unsplash.com/photo-1593691509543-c55fb32e7355?w=800&q=80",
            "Sage" to "https://images.unsplash.com/photo-1545239351-ef35f43d514b?w=800&q=80",
            "Oregano" to "https://images.unsplash.com/photo-1614594975525-e45190c55d0b?w=800&q=80",
            "Dill" to "https://images.unsplash.com/photo-1599819177626-b50f9dd21c9b?w=800&q=80"
        )

        val mockPlants = mutableListOf<Plant>()
        val random = java.util.Random()

        for (i in 1..55) {
            val plantInfo = plantNames[(i - 1) % plantNames.size]
            val category = when (i % 6) {
                0 -> "Succulent"
                1 -> "Indoor"
                2 -> "Decorative"
                3 -> "Hanging"
                4 -> "Office"
                else -> "Outdoor"
            }

            mockPlants.add(
                Plant(
                    id = i,
                    name = "${plantInfo.first} ${if (i > plantNames.size) (i / plantNames.size) + 1 else ""}".trim(),
                    price = (15.0 + (i * 3.7) % 180.0).toInt(),
                    rating = 4.0 + (random.nextFloat().roundToInt()),
                    reviewCount = 10 + random.nextInt(990),
                    description = "A premium ${plantInfo.first} specimen. Perfect for adding a touch of nature to your living space. This $category plant is both beautiful and resilient.",
                    imageUrl = plantInfo.second,
                    category = category
                )
            )
        }
        _plants.value = mockPlants
    }

    fun loadApiPlantData() {
        viewModelScope.launch(Dispatchers.Main) {
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
