package com.rahul.natureplant.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentCartBinding
import com.rahul.natureplant.ui.adapter.CartAdapter
import com.rahul.natureplant.viewmodel.PlantViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlantViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        setupRecyclerView()
        observeCartItems()

        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(R.id.action_cartFragment_to_checkoutFragment)
        }

        return binding.root
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(
            onRemoveClick = { plant ->
                val removeBottomSheet = RemoveCartBottomSheetFragment(plant) {
                    viewModel.removeFromCart(it)
                }
                removeBottomSheet.show(parentFragmentManager, RemoveCartBottomSheetFragment.TAG)
            },
            onIncreaseQuantity = { viewModel.increaseQuantity(it) },
            onDecreaseQuantity = { viewModel.decreaseQuantity(it) }
        )
        binding.rvCart.adapter = cartAdapter
    }

    private fun observeCartItems() {
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            if (cartItems.isNullOrEmpty()) {
                binding.llEmptyCart.visibility = View.VISIBLE
                binding.rvCart.visibility = View.GONE
                binding.tvOrderList.visibility = View.GONE
            } else {
                binding.llEmptyCart.visibility = View.GONE
                binding.rvCart.visibility = View.VISIBLE
                binding.tvOrderList.visibility = View.VISIBLE
                binding.tvOrderList.text = getString(R.string.order_list_items, cartItems.size)
                cartAdapter.submitList(cartItems.toList())
            }
            updateSummary(cartItems)
        }
    }

    private fun updateSummary(cartItems: List<com.rahul.natureplant.model.Plant>?) {
        val subtotal = cartItems?.sumOf { (it.price * it.quantity).toDouble() } ?: 0.0
        val deliveryFee = 10.0
        val tax = 5.0
        val totalAmount = subtotal + deliveryFee + tax

        binding.tvSubtotal.text = getString(R.string.subtotal, subtotal)
        binding.tvDeliveryFee.text = getString(R.string.delivery_fee, deliveryFee)
        binding.tvTax.text = getString(R.string.tax, tax)
        binding.tvTotalAmount.text = getString(R.string.total_amount, totalAmount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
