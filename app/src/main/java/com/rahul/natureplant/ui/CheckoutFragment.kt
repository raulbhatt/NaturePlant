package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentCheckoutBinding
import com.rahul.natureplant.ui.adapter.CheckoutAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var checkoutAdapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeData()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnChangeAddress.setOnClickListener {
            findNavController().navigate(R.id.action_checkoutFragment_to_shippingAddressFragment)
        }

        binding.btnContinueToPayment.setOnClickListener {
            // Navigate to payment or show bottom sheet
            val paymentMethodBottomSheetFragment = PaymentMethodBottomSheetFragment()
            paymentMethodBottomSheetFragment.show(parentFragmentManager, PaymentMethodBottomSheetFragment.TAG)
        }
    }

    private fun setupRecyclerView() {
        checkoutAdapter = CheckoutAdapter()
        binding.rvOrderList.adapter = checkoutAdapter
    }

    private fun observeData() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            checkoutAdapter.submitList(items)
        }

        viewModel.selectedAddress.observe(viewLifecycleOwner) { address ->
            binding.tvAddressTitle.text = address.title
            binding.tvAddressDetail.text = address.detail
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
