package com.rahul.natureplant.ui

import VisualSearchManager
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.google.common.util.concurrent.ListenableFuture
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentHomeBinding
import com.rahul.natureplant.model.Location
import com.rahul.natureplant.util.SharedPrefManager
import com.rahul.natureplant.ui.adapter.CategoryAdapter
import com.rahul.natureplant.ui.adapter.PlantAdapter
import com.rahul.natureplant.utils.Resource
import com.rahul.natureplant.viewmodel.PlantViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.ExecutionException

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private var backPressCount = 0
    private val backPressHandler = Handler(Looper.getMainLooper())
    private lateinit var sharedPrefManager: SharedPrefManager

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private val visualSearchManager by lazy { VisualSearchManager("AIzaSyDfmfem5S8_7VDS8d2_s0D2qImZxD5P4I0") }

    private val cameraPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val window = requireActivity().window
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefManager = SharedPrefManager(requireContext())

        val newLocation = arguments?.getParcelable<Location>("location")

        if (newLocation != null) {
            sharedPrefManager.saveLocation(newLocation)
            displayLocation(newLocation)
        } else {
            val storedLocation = sharedPrefManager.getLocation()
            if (storedLocation != null) {
                displayLocation(storedLocation)
            } else {
                binding.txtLocation.text = "No location set"
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (binding.cameraPreview.visibility == View.VISIBLE) {
                        binding.cameraPreview.visibility = View.GONE
                        binding.captureButton.visibility = View.GONE
                    } else {
                        backPressCount++
                        if (backPressCount == 3) {
                            requireActivity().finish()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Press back button to exit the app",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        backPressHandler.removeCallbacksAndMessages(null)
                        backPressHandler.postDelayed(
                            { backPressCount = 0 },
                            2000
                        )
                    }
                }
            })

        ViewCompat.setOnApplyWindowInsetsListener(binding.appBar) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.appBar.setPadding(0, systemBars.top, 0, 0)
            insets
        }

        toggle = ActionBarDrawerToggle(
            requireActivity(),
            binding.drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)

        binding.ivMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.ivCam.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                cameraPermissionRequest.launch(Manifest.permission.CAMERA)
            }
        }
        
        binding.captureButton.setOnClickListener { 
            takePhoto()
        }

        binding.ivProfile.setOnClickListener {
            val intent = Intent(requireActivity(), NotificationActivity::class.java)
            startActivity(intent)
        }

        setupPlants()
        setupCategories()
        setupSearch()


        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }
    }

    private fun displayLocation(location: Location) {
        binding.txtLocation.text = location.address
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
                binding.cameraPreview.visibility = View.VISIBLE
                binding.captureButton.visibility = View.VISIBLE
            } catch (e: ExecutionException) {
                Log.e("HomeFragment", "Error starting camera: ${e.message}")
            } catch (e: InterruptedException) {
                Log.e("HomeFragment", "Error starting camera: ${e.message}")
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview = Preview.Builder().build()
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()
        preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
        imageCapture = ImageCapture.Builder().build()
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview, imageCapture)
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = image.toBitmap()
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            val result = visualSearchManager.performVisualSearch(bitmap)
                            withContext(Dispatchers.Main) {
                                updateUI(result)
                            }
                            image.close()
                        }
                    }
                    binding.cameraPreview.visibility = View.GONE
                    binding.captureButton.visibility = View.GONE
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("HomeFragment", "Image capture failed: ${exception.message}", exception)
                }
            })
    }


    private fun updateUI(result: String?) {
        Toast.makeText(context, result, Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPlants(s.toString())
                if (s.toString().isNotEmpty()) {
                    binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_search),
                        null,
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_close),
                        null
                    )
                } else {
                    binding.etSearch.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(requireContext(), R.drawable.ic_search),
                        null,
                        null,
                        null
                    )
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.etSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = binding.etSearch.compoundDrawables[2]
                if (drawableEnd != null && event.rawX >= (binding.etSearch.right - drawableEnd.bounds.width())) {
                    binding.etSearch.text?.clear()
                    return@setOnTouchListener true
                }
            }
            false
        }

        binding.etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                return@setOnEditorActionListener true
            }
            false
        }
    }

    private fun filterPlants(query: String) {
        val allPlants = viewModel.plantsApi.value?.data ?: emptyList()
        val filteredList = allPlants.filter {
            it.name.lowercase(Locale.getDefault())
                .contains(query.lowercase(Locale.getDefault()))
        }
        plantAdapter.submitList(filteredList)
    }

    private fun setupCategories() {
        val categoryAdapter = CategoryAdapter { category ->
            val allPlants = viewModel.plantsApi.value?.data ?: emptyList()
            val filteredPlants = if (category.name.equals("All", ignoreCase = true)) {
                allPlants
            } else {
                allPlants.filter { it.category.equals(category.name, ignoreCase = true) }
            }
            plantAdapter.submitList(filteredPlants)
        }
        binding.rvCategories.adapter = categoryAdapter
        viewModel.categories.observe(viewLifecycleOwner) { categories ->
            categoryAdapter.submitList(categories)
        }
    }

    private fun setupPlants() {
        plantAdapter = PlantAdapter(onPlantClick = { plant ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(plant)
            findNavController().navigate(action)
        }, onAddToCartClick = { plant ->
            viewModel.addToCart(plant, 1)
            showAnimatedSuccessDialog()
            Toast.makeText(requireContext(), "${plant.name} added to cart", Toast.LENGTH_SHORT)
                .show()
        })
        binding.rvPlants.adapter = plantAdapter

        viewModel.plantsApi.observe(viewLifecycleOwner) { resource ->
            when (resource) {
                is Resource.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    binding.progressBar.visibility = View.GONE
                    plantAdapter.submitList(resource.data)
                }

                is Resource.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun showAnimatedSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_to_cart_success, null)
        val dialog = context?.let { androidx.appcompat.app.AlertDialog.Builder(it) }
            ?.setView(dialogView)
            ?.setCancelable(true)
            ?.create()

        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnOk = dialogView.findViewById<Button>(R.id.btn_ok)

        btnOk.setOnClickListener {
            dialog?.dismiss()
        }

        dialog?.show()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_settings -> {
                Toast.makeText(requireContext(), "Settings clicked", Toast.LENGTH_SHORT).show()
            }

            R.id.nav_logout -> {
                showLogoutDialog()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout") { _, _ ->
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
