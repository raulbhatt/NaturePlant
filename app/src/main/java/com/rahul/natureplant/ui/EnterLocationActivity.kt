package com.rahul.natureplant.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.rahul.natureplant.MainActivity
import com.rahul.natureplant.R

class EnterLocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_location)

        val enterLocationLayout = findViewById<LinearLayout>(R.id.ll_enter_location)
        enterLocationLayout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("NAVIGATE_TO", "HOME")
            startActivity(intent)
        }
    }
}