package com.example.dcconcerts

import android.provider.Settings.Global.getString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class ResultManager {
    private val okHttpClient: OkHttpClient

    init {
        val builder = OkHttpClient.Builder()

        // Set up our OkHttpClient instance to log all network traffic to Logcat
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        builder.addInterceptor(loggingInterceptor)

        builder.connectTimeout(15, TimeUnit.SECONDS)
        builder.readTimeout(15, TimeUnit.SECONDS)
        builder.writeTimeout(15, TimeUnit.SECONDS)

        okHttpClient = builder.build()

    }

    fun retrieveSong(
        artist : String,
        index : Int
    ): String {
//        val token = ""
//        val request = Request.Builder()
//            .url("https://api.spotify.com/v1/search?q=artist:$artist&type=track&limit=3&access_token=$token")
//            .method("GET", null)
//            .build()
//
//        val response = okHttpClient.newCall(request).execute()
//
//        val responseString: String? = response.body?.string()
//        if (!responseString.isNullOrEmpty() && response.isSuccessful) {
//            val json = JSONObject(responseString)
//            val tracks = json.getJSONObject("tracks")
//            val items = tracks.getJSONArray("items")
//            val item = items.getJSONObject(index)
//            return item.getString("name")
//        }
//        else
            return "Song $index"
    }

    fun retrieveEvent(
        apiKey: String
    ): List<Result> {
        val request = Request.Builder()
            .url("https://app.ticketmaster.com/discovery/v2/events?apikey=$apiKey&classificationName=Music&stateCode=DC&sort=date,asc\n")
            .method("GET", null)
            .build()

        val response = okHttpClient.newCall(request).execute()

        val responseString: String? = response.body?.string()
        val results : MutableList<Result> = mutableListOf()
        if (!responseString.isNullOrEmpty() && response.isSuccessful) {
            val json = JSONObject(responseString)
            val embedded = json.getJSONObject("_embedded")
            val events = embedded.getJSONArray("events")
            for(i in 0..19) {
                val event = events.getJSONObject(i)
                val dates = event.getJSONObject("dates")
                val start = dates.getJSONObject("start")
                val embedded2 = event.getJSONObject("_embedded")
                val attractions = embedded2.getJSONArray("attractions")
                val attraction = attractions.getJSONObject(0)
                results.add(
                    Result(
                        event = event.getString("name"),
                        artist = attraction.getString("name"),
                        date = start.getString("localDate"),
                        song1 = "1. " + retrieveSong(attraction.getString("name"), 0),
                        song2 = "2. " + retrieveSong(attraction.getString("name"), 1),
                        song3 = "3. " + retrieveSong(attraction.getString("name"), 2)
                    )
                )
            }

            return results
        }
        else
            return listOf (Result(
                event = "Event",
                artist = "Artist",
                date = "Date",
                song1 = "Song 1",
                song2 = "Song 2",
                song3 = "Song 3"
            ))
    }
}