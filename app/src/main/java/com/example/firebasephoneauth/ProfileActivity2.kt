package com.example.firebasephoneauth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.firebasephoneauth.databinding.ActivityMainBinding
import com.example.firebasephoneauth.databinding.ActivityProfile2Binding
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity2 : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var logOut: Button
    lateinit var phoneTv:TextView

    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // binding = ActivityProfile2Binding?.inflate(layoutInflater)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()
    logOut = findViewById(R.id.logoutBtn)
        phoneTv = findViewById(R.id.phoneTv)
        logOut.setOnClickListener{
            firebaseAuth.signOut()
            checkUser()
        }

    }

    private fun checkUser() {
        TODO("Not yet implemented")
    }

    private fun checkUser(function: () -> Unit) {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            val phone = firebaseUser.phoneNumber
            phoneTv.text = phone

        }
    }

}