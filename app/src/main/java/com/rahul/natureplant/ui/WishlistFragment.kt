package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.databinding.FragmentWishlistBinding
import com.rahul.natureplant.ui.adapter.WishlistPlantAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class WishlistFragment : Fragment() {

    private var _binding: FragmentWishlistBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var wishlistAdapter: WishlistPlantAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWishlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeWishlist()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        wishlistAdapter = WishlistPlantAdapter(
            onPlantClick = { plant ->
                // Navigate to product detail
                // val action = WishlistFragmentDirections.actionWishlistFragmentToProductDetailFragment(plant)
                // findNavController().navigate(action)
            },
            onWishlistClick = { plant ->
                // Remove from wishlist logic
            }
        )
        binding.rvWishlist.adapter = wishlistAdapter
    }

    private fun observeWishlist() {
        // Since the ViewModel doesn't have a specific wishlist LiveData yet,
        // we'll use plants as a placeholder or filter them if they have isFavorite = true
        viewModel.plantsApi.observe(viewLifecycleOwner) { resource ->
            val plants = resource.data ?: emptyList()
            
            // Mocking wishlist items for demonstration
            // In a real app, this would be a specific list from the ViewModel
            val wishlistItems = plants.filter { it.isFavorite }
            
            if (wishlistItems.isEmpty()) {
                binding.rvWishlist.visibility = View.GONE
                binding.tvEmptyWishlist.visibility = View.VISIBLE
            } else {
                binding.rvWishlist.visibility = View.VISIBLE
                binding.tvEmptyWishlist.visibility = View.GONE
                wishlistAdapter.submitList(wishlistItems)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}