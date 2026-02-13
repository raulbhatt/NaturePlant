// Force update. Overwriting entire file to ensure correctness.
package com.rahul.natureplant.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rahul.natureplant.R

class LocationInformationActivity : AppCompatActivity() {

    // Launcher for fine/coarse location permission request
    private val requestPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val isFineGranted = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            val isCoarseGranted = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (isFineGranted || isCoarseGranted) {
                // Permission is granted. Continue the workflow.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundLocationPermission()
                } else {
                    checkLocationServiceAndProceed()
                }
            } else {
                // Permission was denied. Check if we should show a rationale.
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)
                ) {
                    showPermissionRationale()
                } else {
                    // User has permanently denied the permission.
                    showSettingsRedirectDialog()
                }
            }
        }

    // Launcher for background location permission request
    private val requestBackgroundPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                checkLocationServiceAndProceed()
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    showBackgroundPermissionRationale()
                } else {
                    showSettingsRedirectDialog()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_information)

        val allowLocationButton = findViewById<Button>(R.id.allow_location_button)
        allowLocationButton.setOnClickListener {
            requestLocationPermissions()
        }

        val enterLocationManuallyButton = findViewById<TextView>(R.id.enter_location_manually_button)
        enterLocationManuallyButton.setOnClickListener {
            val intent = Intent(this, NotificationPermissionActivity::class.java)
            startActivity(intent)
        }
    }

    private fun requestLocationPermissions() {
        val hasFineLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocation = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            // Permissions are already granted.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                requestBackgroundLocationPermission()
            } else {
                checkLocationServiceAndProceed()
            }
        } else {
            // Permissions are not granted. Request them.
            requestPermissionsLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermission() {
        // This function is only called on Android Q and above.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkLocationServiceAndProceed()
        } else {
            requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(this)
            .setTitle("Location Permission Needed")
            .setMessage("This app needs the Location permission to function correctly. Please grant the permission.")
            .setPositiveButton("OK") { _, _ ->
                // Re-trigger the permission request
                requestPermissionsLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showBackgroundPermissionRationale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            AlertDialog.Builder(this)
                .setTitle("Background Location Permission")
                .setMessage("This app needs background location access to provide timely updates. Please select 'Allow all the time'.")
                .setPositiveButton("Grant") { _, _ ->
                    requestBackgroundPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                .setNegativeButton("Cancel", null)
                .create()
                .show()
        }
    }

    private fun showSettingsRedirectDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Location permission has been permanently denied. Please enable it in App Settings to use this feature.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun checkLocationServiceAndProceed() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            locationManager.isLocationEnabled
        } else {
            @Suppress("DEPRECATION")
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            @Suppress("DEPRECATION")
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        }

        if (isLocationEnabled) {
            val intent = Intent(this, EnterLocationActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            AlertDialog.Builder(this)
                .setTitle("Enable Location")
                .setMessage("Your location services are turned off. Please enable location to continue.")
                .setPositiveButton("Go to Settings") { _, _ ->
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
}
