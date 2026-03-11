package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.databinding.FragmentChooseShippingBinding
import com.rahul.natureplant.ui.adapter.ShippingTypeAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class ChooseShippingFragment : Fragment() {

    private var _binding: FragmentChooseShippingBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var shippingTypeAdapter: ShippingTypeAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseShippingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeShippingTypes()

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnApply.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRecyclerView() {
        shippingTypeAdapter = ShippingTypeAdapter { shippingType ->
            viewModel.selectShippingType(shippingType)
        }
        binding.rvShippingTypes.adapter = shippingTypeAdapter
    }

    private fun observeShippingTypes() {
        viewModel.shippingTypes.observe(viewLifecycleOwner) { types ->
            shippingTypeAdapter.submitList(types)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
