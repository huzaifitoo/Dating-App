package com.example.datingapp.auths

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnOtp.setOnClickListener {
            if (binding.userNo.text!!.isEmpty()) {
                binding.userNo.error = "please enter your no."
            } else {
                sentOtp(binding.userNo.text.toString())
            }

        }

        binding.btnLogin.setOnClickListener {
            if (binding.userOtp.text!!.isEmpty()) {
                binding.userOtp.error = "please enter your no."
            } else {
                verifyOtp(binding.userOtp.text.toString())
            }

        }


    }

    private fun sentOtp(number: String) {

    }

    private fun verifyOtp(otp: String) {

    }
}
