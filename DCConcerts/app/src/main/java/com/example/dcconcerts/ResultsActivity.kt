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

    //initialize lateinit variables
    private lateinit var recyclerView: RecyclerView
    private lateinit var progBar : ProgressBar
    private lateinit var saveButton: Button
    private lateinit var logOutButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        //get sharedPreferences
        val preferences = getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)

        //populate lateinit variables
        recyclerView = findViewById(R.id.resultRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        progBar = findViewById(R.id.resultProgressBar)
        saveButton = findViewById(R.id.viewSavedButton)
        logOutButton = findViewById(R.id.logOutButton)

        //initialize a variable to store the concert list we will soon get from ResultManager
        var results : List<Result> = listOf()

        //progress bar is already on, disable screen clicks not while networking
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

        doAsync {

            try {

                //try to retreive a list from ResultManager to put it into the adapter to be displayed
                val resultManager = ResultManager()
                results = resultManager.retrieveEvent(getString(R.string.tm_key),getString(R.string.client_id),getString(R.string.client_secret))
                if (results.isEmpty())
                {
                    runOnUiThread { //toast if there are zero concerts
                        Toast.makeText(this@ResultsActivity, getString(R.string.no_results), Toast.LENGTH_LONG).show()
                    }
                }

                //go through sharedPreferences' list of saved concerts to update data field for each object
                val set = preferences.getStringSet("SAVED_CONCERTS", setOf())
                results.forEach{r ->
                    set?.forEach{s ->
                        if(r.event == s)
                            r.saved=true
                    }
                }

                //now send results to adapter and put them in the recycler view
                val adapter = ResultAdapter(results)
                runOnUiThread {
                    recyclerView.adapter = adapter
                }

            } catch(exception: Exception) {

                //toast if there is an issue retrieving from ResultManager
                exception.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this@ResultsActivity, getString(R.string.unable_results), Toast.LENGTH_LONG).show()
                }
            }

            //networking is complete: get rid of progress bar and enable clicking on the screen
            runOnUiThread {
                progBar.visibility=View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }

        //if saved button is clicked
        saveButton.setOnClickListener{

            val savedConcerts:ArrayList<Result> = arrayListOf() //make an updated list of saved concerts to put in intent variable
            val savedConcertSet:MutableSet<String> = mutableSetOf() //update sharedPreferences set as well, only including the exact concerts starred on this screen
            results.forEach()
            {
                if(it.saved) {
                    savedConcerts.add(it)
                    savedConcertSet.add(it.event)
                }
            }

            //update the shared preferences
            preferences.edit().putStringSet("SAVED_CONCERTS", savedConcertSet).apply()

            //head to the saved results activity
            val intent = Intent(this, SavedResultsActivity::class.java)
            intent.putExtra("SAVED_CONCERTS", savedConcerts)
            startActivity(intent)
        }

        //if logout button is clicked, head back to login screen
        logOutButton.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}

