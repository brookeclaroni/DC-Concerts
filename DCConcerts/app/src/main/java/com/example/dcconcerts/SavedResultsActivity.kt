package com.example.dcconcerts

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SavedResultsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_results)

        recyclerView = findViewById(R.id.savedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        backButton = findViewById(R.id.backButton)
        logOutButton = findViewById(R.id.logOutButton2)

        var results : List<Result> = listOf()

        try {
            results = intent.getSerializableExtra("SAVED_CONCERTS") as List<Result>
            if (results.isEmpty())
            {
                Toast.makeText(this, getString(R.string.no_saved), Toast.LENGTH_LONG).show()
            }
            val adapter = SavedResultsAdapter(results)

            recyclerView.adapter = adapter

        } catch(exception: Exception) {
            exception.printStackTrace()
            if (results.isEmpty())
            {
                Toast.makeText(this, getString(R.string.unable_saved), Toast.LENGTH_LONG).show()
            }
        }

        backButton.setOnClickListener{
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

        logOutButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}