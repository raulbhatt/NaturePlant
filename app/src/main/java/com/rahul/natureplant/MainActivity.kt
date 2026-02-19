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
            setOf(R.id.homeFragment, R.id.categoryFragment, R.id.cartFragment, R.id.profileFragment, R.id.chatFragment)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment || destination.id == R.id.loginFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            handleIntent(intent)
        }
    }

    private fun handleIntent(intent: Intent) {
        val encryptedInfo = intent.getStringExtra("encryptedInfo")
        val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("location", Location::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("location")
        }
        val navigateTo = intent.getStringExtra("NAVIGATE_TO")
        val fromNotification = intent.getBooleanExtra("from_notification", false)

        Log.d("MainActivity", "Encrypted Info: $encryptedInfo")

        if (encryptedInfo != null || navigateTo == "HOME" || fromNotification) {
            binding.root.post {
                val bundle = Bundle()
                bundle.putParcelable("location", location)
                navController.navigate(R.id.homeFragment, bundle)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
