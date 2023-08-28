package com.example.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.myapplication.databinding.ActivityRegistrationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegistrationActivity : AppCompatActivity() {

    private val TAG = "RegistrationActivity"
    private lateinit var binding: ActivityRegistrationBinding
    private lateinit var firebaseDatabase: DatabaseReference
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseDatabase = FirebaseDatabase.getInstance().getReference("Users")
    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.singUp -> {
                Log.d(TAG, "Button sign_up clicked.")
                val email = binding.email.text.toString()
                val password = binding.password.text.toString()
                val conf_password = binding.confirmPassword.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty() && conf_password.isNotEmpty()) {
                    if (password == conf_password) {
                        firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener {
                                if (it.isSuccessful) {
                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    val user = User(firebaseAuth.uid.toString(), email, "0")
                                    firebaseDatabase.child(firebaseAuth.uid.toString())
                                        .setValue(user)
                                        .addOnFailureListener {
                                            Toast.makeText(this, "Failed.", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                } else {
                                    Toast.makeText(
                                        this,
                                        it.exception.toString(),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Password is not matching.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Empty fields are not allowed.", Toast.LENGTH_SHORT).show()
                }
            }

            R.id.back -> {
                Log.d(TAG, "Button back clicked.")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }
    }
}