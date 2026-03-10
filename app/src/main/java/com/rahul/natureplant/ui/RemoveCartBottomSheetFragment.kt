package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.LayoutRemoveCartBottomSheetBinding
import com.rahul.natureplant.model.Plant

class RemoveCartBottomSheetFragment(
    private val plant: Plant,
    private val onConfirmRemove: (Plant) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: LayoutRemoveCartBottomSheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutRemoveCartBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvPlantName.text = plant.name
        binding.tvCategory.text = plant.category
        binding.tvPlantPrice.text = "$${plant.price}.00"

        Glide.with(this)
            .load(plant.imageUrl)
            .placeholder(R.drawable.img_aloe)
            .error(R.drawable.img_aloe)
            .into(binding.ivPlant)

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        binding.btnRemove.setOnClickListener {
            onConfirmRemove(plant)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RemoveCartBottomSheetFragment"
    }
}
