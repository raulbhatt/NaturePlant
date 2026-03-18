package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.rahul.natureplant.databinding.FragmentOrderHistoryBinding
import com.rahul.natureplant.model.Plant
import com.rahul.natureplant.ui.adapter.OrderHistoryAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class OrderHistoryFragment : Fragment() {

    private var _binding: FragmentOrderHistoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupTabs()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter(
            onTrackOrderClick = { plant ->
                val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToTrackOrderFragment(plant)
                findNavController().navigate(action)
            },
            onLeaveReviewClick = { plant ->
                val action = OrderHistoryFragmentDirections.actionOrderHistoryFragmentToLeaveReviewFragment(plant)
                findNavController().navigate(action)
            },
            onReorderClick = { plant ->
                Toast.makeText(requireContext(), "Re-ordering ${plant.name}", Toast.LENGTH_SHORT).show()
            }
        )
        binding.rvOrders.adapter = adapter
    }

    private fun setupTabs() {
        val demoActiveOrders = listOf(
            Plant(101, "Monstera Deliciosa Plant", 25, 4.5, 100, "Indoor plant", "https://images.unsplash.com/photo-1614594975525-e45190c55d0b", "Indoor", false, 2),
            Plant(102, "Watermelon Peperomia", 25, 4.5, 100, "Indoor plant", "https://images.unsplash.com/photo-1599598477618-97fe494848f3", "Indoor", false, 2),
            Plant(103, "Calathea Medallion", 22, 4.5, 100, "Indoor plant", "https://images.unsplash.com/photo-1628175344321-72436f470559", "Indoor", false, 2),
            Plant(104, "Chlorophytum", 35, 4.5, 100, "Indoor plant", "https://images.unsplash.com/photo-1599598477618-97fe494848f3", "Indoor", false, 2),
            Plant(105, "Pepper Face Pl..", 20, 4.5, 100, "Indoor plant", "https://images.unsplash.com/photo-1628175344321-72436f470559", "Indoor", false, 2)
        )

        val demoCompletedOrders = listOf(
            Plant(201, "Cylindrical Snake plant", 24, 4.8, 150, "Hardy indoor plant", "https://images.unsplash.com/photo-1614594975525-e45190c55d0b", "Indoor", false, 1),
            Plant(202, "Watermelon Peperomia", 25, 4.7, 85, "Indoor plant", "https://images.unsplash.com/photo-1599598477618-97fe494848f3", "Indoor", false, 2),
            Plant(203, "Birds Nest", 22, 4.6, 90, "Fern plant", "https://images.unsplash.com/photo-1628175344321-72436f470559", "Indoor", false, 2)
        )

        val demoCancelledOrders = listOf(
            Plant(301, "Calathea Medallion", 22, 4.6, 90, "Indoor plant", "https://images.unsplash.com/photo-1628175344321-72436f470559", "Indoor", false, 1),
            Plant(302, "Pepper Face Pl..", 20, 4.4, 75, "Small indoor plant", "https://images.unsplash.com/photo-1512428813834-c702c7702b78", "Indoor", false, 2),
            Plant(303, "Monstera Deliciosa Plant", 25, 4.8, 120, "Beautiful indoor plant", "https://images.unsplash.com/photo-1614594975525-e45190c55d0b", "Indoor", false, 1)
        )

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        adapter.tabType = 0
                        adapter.submitList(demoActiveOrders)
                    }
                    1 -> {
                        adapter.tabType = 1
                        adapter.submitList(demoCompletedOrders)
                    }
                    2 -> {
                        adapter.tabType = 2
                        adapter.submitList(demoCancelledOrders)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        // Set default selection
        adapter.tabType = 0
        adapter.submitList(demoActiveOrders)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
