package com.rahul.natureplant.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.core.graphics.toColorInt
import com.rahul.natureplant.databinding.ItemMessageBinding
import com.rahul.natureplant.ui.Message

class ChatAdapter(private val messages: List<Message>) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.senderTextView.text = message.sender
            binding.messageTextView.text = message.text

            // Dynamic styling for WhatsApp-like appearance
            val layoutParams = binding.messageCard.layoutParams as ConstraintLayout.LayoutParams
            if (message.sender == "User") {
                // Right-align user messages
                layoutParams.startToStart = -1 // Unset start constraint
                layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID // Reference parent
                layoutParams.leftMargin = 48 // Margin from left for balance
                layoutParams.rightMargin = 8 // Closer to right edge
                binding.messageCard.setCardBackgroundColor(Color.parseColor("#DCF8C6")) // WhatsApp green
            } else {
                // Left-align AI messages
                layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID // Reference parent
                layoutParams.endToEnd = -1 // Unset end constraint
                layoutParams.leftMargin = 8 // Closer to left edge
                layoutParams.rightMargin = 48 // Margin from right for balance
                binding.messageCard.setCardBackgroundColor("#FFFFFF".toColorInt()) // White for AI
            }
            binding.messageCard.layoutParams = layoutParams
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messages[position])
    }

    override fun getItemCount() = messages.size
}