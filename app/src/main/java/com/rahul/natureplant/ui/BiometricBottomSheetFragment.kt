package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rahul.natureplant.databinding.BottomSheetBiometricBinding

class BiometricBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: BottomSheetBiometricBinding? = null
    private val binding get() = _binding!!

    private var onEnableClickListener: (() -> Unit)? = null

    fun setOnEnableClickListener(listener: () -> Unit) {
        onEnableClickListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetBiometricBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnEnableBiometric.setOnClickListener {
            onEnableClickListener?.invoke()
            dismiss()
        }

        binding.btnCancelBiometric.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}