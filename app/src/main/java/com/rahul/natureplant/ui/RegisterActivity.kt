package com.rahul.natureplant.ui

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSignUp.setOnClickListener {
            handleSignUp()
        }

        binding.tvSignIn.setOnClickListener {
            finish() // Returns to the previous activity
        }
    }

    private fun handleSignUp() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val isAgreed = binding.cbTerms.isChecked

        if (name.isEmpty()) {
            binding.etName.error = "Please enter your name"
            binding.etName.requestFocus()
            return
        }

        if (phone.isEmpty() || phone.length < 10) {
            binding.etPhone.error = "Please enter a valid phone number"
            binding.etPhone.requestFocus()
            return
        }

        if (!isValidEmail(email)) {
            binding.etEmail.error = "Please enter a valid email address"
            binding.etEmail.requestFocus()
            return
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            binding.etPassword.requestFocus()
            return
        }

        if (!isAgreed) {
            Toast.makeText(this, "Please agree to Terms and Conditions", Toast.LENGTH_SHORT).show()
            return
        }

        // Success logic
        showSuccessDialog()
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_registration_success, null)
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnOk = dialogView.findViewById<Button>(R.id.btn_ok)

        btnOk.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, VerificationCodeActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}