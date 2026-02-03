package com.rahul.natureplant.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rahul.natureplant.MainActivity
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentLoginBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.security.KeyStore
import java.util.concurrent.Executor
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey


class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var loadingDialog: AlertDialog? = null
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo
    private val KEY_NAME = "my_key"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        executor = ContextCompat.getMainExecutor(requireContext())

        return binding.root
    }

    private fun showBiometricPrompt() {
        val biometricBottomSheet = BiometricBottomSheetFragment()
        biometricBottomSheet.setOnEnableClickListener {
            showBiometricLogin()
        }
        biometricBottomSheet.show(parentFragmentManager, biometricBottomSheet.tag)
    }

    private fun showBiometricLogin() {

        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Authentication Successfull!", Toast.LENGTH_SHORT).show()
                    if (result.cryptoObject != null) {
                        try {
                            val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal("Hello World".toByteArray())
                            val intent = Intent(this@LoginFragment.requireContext(), MainActivity::class.java)
                            intent.putExtra("encryptedInfo", encryptedInfo.toString())
                            startActivity(intent)
                            //finish()
                        } catch (e: Exception) {
                            Log.d("LoginFragment", "Error encrypting data: ${e.message}")
                        }
                    } else {
                        Log.d("LoginFragment", "Encrypted Info is null")
                    }
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    //Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            })

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Enable Fingerprint Authentication")
            .setSubtitle("Log in using your Biometric credentials")
            .setNegativeButtonText("Use Email/password")
            .build()

        try {
            generateSecretKey(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
        } catch (e: Exception) {
            // Handle key generation exception
            Toast.makeText(context, "Error generating key: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        val biometricManager = BiometricManager.from(requireActivity())
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                try {
                    val cipher = getCipher()
                    val secretKey = getSecretKey()
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
                    biometricPrompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
                } catch (e: Exception) {
                    // Handle exceptions
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(context, "No biometric features available on this device.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(context, "Biometric features are currently unavailable.", Toast.LENGTH_SHORT).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                val enrollIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
                    putExtra(
                        Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL
                    )
                }
                startActivityForResult(enrollIntent, 100)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail.text?.toString()
            val password = binding.etPassword.text?.toString()

            if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
            } else {
                handleSignIn()
            }
        }

        binding.btnGoogleSignIn.setOnClickListener {

        }

        binding.ivFingerprint.setOnClickListener {
            //showBiometricPrompt()
        }
        showBiometricPrompt()

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }
    }



    /**
     * Handles the sign-in button click, validates input, and navigates if successful.
     */
    private fun handleSignIn() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        // 1. Validate the inputs
        if (!isValidEmail(email)) {
            binding.etEmail.error = "Enter a valid email address"
            // Request focus on the field with the error
            binding.etEmail.requestFocus()
            return // Stop the function here
        }

        if (!isValidPassword(password)) {
            binding.etPassword.error = "Password must be longer than 6 characters"
            binding.etPassword.requestFocus()
            return // Stop the function here
        }

        // 2. If validation is successful, proceed with navigation
        showLoadingDialog()

        lifecycleScope.launch {
            delay(2000) // 2 seconds delay
            hideLoadingDialog()
            Toast.makeText(requireContext(), "Sign-in successful!", Toast.LENGTH_SHORT).show()
            // Navigate to the next destination
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val dialogView = layoutInflater.inflate(R.layout.dialog_loading, null)
            loadingDialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create()
            loadingDialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
        loadingDialog?.show()
        val loadingIcon = loadingDialog?.findViewById<ImageView>(R.id.iv_loading)
        val rotation = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate)
        loadingIcon?.startAnimation(rotation)
    }

    private fun hideLoadingDialog() {
        val loadingIcon = loadingDialog?.findViewById<ImageView>(R.id.iv_loading)
        loadingIcon?.clearAnimation()
        loadingDialog?.dismiss()
    }

    /**
     * Checks if the provided email string is in a valid format.
     * @return True if the email is valid, false otherwise.
     */
    private fun isValidEmail(email: String): Boolean {
        // Patterns.EMAIL_ADDRESS is a standard Android utility for email validation.
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Checks if the provided password meets the length requirement.
     * @return True if the password is valid, false otherwise.
     */
    private fun isValidPassword(password: String): Boolean {
        return password.length > 6
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)
        return keyStore.getKey(KEY_NAME, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }


}
