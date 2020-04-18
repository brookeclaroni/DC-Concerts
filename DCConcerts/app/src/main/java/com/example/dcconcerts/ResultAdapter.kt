package com.example.dcconcerts

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ResultAdapter (private val results: List<Result>) : RecyclerView.Adapter<ResultAdapter.ViewHolder>(){

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.row_result, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            //get sharedPreferences, get the string set of saved concerts
            val preferences = holder.event.context.getSharedPreferences("dc-concerts", Context.MODE_PRIVATE)
            val savedConcertSet = preferences.getStringSet("SAVED_CONCERTS", mutableSetOf())

            //retrieve the current result to add to the recycler view
            val currentResult = results[position]

            //fill in event, location, and artist
            holder.event.text = currentResult.event
            holder.location.text=holder.location.context.getString(R.string.pin,currentResult.location)
            holder.artist.text=holder.location.context.getString(R.string.music_note,currentResult.artist)

            //fill in the date with the specific format
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsedDate = sdf.parse(currentResult.date)
            val stringDate = parsedDate!!.toString()
            holder.month.text = stringDate.substring(4,7).toUpperCase(Locale.ROOT)
            holder.day.text = stringDate.substring(8,10)

            //fill in the three spotify songs
            if(currentResult.songList[0] == null)
                holder.song1.text = holder.song1.context.getString(R.string.no_songs)  //instead of leaving blank for 0 songs, add message
            else
                holder.song1.text = currentResult.songList[0]
            holder.song2.text = currentResult.songList[1]
            holder.song3.text = currentResult.songList[2]

            //make the star the appropriate color
            if(currentResult.saved)
                holder.starOff.visibility = View.GONE //if this is a saved result, make the yellow star appear
            else  //if this is not a saved result, make the grey star appear
                holder.starOff.visibility = View.VISIBLE

            //if star is grey and then the user clicks to save
            holder.starOff.setOnClickListener{

                //change the color, object data, and update shared preferences
                holder.starOff.visibility = View.GONE
                currentResult.saved=true
                savedConcertSet?.add(currentResult.event)
                preferences.edit().putStringSet("SAVED_CONCERTS", savedConcertSet).apply()
            }

            //if star is yellow and then the user clicks to unsave
            holder.starOn.setOnClickListener{

                //change the color, object data, and update shared preferences
                holder.starOff.visibility = View.VISIBLE
                currentResult.saved=false
                savedConcertSet?.remove(currentResult.event)
                preferences.edit().putStringSet("SAVED_CONCERTS", savedConcertSet).apply()
            }

            //if link button is clicked, try to open the link
            holder.linkButton.setOnClickListener{
                try {
                    val urlIntent: Intent = Uri.parse(currentResult.link).let { webpage ->
                        Intent(Intent.ACTION_VIEW, webpage)
                    }
                    it.context.startActivity(urlIntent)
                }
                catch(e: Exception) {

                    //make a toast if it won't open
                    e.printStackTrace()
                    Toast.makeText(holder.linkButton.context, holder.linkButton.context.getString(R.string.unable_link), Toast.LENGTH_LONG).show()
                }
            }

        }

        override fun getItemCount(): Int {
            return results.size
        }

        class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val event: TextView = itemView.findViewById(R.id.event)
            val location: TextView = itemView.findViewById(R.id.location)
            val artist: TextView = itemView.findViewById(R.id.artist)
            val month: TextView = itemView.findViewById(R.id.month)
            val day: TextView = itemView.findViewById(R.id.day)
            val song1: TextView = itemView.findViewById(R.id.song1)
            val song2: TextView = itemView.findViewById(R.id.song2)
            val song3: TextView = itemView.findViewById(R.id.song3)
            val starOff: ImageButton = itemView.findViewById(R.id.starButtonOff)
            val starOn: ImageButton = itemView.findViewById(R.id.starButtonOn)
            val linkButton: ImageButton = itemView.findViewById(R.id.linkButton)
        }
}
