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

    //initialize lateinit variables
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

        //populate the lateinit variables
        firebaseAuth = FirebaseAuth.getInstance()

        email = findViewById(R.id.newEmailBox)
        password = findViewById(R.id.newPasswordBox)
        password2 = findViewById(R.id.newPasswordBox2)
        signUpButton = findViewById(R.id.newSignUpButton)
        backButton = findViewById(R.id.backToLoginButton)
        progBar = findViewById(R.id.signUpProgressBar)

        //when sign up button is pressed:
        signUpButton.setOnClickListener{

            //retrieve the text from the editTexts
            val inputtedUsername: String = email.text.toString().trim()
            val inputtedPassword: String = password.text.toString().trim()
            val inputtedPassword2: String = password2.text.toString().trim()

            //make sure email editText is not empty
            if(inputtedUsername == "")
            {
                Toast.makeText(this, getString(R.string.enter_email),Toast.LENGTH_LONG).show()
            }

            //make sure password editText is not empty
            else if (inputtedPassword == "")
            {
                Toast.makeText(this, getString(R.string.enter_pword),Toast.LENGTH_LONG).show()
            }

            //make sure second password editText is not empty
            else if (inputtedPassword2 == "")
            {
                Toast.makeText(this, getString(R.string.reenter_pword),Toast.LENGTH_LONG).show()
            }

            //make sure the two passwords match
            else if (inputtedPassword != inputtedPassword2)
            {
                Toast.makeText(this, getString(R.string.match_pword),Toast.LENGTH_LONG).show()
            }

            //if all editTexts are filled in and passwords match:
            else {
                //start the progress bar and disable clicks to the screen since networking is about to occur
                progBar.visibility=View.VISIBLE
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

                //use firebase to attempt to create the user
                firebaseAuth.createUserWithEmailAndPassword(inputtedUsername, inputtedPassword)
                    .addOnCompleteListener { task ->

                        //if the user is created successfully:
                        if (task.isSuccessful) {

                            //make a toast with the new user name
                            val user = firebaseAuth.currentUser
                            Toast.makeText(
                                this,
                                getString(R.string.created_user, user!!.email),
                                Toast.LENGTH_SHORT
                            ).show()

                            // Go to the next Activity and get rid of progress bar and screen click disable
                            val intent = Intent(this, ResultsActivity::class.java)
                            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            startActivity(intent)
                            progBar.visibility=View.GONE
                        }

                        //if the user is not created successfully:
                        else {
                            val exception = task.exception

                            // The user’s username is formatted incorrectly or their password doesn’t meet minimum requirements
                            // then toast and get rid of progress bar and screen click disable
                            if (exception is FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.invalid_cred),
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.visibility=View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }

                            // A user with this email already exists. Toast and get rid of progress bar and screen click disable
                            else if (exception is FirebaseAuthUserCollisionException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.already_exists),
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.visibility=View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }

                            // A networking error occurred.  Toast and get rid of progress bar and screen click disable
                            else if (exception is FirebaseNetworkException) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.network_error),
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.visibility=View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }

                            // Some other error occurred.  Toast with exception and get rid of progress bar and screen click disable
                            else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.signup_error, exception),
                                    Toast.LENGTH_LONG
                                ).show()
                                progBar.visibility=View.GONE
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                            }
                        }
                    }
            }
        }

        //when signup button is pressed, head back to login activity
        backButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
