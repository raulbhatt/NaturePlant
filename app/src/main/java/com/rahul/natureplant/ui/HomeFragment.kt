package com.rahul.natureplant.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
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
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentHomeBinding
import com.rahul.natureplant.model.Location
import com.rahul.natureplant.util.SharedPrefManager
import com.rahul.natureplant.ui.adapter.CategoryAdapter
import com.rahul.natureplant.ui.adapter.PlantAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var toggle: ActionBarDrawerToggle
    private var backPressCount = 0
    private val backPressHandler = Handler(Looper.getMainLooper())
    private lateinit var sharedPrefManager: SharedPrefManager


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
                    ) // Reset counter after 2 seconds
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
                // You can handle the search action here, e.g., hide the keyboard
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

//        viewModel.plants.observe(viewLifecycleOwner) { plants ->
//            plantAdapter.submitList(plants)
//        }

        viewModel.plantsApi.observe(viewLifecycleOwner) { plants ->
            plantAdapter.submitList(plants?.data)
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
                // Navigate to login screen
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
