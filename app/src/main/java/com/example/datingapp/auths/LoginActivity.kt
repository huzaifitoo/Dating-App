package com.example.datingapp.auths

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.datingapp.MainActivity
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        dialog = AlertDialog.Builder(this).setView(R.layout.loading_layout)
            .setCancelable(false)
            .create()

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

        dialog.show()
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                //    signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {


            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {

                this@LoginActivity.verificationId = verificationId

                dialog.dismiss()

                binding.numberLayout.visibility = GONE
                binding.otpLayout.visibility = VISIBLE

            }
        }
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber("+91$number")       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }

    private fun verifyOtp(otp: String) {

        dialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)

        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    checkUserExist(binding.userNo.text.toString())
                //    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                  //  finish()
                } else {

                    Toast.makeText(this@LoginActivity, "Error login", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun checkUserExist(number: String) {

        FirebaseDatabase.getInstance().getReference("users").child(number)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {

                    dialog.dismiss()
                    if (p0.exists()){
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    }
                    else{
                        startActivity(Intent(this@LoginActivity,RegisterActivity::class.java))
                    }

                }

                override fun onCancelled(p0: DatabaseError) {

                    dialog.dismiss()
                    Toast.makeText(this@LoginActivity, p0.message, Toast.LENGTH_SHORT).show()
                }

            })
    }

}
