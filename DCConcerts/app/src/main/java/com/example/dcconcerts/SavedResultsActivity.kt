package com.example.dcconcerts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedResultsActivity : AppCompatActivity() {

    //initialize lateinit variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_results)

        //populate lateinit variables
        recyclerView = findViewById(R.id.savedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton = findViewById(R.id.backButton)
        logOutButton = findViewById(R.id.logOutButton2)

        //initialize a variable to store saved concert list from intent variable
        var results : List<Result> = listOf()

        try {

            //try to retreive the list from the intent variable to put it into the adapter to be displayed
            results = intent.getSerializableExtra("SAVED_CONCERTS") as List<Result>
            if (results.isEmpty()) //toast if there are zero saved concerts
            {
                Toast.makeText(this, getString(R.string.no_saved), Toast.LENGTH_LONG).show()
            }

            //send results to adapter and put them in the recycler view
            val adapter = SavedResultsAdapter(results)
            recyclerView.adapter = adapter

        } catch(exception: Exception) {

            //toast if there is an issue retrieving from intent variable
            exception.printStackTrace()
            if (results.isEmpty())
            {
                Toast.makeText(this, getString(R.string.unable_saved), Toast.LENGTH_LONG).show()
            }
        }

        //is back button is clicked go back to the last activity
        backButton.setOnClickListener{
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

        //if logout button is clicked, head back to login screen
        logOutButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}