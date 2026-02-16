package com.rahul.natureplant.ui

import android.app.Activity
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.rahul.natureplant.MainActivity
import com.rahul.natureplant.R
import com.rahul.natureplant.databinding.FragmentLoginBinding
import com.rahul.natureplant.util.SharedPreferenceManager
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
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    private val keyName = "my_key"
    private lateinit var googleSignInClient: GoogleSignInClient

    private val googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    Toast.makeText(requireContext(), "Google Sign-in successful!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            } catch (e: ApiException) {
                Log.w("LoginFragment", "Google sign in failed, status code: ${e.statusCode}", e)
                Toast.makeText(requireContext(), "Google Sign-in failed. Error: ${e.statusCode}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "Google Sign-in cancelled or failed.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        executor = ContextCompat.getMainExecutor(requireContext())
        sharedPreferenceManager = SharedPreferenceManager(requireContext())

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
                    //Toast.makeText(context, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    Toast.makeText(context, "Authentication Successfull!", Toast.LENGTH_SHORT).show()
                    sharedPreferenceManager.setBiometricEnabled(true)
                    if (result.cryptoObject != null) {
                        try {
                            val encryptedInfo: ByteArray? = result.cryptoObject?.cipher?.doFinal("Hello World".toByteArray())
                            val intent = Intent(this@LoginFragment.requireContext(), MainActivity::class.java)
                            intent.putExtra("encryptedInfo", encryptedInfo.toString())
                            startActivity(intent)
                        } catch (e: Exception) {
                            Log.d("LoginFragment", "Error encrypting data: ${e.message}")
                        }
                    } else {
                        Log.d("LoginFragment", "Encrypted Info is null")
                    }
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
                    keyName,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
        } catch (e: Exception) {
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
                startActivity(enrollIntent)
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

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
            handleGoogleSignIn()
        }

        binding.ivFingerprint.setOnClickListener {
            if (sharedPreferenceManager.isBiometricEnabled()) {
                showBiometricLogin()
            } else {
                showBiometricPrompt()
            }
        }

        if (sharedPreferenceManager.isBiometricEnabled()) {
            showBiometricLogin()
        } else {
            showBiometricPrompt()
        }

        binding.tvSignUp.setOnClickListener {
            val intent = Intent(requireContext(), RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun handleGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignIn() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (!isValidEmail(email)) {
            binding.etEmail.error = "Enter a valid email address"
            binding.etEmail.requestFocus()
            return
        }

        if (!isValidPassword(password)) {
            binding.etPassword.error = "Password must be longer than 6 characters"
            binding.etPassword.requestFocus()
            return
        }

        showLoadingDialog()

        lifecycleScope.launch {
            delay(2000) // 2 seconds delay
            hideLoadingDialog()
            Toast.makeText(requireContext(), "Sign-in successful!", Toast.LENGTH_SHORT).show()
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

    private fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

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
        return keyStore.getKey(keyName, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }
}
