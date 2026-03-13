package com.rahul.natureplant

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rahul.natureplant.databinding.ActivityMainBinding
import com.rahul.natureplant.model.Location

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the navigation controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        handleIntent(intent)

        // Set up the bottom navigation view with navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Set up the AppBarConfiguration for top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.categoryFragment, R.id.wishlistFragment, R.id.cartFragment, R.id.profileFragment)
        )

        binding.fabChat.setOnClickListener {
            navController.navigate(R.id.chatFragment)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.splashFragment, R.id.loginFragment, R.id.paymentSuccessFragment -> {
                    binding.bottomNavigation.visibility = View.GONE
                    binding.fabChat.visibility = View.GONE
                }
                R.id.chatFragment -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.fabChat.visibility = View.GONE
                }
                else -> {
                    binding.bottomNavigation.visibility = View.VISIBLE
                    binding.fabChat.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.let {
            val encryptedInfo = it.getStringExtra("encryptedInfo")
            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelableExtra("location", Location::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.getParcelableExtra("location")
            }
            val navigateTo = it.getStringExtra("NAVIGATE_TO")
            val fromNotification = it.getBooleanExtra("from_notification", false)

            Log.d("MainActivity", "Encrypted Info: $encryptedInfo")

            if (encryptedInfo != null || navigateTo == "HOME" || fromNotification) {
                binding.root.post {
                    val bundle = Bundle()
                    bundle.putParcelable("location", location)
                    navController.navigate(R.id.homeFragment, bundle)
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}