package com.rahul.natureplant.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.rahul.natureplant.adapter.LocationAdapter
import com.rahul.natureplant.databinding.FragmentAddAddressBinding
import com.rahul.natureplant.model.Location as LocationModel
import com.rahul.natureplant.viewmodel.PlantViewModel
import java.io.IOException
import java.util.*

class AddAddressFragment : Fragment() {

    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    
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
                requireContext(),
                "Location permission is required to use current location feature.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        geocoder = Geocoder(requireContext(), Locale.getDefault())

        setupRecyclerView()
        setupListeners()
    }

    private fun setupRecyclerView() {
        locationAdapter = LocationAdapter(emptyList()) { location ->
            viewModel.addAddress(location.address)
            findNavController().navigateUp()
        }
        binding.locationResultsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.locationResultsRecyclerView.adapter = locationAdapter
    }

    private fun setupListeners() {
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.useCurrentLocationTextView.setOnClickListener {
            checkLocationPermission()
        }

        binding.searchEditText.addTextChangedListener(object : TextWatcher {
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
                        if (address.getAddressLine(0) != null) {
                            LocationModel(
                                name = address.featureName ?: "Unnamed",
                                address = address.getAddressLine(0),
                                latitude = address.latitude,
                                longitude = address.longitude
                            )
                        } else null
                    }
                    activity?.runOnUiThread {
                        locationAdapter.updateLocations(locations)
                    }
                }
                override fun onError(errorMessage: String?) {
                    activity?.runOnUiThread {
                        Log.e("AddAddressFragment", "Error searching: $errorMessage")
                    }
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocationName(query, 10)
                val locations = addresses?.mapNotNull { address ->
                    if (address.getAddressLine(0) != null) {
                        LocationModel(
                            name = address.featureName ?: "Unnamed",
                            address = address.getAddressLine(0),
                            latitude = address.latitude,
                            longitude = address.longitude
                        )
                    } else null
                } ?: emptyList()
                locationAdapter.updateLocations(locations)
            } catch (e: IOException) {
                Log.e("AddAddressFragment", "Search error", e)
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fetchAndUseCurrentLocation()
        } else {
            locationPermissionRequest.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchAndUseCurrentLocation() {
        Toast.makeText(requireContext(), "Fetching current location...", Toast.LENGTH_SHORT).show()
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 10000)
            .setMaxUpdates(1)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                fusedLocationClient.removeLocationUpdates(this)
                locationResult.locations.firstOrNull()?.let { processLocation(it) }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback!!, Looper.getMainLooper())
    }

    private fun processLocation(location: Location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            geocoder.getFromLocation(location.latitude, location.longitude, 1, object : Geocoder.GeocodeListener {
                override fun onGeocode(addresses: MutableList<Address>) {
                    if (addresses.isNotEmpty()) {
                        val addressLine = addresses[0].getAddressLine(0)
                        activity?.runOnUiThread {
                            viewModel.addAddress(addressLine)
                            findNavController().navigateUp()
                        }
                    }
                }
                override fun onError(errorMessage: String?) {
                    Log.e("AddAddressFragment", "Geocoder error: $errorMessage")
                }
            })
        } else {
            try {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    viewModel.addAddress(addresses[0].getAddressLine(0))
                    findNavController().navigateUp()
                }
            } catch (e: IOException) {
                Log.e("AddAddressFragment", "Geocoder IO error", e)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        locationCallback?.let { fusedLocationClient.removeLocationUpdates(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}