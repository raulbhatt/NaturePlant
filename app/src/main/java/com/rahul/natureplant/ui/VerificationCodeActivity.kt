package com.rahul.natureplant.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.rahul.natureplant.R

class VerificationCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification_code)

        val otpEditText1 = findViewById<EditText>(R.id.otp_edit_text1)
        val otpEditText2 = findViewById<EditText>(R.id.otp_edit_text2)
        val otpEditText3 = findViewById<EditText>(R.id.otp_edit_text3)
        val otpEditText4 = findViewById<EditText>(R.id.otp_edit_text4)
        val verifyButton = findViewById<Button>(R.id.verifyButton)

        otpEditText1.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    otpEditText2.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        otpEditText2.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    otpEditText3.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        otpEditText3.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    otpEditText4.requestFocus()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        otpEditText4.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s?.length == 1) {
                    verifyButton.performClick()
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        verifyButton.setOnClickListener {
            val intent = Intent(this, LocationInformationActivity::class.java)
            startActivity(intent)
        }
    }
}