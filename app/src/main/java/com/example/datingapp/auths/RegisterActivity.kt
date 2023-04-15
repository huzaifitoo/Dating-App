package com.example.datingapp.auths

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.datingapp.R
import com.example.datingapp.databinding.ActivityRegisterBinding
import com.example.datingapp.models.UserModel
import com.example.datingapp.utils.config
import com.example.datingapp.utils.config.hideDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class RegisterActivity : AppCompatActivity() {
    lateinit var binding: ActivityRegisterBinding

    private var imageUri: Uri? = null
    private val selectImage = registerForActivityResult(ActivityResultContracts.GetContent()) {
        imageUri = it

        binding.userImage.setImageURI(imageUri)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.userImage.setOnClickListener {
            selectImage.launch("image/*")
        }
        binding.saveData.setOnClickListener {
            validateData()
        }
    }

    private fun validateData() {
        if (binding.userName.text.toString().isEmpty()
            || binding.userEmail.text.toString().isEmpty()
            || binding.userCity.text.toString().isEmpty()
            || imageUri == null
        ) {
            Toast.makeText(this, "please enter all data", Toast.LENGTH_SHORT).show()
        } else if (!binding.termsConditions.isChecked) {
            Toast.makeText(this, "please check checkbox", Toast.LENGTH_SHORT).show()
        } else {
            uploadImage()
        }

    }

    private fun uploadImage() {
        config.showDialog(this)

        val storageRef = FirebaseStorage.getInstance().getReference("profile")
            .child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profile.jpg")

                storageRef.putFile(imageUri!!)
            .addOnSuccessListener {

                storageRef.downloadUrl.addOnSuccessListener {
                    storeData(it)

                }.addOnFailureListener {

                    hideDialog()

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

            } .addOnFailureListener {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
    }

    private fun storeData(imageUrl: Uri?) {

        val data = UserModel(
            name = binding.userName.text.toString(),
         city = binding.userCity.text.toString(),
         email = binding.userEmail.text.toString(),
         image = imageUrl.toString()

        )
        FirebaseDatabase.getInstance().getReference("users")
            .child(FirebaseAuth.getInstance().currentUser!!.phoneNumber!!)
            .setValue(data).addOnCompleteListener {

                hideDialog()
                if (it.isSuccessful){
                    Toast.makeText(this, "registered successfully", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(this, it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }

    }
}