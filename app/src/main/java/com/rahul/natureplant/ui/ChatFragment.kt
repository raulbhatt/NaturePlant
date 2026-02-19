package com.rahul.natureplant.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.HarmBlockThreshold
import com.google.firebase.ai.type.HarmCategory
import com.google.firebase.ai.type.SafetySetting
import com.google.firebase.ai.type.content
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentChatBinding
import com.rahul.natureplant.ui.adapter.ChatAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class Message(val sender: String, val text: String)

class ChatFragment : Fragment() {

    private lateinit var generativeModel: GenerativeModel
    private val messages = mutableListOf<Message>()
    private lateinit var adapter: ChatAdapter // Define below
    private lateinit var binding: FragmentChatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        binding = FragmentChatBinding.bind(view)
        initializeView(binding)
        return view
    }

    private fun initializeView(binding : FragmentChatBinding) {
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ChatAdapter(messages)
        binding.chatRecyclerView.adapter = adapter

        // Initialize Gemini Model
        generativeModel = FirebaseAI.getInstance(FirebaseApp.getInstance())
            .generativeModel(
                modelName = "gemini-2.5-flash-lite",
                // Optional: Safety settings to block harmful content
                safetySettings = listOf(
                    SafetySetting(
                        HarmCategory.HATE_SPEECH,
                        HarmBlockThreshold.MEDIUM_AND_ABOVE
                    ),
                    SafetySetting(HarmCategory.DANGEROUS_CONTENT, HarmBlockThreshold.MEDIUM_AND_ABOVE)
                    // Add more as needed
                )
            )

        // Start a chat session (with optional history)
        val chat = generativeModel.startChat(
            history = listOf(
                content(role = "user") { text("You are a helpful AI assistant.") },
                content(role = "model") { text("Got it! How can I help?") }
            )
        )

        // Send button click
        binding.sendButton.setOnClickListener {
            val userMessage = binding.messageEditText.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                messages.add(Message("User", userMessage))
                adapter.notifyItemInserted(messages.size - 1)
                binding.chatRecyclerView.smoothScrollToPosition(messages.size - 1)
                binding.messageEditText.setText("")

                // Send to Gemini in background
                GlobalScope.launch {
                    CoroutineScope(coroutineContext).launch {
                        try {
                            val response = chat.sendMessage(userMessage)
                            withContext(Dispatchers.IO) {
                                messages.add(Message("AI", response.text ?: "No response"))
                                adapter.notifyItemInserted(messages.size - 1)
                                binding.chatRecyclerView.scrollToPosition(messages.size - 1)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                //Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                }
            }
        }
    }
}
