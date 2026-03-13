package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.DialogPaymentSuccessBinding
import com.rahul.natureplant.viewmodel.PlantViewModel

class PaymentSuccessFragment : Fragment() {

    private var _binding: DialogPaymentSuccessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPaymentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Clear the cart since payment was successful
        viewModel.clearCart()

        // Animate the success icon and text
        val popIn = AnimationUtils.loadAnimation(requireContext(), R.anim.pop_in)
        binding.ivSuccessIcon.startAnimation(popIn)
        
        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_homeFragment)
        }

        binding.btnViewOrder.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_orderSummaryFragment)
        }

        binding.btnViewEReceipt.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_eReceiptFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
