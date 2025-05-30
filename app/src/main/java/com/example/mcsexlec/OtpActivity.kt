package com.example.mcsexlec

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class OtpActivity : AppCompatActivity() {

    private lateinit var etOtp: EditText
    private lateinit var btnVerifyOtp: Button
    private lateinit var tvResendOtp: TextView
    private lateinit var tvOtpError: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        // Initialize views
        etOtp = findViewById(R.id.etOtp)
        btnVerifyOtp = findViewById(R.id.btnVerifyOtp)
        tvResendOtp = findViewById(R.id.tvResendOtp)
        tvOtpError = findViewById(R.id.tvOtpError)

        // Set up button click listener for OTP verification
        btnVerifyOtp.setOnClickListener {
            val otp = etOtp.text.toString()

            // Validate OTP input
            if (otp.isEmpty()) {
                showError("OTP must be filled!")
            } else if (otp == "123456") { // Gimmick OTP validation
                Toast.makeText(this, "OTP Verified", Toast.LENGTH_SHORT).show()
//                ?val intent = Intent(this, HomeActivity::class.java)
                startActivity(intent)
                // Navigate to Home Activity or Dashboard (you can replace this with your target activity)
                // val intent = Intent(this, HomeActivity::class.java)
                // startActivity(intent)
                finish() // Close OTP page and return to the previous screen
            } else {
                showError("Invalid OTP!")
            }
        }

        // Set up click listener for Resend OTP link
        tvResendOtp.setOnClickListener {
            // Handle OTP resend (for now, just show a toast)
            Toast.makeText(this, "OTP Sent Again", Toast.LENGTH_SHORT).show()
            // Reset OTP field
            etOtp.text.clear()
        }
    }

    // Show error message and make it visible
    private fun showError(message: String) {
        tvOtpError.text = message
        tvOtpError.visibility = View.VISIBLE
    }
}