package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentShippingAddressBinding
import com.rahul.natureplant.ui.adapter.AddressAdapter
import com.rahul.natureplant.util.SharedPrefManager
import com.rahul.natureplant.viewmodel.PlantViewModel

class ShippingAddressFragment : Fragment() {

    private var _binding: FragmentShippingAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var sharedPrefManager: SharedPrefManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShippingAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedPrefManager = SharedPrefManager(requireContext())
        
        // Fetch saved location and add it to the view model's address list
        sharedPrefManager.getLocation()?.let { location ->
            viewModel.addCurrentLocationAddress(location.address)
        }

        setupRecyclerView()
        observeAddresses()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnApply.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnAddAddress.setOnClickListener {
            findNavController().navigate(R.id.action_shippingAddressFragment_to_addAddressFragment)
        }
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter { address ->
            viewModel.selectAddress(address)
            sharedPrefManager.saveSelectedAddress(address)
        }
        binding.rvAddresses.adapter = addressAdapter
    }

    private fun observeAddresses() {
        viewModel.addresses.observe(viewLifecycleOwner) { addresses ->
            addressAdapter.submitList(addresses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
