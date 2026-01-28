package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rahul.natureplant.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private var quantity = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plant = args.plant

        binding.apply {
            tvTitle.text = plant.name
            tvPlantName.text = plant.name
            tvDescription.text = plant.description
            tvPrice.text = "$${plant.price}"
            tvQuantity.text = quantity.toString()

            Glide.with(requireContext())
                .load(plant.imageUrl)
                .into(ivPlant)

            Glide.with(requireContext())
                .load(plant.imageUrl)
                .into(ivSmallPlant1)

            Glide.with(requireContext())
                .load(plant.imageUrl)
                .into(ivSmallPlant2)

            Glide.with(requireContext())
                .load(plant.imageUrl)
                .into(ivSmallPlant3)

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            ivPlus.setOnClickListener {
                quantity++
                tvQuantity.text = quantity.toString()
                val totalPrice = plant.price * quantity
                tvPrice.text = "$${totalPrice}"
            }

            ivMinus.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    tvQuantity.text = quantity.toString()
                    val totalPrice = plant.price * quantity
                    tvPrice.text = "$${totalPrice}"
                }
            }

            btnAddToCart.setOnClickListener {
                // Add to cart logic with quantity
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
