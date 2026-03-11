package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.DialogPaymentSuccessBinding

class PaymentSuccessFragment : Fragment() {

    private var _binding: DialogPaymentSuccessBinding? = null
    private val binding get() = _binding!!

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

        binding.btnBack.setOnClickListener {
            findNavController().navigate(R.id.action_paymentSuccessFragment_to_homeFragment)
        }

        binding.btnViewOrder.setOnClickListener {
            // Navigate to order list or detail
        }

        binding.btnViewEReceipt.setOnClickListener {
            // Show receipt
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
