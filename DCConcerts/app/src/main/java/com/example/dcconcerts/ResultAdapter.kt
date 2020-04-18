package com.example.dcconcerts

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
            val currentResult = results[position]

            holder.event.text = currentResult.event
            holder.location.text="${holder.location.context.getString(R.string.pin)} ${currentResult.location}"
            holder.artist.text="${holder.location.context.getString(R.string.music_note)} ${currentResult.artist}"

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsedDate = sdf.parse(currentResult.date)
            val stringDate = parsedDate!!.toString()
            holder.month.text = stringDate.substring(4,7).toUpperCase(Locale.ROOT)
            holder.day.text = stringDate.substring(8,10)

            holder.song1.text = currentResult.song1
            holder.song2.text = currentResult.song2
            holder.song3.text = currentResult.song3

            if(currentResult.saved)
                holder.starOff.visibility = View.GONE
            else
                holder.starOff.visibility = View.VISIBLE

            //star button functionality
            holder.starOff.setOnClickListener{
                holder.starOff.visibility = View.GONE
                currentResult.saved=true
            }
            holder.starOn.setOnClickListener{
                holder.starOff.visibility = View.VISIBLE
                currentResult.saved=false
            }

            holder.linkButton.setOnClickListener{
                try {
                    val urlIntent: Intent = Uri.parse(currentResult.link).let { webpage ->
                        Intent(Intent.ACTION_VIEW, webpage)
                    }
                    it.context.startActivity(urlIntent)
                }
                catch(e: Exception) {
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
