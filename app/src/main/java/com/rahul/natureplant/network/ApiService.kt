package com.rahul.natureplant.network

import com.rahul.natureplant.model.Plant
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("Plant")
    suspend fun getPlants(): List<Plant>

    @GET("Plant/{plantId}")
    suspend fun getPlantDetails(@Path("plantId") plantId: Int): Plant





}