package com.example.dcconcerts

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat

class SavedResultsAdapter (val results: List<Result>) : RecyclerView.Adapter<SavedResultsAdapter.ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_result, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentResult = results[position]

        holder.event.setText(currentResult.event)
        holder.location.setText("\uD83D\uDCCD ${currentResult.location}")
        holder.artist.setText("\uD83C\uDFB6 ${currentResult.artist}")

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val parsedDate = sdf.parse(currentResult.date)
        val stringDate = parsedDate!!.toString()
        holder.month.setText(stringDate.substring(4,7).toUpperCase())
        holder.day.setText(stringDate.substring(8,10))

        holder.song1.setText(currentResult.song1)
        holder.song2.setText(currentResult.song2)
        holder.song3.setText(currentResult.song3)

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
    }
}
