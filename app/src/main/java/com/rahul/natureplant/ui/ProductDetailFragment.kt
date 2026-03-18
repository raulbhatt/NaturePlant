package com.rahul.natureplant.ui

import android.annotation.SuppressLint
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
import android.widget.Toast
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

        setupUI(plant)
        observeWishlist(plant)
    }

    private fun setupUI(plant: Plant) {
        binding.apply {
            tvTitle.text = "Plant Details"
            tvPlantName.text = plant.name
            tvDescBody.text = plant.description
            tvCategory.text = plant.category
            tvRating.text = plant.rating.toString()
            updatePrice(plant)

            Glide.with(requireContext())
                .load(plant.imageUrl)
                .placeholder(R.drawable.img_aloe)
                .into(ivPlant)

            ivBack.setOnClickListener {
                findNavController().navigateUp()
            }

            btnAddToCart.setOnClickListener {
                plantViewModel.addToCart(plant, quantity)
                showAnimatedSuccessDialog()
            }

            btnWishlist.setOnClickListener {
                plantViewModel.toggleWishlist(plant)
                val isFavorite = plantViewModel.wishlistItems.value?.any { it.id == plant.id } == true
                val message = if (isFavorite) "Added to wishlist" else "Removed from wishlist"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }

            sellerInfo.setOnClickListener {
                findNavController().navigate(R.id.action_productDetailFragment_to_sellerProfileFragment)
            }
            
            ivChat.setOnClickListener {
                Toast.makeText(requireContext(), "Chat with seller", Toast.LENGTH_SHORT).show()
            }
            
            ivCall.setOnClickListener {
                Toast.makeText(requireContext(), "Call seller", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeWishlist(plant: Plant) {
        plantViewModel.wishlistItems.observe(viewLifecycleOwner) { wishlist ->
            val isFavorite = wishlist.any { it.id == plant.id }
            if (isFavorite) {
                binding.btnWishlist.setColorFilter(requireContext().getColor(R.color.red))
            } else {
                binding.btnWishlist.setColorFilter(requireContext().getColor(R.color.light_gray))
            }
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updatePrice(plant: Plant) {
        val totalPrice = plant.price * quantity
        binding.tvTotalPrice.text = String.format("$%.2f", totalPrice.toDouble())
    }

    private fun showAnimatedSuccessDialog() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_animated_success)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
                findNavController().navigateUp()
            }
        }, 2000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
