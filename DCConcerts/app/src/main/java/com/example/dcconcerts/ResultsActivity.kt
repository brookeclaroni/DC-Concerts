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
import android.view.WindowManager


class ResultsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var progBar : ProgressBar
    private lateinit var saveButton: Button
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val preferences = getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)

        recyclerView = findViewById(R.id.resultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progBar = findViewById(R.id.resultProgressBar)
        saveButton = findViewById(R.id.viewSavedButton)
        logOutButton = findViewById(R.id.logOutButton)

        var results : List<Result> = listOf()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        doAsync {
            try {
                val resultManager = ResultManager()
                results = resultManager.retrieveEvent(getString(R.string.tm_key),getString(R.string.client_id),getString(R.string.client_secret))
                if (results.isEmpty())
                {
                    runOnUiThread {
                        Toast.makeText(this@ResultsActivity, getString(R.string.no_results), Toast.LENGTH_LONG).show()
                    }
                }
                val set = preferences.getStringSet("SAVED_CONCERTS", setOf())
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
                runOnUiThread {
                    Toast.makeText(this@ResultsActivity, getString(R.string.unable_results), Toast.LENGTH_LONG).show()
                }
            }
            runOnUiThread {
                progBar.visibility=View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        saveButton.setOnClickListener{
            val intent = Intent(this, SavedResultsActivity::class.java)
            val savedConcerts:ArrayList<Result> = arrayListOf()
            val savedConcertSet:MutableSet<String> = mutableSetOf()
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
            intent.putExtra("SAVED_CONCERTS", savedConcerts)
            startActivity(intent)
        }

        logOutButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

