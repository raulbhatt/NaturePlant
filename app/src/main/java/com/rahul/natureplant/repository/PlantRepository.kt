package com.rahul.natureplant.repository

import com.rahul.natureplant.network.RetrofitInstance

class PlantRepository {

    suspend fun getPlants() = RetrofitInstance.api.getPlants()

    //suspend fun getPlantDetails(plantId: Int) = RetrofitInstance.api.getPlantDetails(plantId)
}
