package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentTrackOrderBinding

class TrackOrderFragment : Fragment() {

    private var _binding: FragmentTrackOrderBinding? = null
    private val binding get() = _binding!!
    private val args: TrackOrderFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plant = args.plant

        binding.tvPlantName.text = plant.name
        binding.tvPlantCategoryQty.text = "${plant.category} | Qty. : ${String.format("%02d", plant.quantity)} pcs"
        binding.tvPlantPrice.text = "$${String.format("%.2f", (plant.price * plant.quantity).toDouble())}"

        Glide.with(this)
            .load(plant.imageUrl)
            .placeholder(R.drawable.img_aloe)
            .into(binding.ivPlant)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCancelOrder.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
