package com.example.dcconcerts

import android.content.Context
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

        val preferences = getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.savedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

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

        val button: Button = findViewById(R.id.backButton)
        button.setOnClickListener{
            val intent = Intent(this, ResultsActivity::class.java)
            val savedConcertSet:MutableSet<String> = mutableSetOf()
            results.forEach()
            {
                if(it.saved)
                {
                    savedConcertSet.add(it.event)
                }
                else
                {
                    savedConcertSet.remove(it.event)
                }
            }
            preferences.edit().putStringSet("SAVED_CONCERTS", savedConcertSet).apply()
            startActivity(intent)
        }

    }
}