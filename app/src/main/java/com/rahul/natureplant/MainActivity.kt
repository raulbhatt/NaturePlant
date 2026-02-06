package com.rahul.natureplant

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.rahul.natureplant.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var encryptedInfo : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        encryptedInfo = intent.getStringExtra("encryptedInfo")
        val navigateTo = intent.getStringExtra("NAVIGATE_TO")

        Log.d("MainActivity", "Encrypted Info: $encryptedInfo")

        // Set up the navigation controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        if (encryptedInfo != null || navigateTo == "HOME") {
            navController.navigate(R.id.homeFragment)
        }

        // Set up the bottom navigation view with navController
        binding.bottomNavigation.setupWithNavController(navController)

        // Set up the AppBarConfiguration for top-level destinations
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.categoryFragment, R.id.cartFragment, R.id.profileFragment)
        )

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.splashFragment || destination.id == R.id.loginFragment) {
                binding.bottomNavigation.visibility = View.GONE
            } else {
                binding.bottomNavigation.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
