package com.example.dcconcerts

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.TextView
import android.widget.Toast
import org.jetbrains.anko.doAsync

class ResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)


        doAsync {
            try {
                val resultManager = ResultManager()
                val results = resultManager.retrieveEvent(getString(R.string.tm_key))
                if (results.isEmpty())
                {
                    runOnUiThread {
                        Toast.makeText(this@ResultsActivity, "There are no results.", Toast.LENGTH_LONG).show()
                    }
                }
                val adapter = ResultAdapter(results)

                runOnUiThread {
                    recyclerView.adapter = adapter
                }

            } catch(exception: Exception) {
                exception.printStackTrace()
            }
        }
    }
}

