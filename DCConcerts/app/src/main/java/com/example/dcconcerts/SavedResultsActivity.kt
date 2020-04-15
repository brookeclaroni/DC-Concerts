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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_saved_results)

        recyclerView = findViewById(R.id.savedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        try {
            val results = intent.getSerializableExtra("SAVED_CONCERTS") as List<Result>
            //val results = listOf(Result("event", "artist","date","song1","song2","song3", true))
            if (results.isEmpty())
            {
                Toast.makeText(this, "There are no results.", Toast.LENGTH_LONG).show()
            }
            val adapter = SavedResultsAdapter(results)

            recyclerView.adapter = adapter

        } catch(exception: Exception) {
            exception.printStackTrace()
        }

        val button: Button = findViewById(R.id.backButton)
        button.setOnClickListener{
            val intent = Intent(this, ResultsActivity::class.java)
            startActivity(intent)
        }

    }
}