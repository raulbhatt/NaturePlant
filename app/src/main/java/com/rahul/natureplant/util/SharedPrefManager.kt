package com.rahul.natureplant.util

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.rahul.natureplant.model.Location

class SharedPrefManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()
    private val gson = Gson()

    fun saveLocation(location: Location) {
        val locationJson = gson.toJson(location)
        editor.putString("location", locationJson)
        editor.apply()
    }

    fun getLocation(): Location? {
        val locationJson = sharedPreferences.getString("location", null)
        return if (locationJson != null) {
            gson.fromJson(locationJson, Location::class.java)
        } else {
            null
        }
    }
}
