package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.rahul.natureplant.databinding.FragmentCartBinding

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        binding.btnCheckout.setOnClickListener {
            val checkoutBottomSheetFragment = CheckoutBottomSheetFragment()
            checkoutBottomSheetFragment.show(parentFragmentManager, CheckoutBottomSheetFragment.TAG)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
