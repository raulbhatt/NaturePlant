package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentLeaveReviewBinding

class LeaveReviewFragment : Fragment() {

    private var _binding: FragmentLeaveReviewBinding? = null
    private val binding get() = _binding!!

    private val args: LeaveReviewFragmentArgs by navArgs()

    override fun onCreateView(
        LayoutInflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaveReviewBinding.inflate(LayoutInflater, container, false)
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
            .error(R.drawable.img_aloe)
            .into(binding.ivPlant)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnSubmit.setOnClickListener {
            val rating = binding.ratingBar.rating
            val review = binding.etReview.text.toString()

            if (rating == 0f) {
                Toast.makeText(requireContext(), "Please provide a rating", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }
        }
        
        binding.btnAddPhoto.setOnClickListener {
            Toast.makeText(requireContext(), "Add photo clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnReorder.setOnClickListener {
             Toast.makeText(requireContext(), "Re-order clicked", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
