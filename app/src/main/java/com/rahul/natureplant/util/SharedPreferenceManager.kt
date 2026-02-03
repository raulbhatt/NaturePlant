package com.rahul.natureplant.util

import android.content.Context
import android.content.SharedPreferences

class SharedPreferenceManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("biometric_prefs", Context.MODE_PRIVATE)

    fun isBiometricEnabled(): Boolean {
        return sharedPreferences.getBoolean("is_biometric_enabled", false)
    }

    fun setBiometricEnabled(enabled: Boolean) {
        sharedPreferences.edit().putBoolean("is_biometric_enabled", enabled).apply()
    }
}
