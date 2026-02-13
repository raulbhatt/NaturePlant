package com.rahul.natureplant.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.rahul.natureplant.MainActivity
import com.rahul.natureplant.R
import com.rahul.natureplant.adapter.LocationAdapter
import com.rahul.natureplant.model.Location as LocationModel
import java.io.IOException
import java.util.Locale

class EnterLocationActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationAdapter: LocationAdapter
    private lateinit var geocoder: Geocoder
    private var locationCallback: LocationCallback? = null

    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)
        ) {
            fetchAndUseCurrentLocation()
        } else {
            Toast.makeText(
                this,
                "Location permission is required to use current location feature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_location)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val useCurrentLocationTextView = findViewById<TextView>(R.id.use_current_location_text_view)
        val locationResultsRecyclerView = findViewById<RecyclerView>(R.id.location_results_recycler_view)

        locationAdapter = LocationAdapter(emptyList()) { location ->
            navigateToHome(location)
        }
        locationResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        locationResultsRecyclerView.adapter = locationAdapter

        useCurrentLocationTextView.setOnClickListener {
            checkLocationPermission()
        }

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    searchLocations(s.toString())
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun searchLocations(query: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocationName(query, 10, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    val locations = addresses.mapNotNull { address ->
                        if (address.featureName != null && address.getAddressLine(0) != null) {
                            LocationModel(
                                name = address.featureName,
                                address = address.getAddressLine(0),
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        } else {
                            null
                        }
                    }
                    runOnUiThread {
                        locationAdapter.updateLocations(locations)
                    }
                }

                override fun onError(errorMessage: String?) {
                    super.onError(errorMessage)
                    runOnUiThread {
                        Log.e("EnterLocationActivity", "Error searching for locations: $errorMessage")
                        Toast.makeText(this@EnterLocationActivity, "Error searching for locations. Please check your network connection.", Toast.LENGTH_LONG).show()
                    }
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 10)
                if (addresses != null) {
                    val locations = addresses.mapNotNull { address ->
                        if (address.featureName != null && address.getAddressLine(0) != null) {
                            LocationModel(
                                name = address.featureName,
                                address = address.getAddressLine(0),
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        } else {
                            null
                        }
                    }
                    locationAdapter.updateLocations(locations)
                } else {
                    locationAdapter.updateLocations(emptyList())
                }
            } catch (e: IOException) {
                Log.e("EnterLocationActivity", "Error searching for locations (legacy).", e)
                Toast.makeText(this, "Error searching for locations. Please check your network connection.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToHome(location: LocationModel) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("NAVIGATE_TO", "HOME")
        intent.putExtra("location", location)
        startActivity(intent)
        finish()
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchAndUseCurrentLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchAndUseCurrentLocation() {
        Toast.makeText(this, "Fetching current location...", Toast.LENGTH_SHORT).show()

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
            .setMaxUpdates(1)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                val location = locationResult.locations.firstOrNull()
                if (location != null) {
                    processLocation(location)
                } else {
                    handleLocationFetchFailure("Location result was empty.")
                }
            }
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

    private fun processLocation(location: Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val locationModel = LocationModel(
                            name = address.featureName ?: "Unnamed Location",
                            address = address.getAddressLine(0),
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                        navigateToHome(locationModel)
                    } else {
                        handleGeocoderFailure(location)
                    }
                }

                override fun onError(errorMessage: String?) {
                    Log.e("EnterLocationActivity", "Geocoder error: $errorMessage")
                    handleGeocoderFailure(location)
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    val locationModel = LocationModel(
                        name = address.featureName ?: "Unnamed Location",
                        address = address.getAddressLine(0),
                        latitude = address.latitude,
                        longitude = address.longitude
                    )
                    navigateToHome(locationModel)
                } else {
                    handleGeocoderFailure(location)
                }
            } catch (e: IOException) {
                Log.e("EnterLocationActivity", "Geocoder IO error", e)
                handleGeocoderFailure(location)
            }
        }
    }

    private fun handleGeocoderFailure(location: Location) {
        Toast.makeText(this, "Could not determine address. Using coordinates.", Toast.LENGTH_SHORT).show()
        val locationModel = LocationModel(
            name = "Current Location",
            address = "Lat: ${location.latitude}, Lon: ${location.longitude}",
            latitude = location.latitude,
            longitude = location.longitude
        )
        navigateToHome(locationModel)
    }

    private fun handleLocationFetchFailure(reason: String) {
        Log.e("EnterLocationActivity", "Location fetch failed: $reason")
        Toast.makeText(this, "Unable to fetch current location. Please ensure location is enabled and try again.", Toast.LENGTH_LONG).show()
    }

    override fun onStop() {
        super.onStop()
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}
