package com.example.dcconcerts

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpButton: Button
    private lateinit var progBar: ProgressBar
    private lateinit var switch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        firebaseAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.emailBox)
        password = findViewById(R.id.passwordBox)
        loginButton = findViewById(R.id.loginButton)
        signUpButton = findViewById(R.id.signUpButton)
        progBar = findViewById(R.id.loginProgressBar)
        switch = findViewById(R.id.rememberMeSwitch)

        val preferences = getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)

        if(preferences.getBoolean("REMEMBER", false)){
            email.setText(preferences.getString("EMAIL", ""))
            password.setText(preferences.getString("PASSWORD", ""))
            switch.isChecked = true
        }

        loginButton.setOnClickListener{
            val inputtedUsername = email.text.toString().trim()
            val inputtedPassword = password.text.toString().trim()

            if(inputtedUsername == null || inputtedUsername == "")
            {
                Toast.makeText(this, "Please enter an email.",Toast.LENGTH_LONG).show()
            }
            else if (inputtedPassword == null || inputtedPassword == "")
            {
                Toast.makeText(this, "Please enter a password.",Toast.LENGTH_LONG).show()
            }
            else
            {
                progBar.setVisibility(View.VISIBLE)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                firebaseAuth.signInWithEmailAndPassword(inputtedUsername, inputtedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            Toast.makeText(
                                this,
                                "Logged in as user: ${user!!.email}",
                                Toast.LENGTH_SHORT
                            ).show()

                            //remember me switch: save credentials if checked, forget them if unchecked
                            if(switch.isChecked()) {
                                preferences.edit().putString("EMAIL", inputtedUsername).apply()
                                preferences.edit().putString("PASSWORD", inputtedPassword).apply()
                                preferences.edit().putBoolean("REMEMBER", true).apply()
                            }
                            else{
                                preferences.edit().putString("EMAIL", "").apply()
                                preferences.edit().putString("PASSWORD", "").apply()
                                preferences.edit().putBoolean("REMEMBER", false).apply()
                            }

                            // Go to the next Activity ...
                            val intent = Intent(this, ResultsActivity::class.java)
                            startActivity(intent)
                            progBar.setVisibility(View.GONE)
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        } else {
                            val exception = task.exception
                            if (exception is FirebaseAuthInvalidCredentialsException) {
                                // The user’s username is formatted incorrectly
                                // or their password doesn’t meet minimum requirements
                                Toast.makeText(
                                    this,
                                    "Error: Invalid credentials.",
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.setVisibility(View.GONE)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            } else if (exception is FirebaseAuthInvalidUserException) {
                                // A user with this email already exists
                                Toast.makeText(
                                    this,
                                    "Error: An account with this email does not exists.",
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.setVisibility(View.GONE)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            } else if (exception is FirebaseNetworkException) {
                                // A user with this email already exists
                                Toast.makeText(
                                    this,
                                    "Error: Unable to connect to network.",
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.setVisibility(View.GONE)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            } else {
                                // Show generic error message
                                Toast.makeText(
                                    this,
                                    "Error: Failed to login user. $exception",
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.setVisibility(View.GONE)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }
                    }
            }
        }

        signUpButton.setOnClickListener{
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }
}
