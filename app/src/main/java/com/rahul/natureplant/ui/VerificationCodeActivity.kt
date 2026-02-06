package com.rahul.natureplant.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.rahul.natureplant.R

class VerificationCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_code)

        val verifyButton = findViewById<Button>(R.id.verifyButton)
        verifyButton.setOnClickListener {
            val intent = Intent(this, LocationInformationActivity::class.java)
            startActivity(intent)
        }
    }
}