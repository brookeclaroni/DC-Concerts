package com.example.dcconcerts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUpButton: Button = findViewById(R.id.newSignUpButton)
        val backButton: Button = findViewById(R.id.backToLoginButton)

        signUpButton.setOnClickListener{
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
