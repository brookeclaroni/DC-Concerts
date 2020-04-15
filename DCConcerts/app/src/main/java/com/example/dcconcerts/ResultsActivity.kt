package com.example.dcconcerts

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.Toast
import org.jetbrains.anko.doAsync
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class ResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val preferences = getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.resultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        var results : List<Result> = mutableListOf()

        doAsync {
            try {
                val resultManager = ResultManager()
                results = resultManager.retrieveEvent(getString(R.string.tm_key),getString(R.string.client_id),getString(R.string.client_secret))
                if (results.isEmpty())
                {
                    runOnUiThread {
                        Toast.makeText(this@ResultsActivity, "There are no results.", Toast.LENGTH_LONG).show()
                    }
                }
                val set = preferences.getStringSet("SAVED_CONCERTS", null)
                //val set = setOf("Tops")
                results.forEach{r ->
                    set?.forEach{s ->
                        if(r.event == s)
                            r.saved=true
                    }
                }
                val adapter = ResultAdapter(results)

                runOnUiThread {
                    recyclerView.adapter = adapter
                }

            } catch(exception: Exception) {
                exception.printStackTrace()
            }
            runOnUiThread {
                val progBar : ProgressBar = findViewById(R.id.resultProgressBar)
                progBar.setVisibility(View.GONE)
            }
        }

        val button: Button = findViewById(R.id.viewSavedButton)
        button.setOnClickListener{
            val intent = Intent(this, SavedResultsActivity::class.java)
            var savedConcerts:ArrayList<Result> = arrayListOf()
            var savedConcertSet:MutableSet<String> = mutableSetOf()
            savedConcertSet.add("Tops")
            results.forEach()
            {
                if(it.saved)
                {
                    savedConcerts.add(it)
                    savedConcertSet.add(it.event)
                }
                else
                {
                    savedConcertSet.remove(it.event)
                }
            }
            preferences.edit().putStringSet("SAVED_CONCERTS", savedConcertSet).apply()
            intent.putExtra("SAVED_CONCERTS", savedConcerts)
            startActivity(intent)
        }
    }
}

