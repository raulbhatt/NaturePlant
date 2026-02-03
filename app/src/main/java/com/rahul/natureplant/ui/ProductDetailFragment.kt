package com.rahul.natureplant.ui

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentProductDetailBinding
import com.rahul.natureplant.model.Plant
import com.rahul.natureplant.viewmodel.PlantViewModel

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private val args: ProductDetailFragmentArgs by navArgs()
    private var quantity = 1
    private lateinit var plantViewModel: PlantViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        plantViewModel = ViewModelProvider(requireActivity())[PlantViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plant = args.plant

        binding.apply {
            tvTitle.text = plant.name
            tvPlantName.text = plant.name
            tvDescription.text = plant.description
            updatePrice(plant)
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
                updatePrice(plant)
            }

            ivMinus.setOnClickListener {
                if (quantity > 1) {
                    quantity--
                    tvQuantity.text = quantity.toString()
                    updatePrice(plant)
                }
            }

            btnAddToCart.setOnClickListener {
                plantViewModel.addToCart(plant, quantity)
                showAnimatedSuccessDialog()
            }
        }
    }

    private fun updatePrice(plant: Plant) {
        val totalPrice = plant.price * quantity
        binding.tvPrice.text = String.format("$%.2f", totalPrice.toDouble())
    }

    private fun showAnimatedSuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_animated_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Automatically dismiss the dialog after a short delay
        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
                findNavController().navigateUp()
            }
        }, 2000) // 2 seconds delay
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
