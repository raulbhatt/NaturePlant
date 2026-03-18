package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.rahul.natureplant.databinding.FragmentSellerProfileBinding
import com.rahul.natureplant.model.Plant
import com.rahul.natureplant.ui.adapter.GalleryAdapter
import com.rahul.natureplant.ui.adapter.PlantAdapter
import com.rahul.natureplant.ui.adapter.Review
import com.rahul.natureplant.ui.adapter.ReviewAdapter

class SellerProfileFragment : Fragment() {

    private var _binding: FragmentSellerProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var plantAdapter: PlantAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var reviewAdapter: ReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSellerProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupListeners()
        setupTabs()
    }

    private fun setupRecyclerViews() {
        // Plants Setup
        plantAdapter = PlantAdapter(
            onPlantClick = { plant ->
                Toast.makeText(requireContext(), "Clicked: ${plant.name}", Toast.LENGTH_SHORT).show()
            },
            onAddToCartClick = { plant ->
                Toast.makeText(requireContext(), "Added ${plant.name} to cart", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvSellerPlants.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvSellerPlants.adapter = plantAdapter

        val sellerPlants = listOf(
            Plant(201, "Monstera Deliciosa", 25, 4.9, 150, "Beautiful indoor plant", "https://images.unsplash.com/photo-1614594975525-e45190c55d0b", "Indoor", false, 1),
            Plant(202, "Watermelon Peperomia", 25, 4.9, 85, "Small indoor plant", "https://images.unsplash.com/photo-1599598477618-97fe494848f3", "Indoor", false, 1),
            Plant(203, "Snake Plant", 20, 4.7, 120, "Hardy plant", "https://images.unsplash.com/photo-1628175344321-72436f470559", "Indoor", false, 1),
            Plant(204, "Aloe Vera", 15, 4.8, 200, "Medicinal plant", "https://images.unsplash.com/photo-1512428813834-c702c7702b78", "Outdoor", false, 1)
        )
        plantAdapter.submitList(sellerPlants)

        // Gallery Setup
        val galleryImages = listOf(
            "https://images.unsplash.com/photo-1614594975525-e45190c55d0b",
            "https://images.unsplash.com/photo-1599598477618-97fe494848f3",
            "https://images.unsplash.com/photo-1628175344321-72436f470559",
            "https://images.unsplash.com/photo-1512428813834-c702c7702b78",
            "https://images.unsplash.com/photo-1501004318641-72ee5feba2f1",
            "https://images.unsplash.com/photo-1485955900006-10f4d324d411"
        )
        galleryAdapter = GalleryAdapter(galleryImages)
        binding.rvGallery.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvGallery.adapter = galleryAdapter

        // Reviews Setup
        val reviewsList = listOf(
            Review("Dale Thiel", "11 months ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt", 5.0f),
            Review("Tiffany Nitzsche", "11 months ago", "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt", 5.0f),
            Review("Jacob Doe", "1 year ago", "Great service and healthy plants! Highly recommended.", 4.5f)
        )
        reviewAdapter = ReviewAdapter(reviewsList)
        binding.rvReviews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvReviews.adapter = reviewAdapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(requireContext(), "Share Seller Profile", Toast.LENGTH_SHORT).show()
        }
        
        binding.tvViewAll.setOnClickListener {
            Toast.makeText(requireContext(), "View All Plants", Toast.LENGTH_SHORT).show()
        }
        
        binding.ivChatSmall.setOnClickListener {
             Toast.makeText(requireContext(), "Chat with seller", Toast.LENGTH_SHORT).show()
        }
        
        binding.ivCallSmall.setOnClickListener {
             Toast.makeText(requireContext(), "Call seller", Toast.LENGTH_SHORT).show()
        }

        binding.tvGalleryViewAll.setOnClickListener {
            Toast.makeText(requireContext(), "View All Gallery", Toast.LENGTH_SHORT).show()
        }

        binding.btnAddReview.setOnClickListener {
            Toast.makeText(requireContext(), "Add Review Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                hideAllSections()
                when (tab?.position) {
                    0 -> binding.layoutPlantsList.visibility = View.VISIBLE
                    1 -> binding.layoutAbout.visibility = View.VISIBLE
                    2 -> binding.layoutGallery.visibility = View.VISIBLE
                    3 -> binding.layoutReviews.visibility = View.VISIBLE
                    else -> binding.layoutPlantsList.visibility = View.VISIBLE
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun hideAllSections() {
        binding.layoutPlantsList.visibility = View.GONE
        binding.layoutAbout.visibility = View.GONE
        binding.layoutGallery.visibility = View.GONE
        binding.layoutReviews.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
