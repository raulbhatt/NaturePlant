package com.rahul.natureplant.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentHomeBinding
import com.rahul.natureplant.ui.adapter.CategoryAdapter
import com.rahul.natureplant.ui.adapter.PlantAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), NavigationView.OnNavigationItemSelectedListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by viewModels()
    private lateinit var plantAdapter: PlantAdapter
    private var rotation: Animation? = null
    private lateinit var toggle: ActionBarDrawerToggle


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


        showLoading()

        setupPlants()
        setupCategories()

        binding.tvSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_categoryFragment)
        }
    }

    private fun showLoading() {
        binding.nestedScrollView.visibility = View.GONE
        binding.ivLoading.visibility = View.VISIBLE
        rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        binding.ivLoading.startAnimation(rotation)
    }

    private fun hideLoading() {
        binding.nestedScrollView.visibility = View.VISIBLE
        binding.ivLoading.visibility = View.GONE
        binding.ivLoading.clearAnimation()
        rotation = null
    }

    private fun setupCategories() {
        val categoryAdapter = CategoryAdapter { category ->
            val allPlants = viewModel.plants.value ?: emptyList()
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
        plantAdapter = PlantAdapter { plant ->
            val action = HomeFragmentDirections.actionHomeFragmentToProductDetailFragment(plant)
            findNavController().navigate(action)
        }
        binding.rvPlants.adapter = plantAdapter
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            plantAdapter.submitList(plants)
            // Assuming data is loaded when we get plants
            lifecycleScope.launch {
                delay(1500) // Simulate loading
                hideLoading()
            }
        }
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
