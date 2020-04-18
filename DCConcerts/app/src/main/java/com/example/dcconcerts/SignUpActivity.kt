package com.example.dcconcerts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

class SignUpActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var password2: EditText
    private lateinit var signUpButton: Button
    private lateinit var backButton: Button
    private lateinit var progBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        firebaseAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.newEmailBox)
        password = findViewById(R.id.newPasswordBox)
        password2 = findViewById(R.id.newPasswordBox2)
        signUpButton = findViewById(R.id.newSignUpButton)
        backButton = findViewById(R.id.backToLoginButton)
        progBar = findViewById(R.id.signUpProgressBar)

        signUpButton.setOnClickListener{
            val inputtedUsername: String = email.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            val inputtedPassword2: String = password2.text.toString().trim()

            if(inputtedUsername == null || inputtedUsername == "")
            {
                Toast.makeText(this, "Please enter an email.",Toast.LENGTH_LONG).show()
            }
            else if (inputtedPassword == null || inputtedPassword == "")
            {
                Toast.makeText(this, "Please enter a password.",Toast.LENGTH_LONG).show()
            }
            else if (inputtedPassword2 == null || inputtedPassword2 == "")
            {
                Toast.makeText(this, "Please re-enter your password.",Toast.LENGTH_LONG).show()
            }
            else if (inputtedPassword != inputtedPassword2)
            {
                Toast.makeText(this, "Please make passwords match.",Toast.LENGTH_LONG).show()
            }
            else {
                progBar.setVisibility(View.VISIBLE)
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                firebaseAuth.createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            Toast.makeText(
                                this,
                                "Created user: ${user!!.email}",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this, ResultsActivity::class.java)
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
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
                            } else if (exception is FirebaseAuthUserCollisionException) {
                                // A user with this email already exists
                                Toast.makeText(
                                    this,
                                    "Error: An account with this email already exists.",
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
                                    "Error: Failed to sign up user. $exception",
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.setVisibility(View.GONE)
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }
                    }
            }
        }

        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
