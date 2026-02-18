package com.rahul.natureplant.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.rahul.natureplant.databinding.ActivityVerificationCodeBinding
import java.util.concurrent.TimeUnit

class VerificationCodeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVerificationCodeBinding
    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerificationCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Retrieve the phone number from the intent
        //phoneNumber = intent.getStringExtra("phone") ?: ""
        phoneNumber = "+917086867183"

        if (phoneNumber.isNotEmpty()) {
            sendVerificationCode(phoneNumber)
        }

        binding.verifyButton.setOnClickListener {
            val code = binding.otpEditText1.text.toString() +
                    binding.otpEditText2.text.toString() +
                    binding.otpEditText3.text.toString() +
                    binding.otpEditText4.text.toString()

            if (code.length == 4 && verificationId != null) {
                val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
                signInWithPhoneAuthCredential(credential)
            } else {
                Toast.makeText(this, "Please enter a valid code", Toast.LENGTH_SHORT).show()
            }
        }

        binding.resendCode.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                sendVerificationCode(phoneNumber)
            }
        }

        setupOtpEditTexts()
    }

    private fun sendVerificationCode(phoneNumber: String) {
        binding.progressBar.visibility = View.VISIBLE
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phoneNumber, // Phone number to verify
            60, // Timeout duration
            TimeUnit.SECONDS, // Unit of timeout
            this, // Activity (for callback binding)
            callbacks // OnVerificationStateChangedCallbacks
        )
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            signInWithPhoneAuthCredential(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("VerificationCodeActivity", "onVerificationFailed", e)
            binding.progressBar.visibility = View.GONE
            Toast.makeText(applicationContext, e.message, Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@VerificationCodeActivity.verificationId = verificationId
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val intent = Intent(this, LocationInformationActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
                binding.progressBar.visibility = View.GONE
            }
    }

    private fun setupOtpEditTexts() {
        binding.otpEditText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    binding.otpEditText2.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.otpEditText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    binding.otpEditText3.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.otpEditText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    binding.otpEditText4.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }
}
