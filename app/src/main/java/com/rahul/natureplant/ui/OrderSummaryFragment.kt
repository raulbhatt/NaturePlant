package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.databinding.FragmentOrderSummaryBinding
import com.rahul.natureplant.ui.adapter.CheckoutAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class OrderSummaryFragment : Fragment() {

    private var _binding: FragmentOrderSummaryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var adapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnContinue.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        adapter = CheckoutAdapter()
        binding.rvItems.adapter = adapter
    }

    private fun observeViewModel() {
        viewModel.lastOrderItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            calculateSummary(items)
        }
        
        viewModel.selectedShippingType.observe(viewLifecycleOwner) { shipping ->
            binding.tvDeliveryType.text = shipping.title
            binding.tvDeliveryCharge.text = "$${String.format("%.2f", shipping.price)}"
            updateTotal()
        }
    }

    private fun calculateSummary(items: List<com.rahul.natureplant.model.Plant>) {
        val amount = items.sumOf { it.price * it.quantity }.toDouble()
        binding.tvAmount.text = "$${String.format("%.2f", amount)}"
        updateTotal()
    }

    private fun updateTotal() {
        val amountStr = binding.tvAmount.text.toString().replace("$", "")
        val deliveryStr = binding.tvDeliveryCharge.text.toString().replace("$", "")
        val taxStr = binding.tvTax.text.toString().replace("$", "")
        
        val amount = amountStr.toDoubleOrNull() ?: 0.0
        val delivery = deliveryStr.toDoubleOrNull() ?: 0.0
        val tax = taxStr.toDoubleOrNull() ?: 0.0
        
        // Total is not explicitly shown in the image bottom but usually it's there. 
        // The image shows Amount, Delivery Charge, Tax.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
