package com.example.dcconcerts

import android.util.Base64
import android.provider.Settings.Global.getString
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

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

    //code and comments adapted from lecture
    private fun encodeSecrets(clientID: String, clientSecret: String): String {
        // Encoding for a URL -- converts things like spaces into %20
        val encodedKey = URLEncoder.encode(clientID, "UTF-8")
        val encodedSecret = URLEncoder.encode(clientSecret, "UTF-8")

        // Concatenate the two together, with a colon inbetween
        val combinedEncoded = "$encodedKey:$encodedSecret"

        // Base-64 encode the combined string
        // https://en.wikipedia.org/wiki/Base64
        val base64Combined = Base64.encodeToString(
            combinedEncoded.toByteArray(), Base64.NO_WRAP)

        return base64Combined
    }

    fun retrieveOAuthToken(clientID: String, clientSecret: String): String {
        // Twitter requires us to encoded our API Key and API Secret in a special way for the request.
        val encodedSecrets = encodeSecrets(clientID, clientSecret)

        // OAuth is defined to be a POST call, which has a specific body / payload to let the server
        // know we are doing "application-only" OAuth (e.g. we will only access public information)
        val requestBody = "grant_type=client_credentials"
            .toRequestBody(
                contentType = "application/x-www-form-urlencoded".toMediaType()
            )

        // Build the request
        // The encoded secrets become a header on the request
        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .header("Authorization", "Basic $encodedSecrets")
            .post(requestBody)
            .build()

        // "Execute" the request (.execute will block the current thread until the server replies with a response)
        val response = okHttpClient.newCall(request).execute()

        // Create an empty, mutable list to hold up the Tweets we will parse from the JSON
        val responseString: String? = response.body?.string()

        // If the response was successful (e.g. status code was a 200) AND the server sent us back
        // some JSON (which will contain the OAuth token), then we can go ahead and parse the JSON body.
        return if (!responseString.isNullOrEmpty() && response.isSuccessful) {
            val json: JSONObject = JSONObject(responseString)
            json.getString("access_token")
        } else {
            ""
        }
    }

    fun retrieveSongs(
        clientID: String,
        clientSecret: String,
        artist : String
    ): MutableList<String?> {
        val token = retrieveOAuthToken(clientID, clientSecret)
        val request = Request.Builder()
            .url("https://api.spotify.com/v1/search?q=artist:$artist&type=track&limit=3&access_token=$token")
            .method("GET", null)
            .build()

        val response = okHttpClient.newCall(request).execute()

        val responseString: String? = response.body?.string()
        if (!responseString.isNullOrEmpty() && response.isSuccessful) {
            val json = JSONObject(responseString)
            val tracks = json.getJSONObject("tracks")
            val total = tracks.getString("total")
            if (total=="0"||total=="1"||total=="2")
            {
                return mutableListOf()
            }
            val items = tracks.getJSONArray("items")
            val list: MutableList<String?> = mutableListOf()
            for(i in 0..2) {
                val item = items.getJSONObject(i)
                list.add("${i+1}. ${item.getString("name")}")
            }
            return list
        }
        else
            return mutableListOf()
    }

    fun retrieveEvent(
        tmApiKey: String,
        clientID: String,
        clientSecret: String
    ): List<Result> {
        val request = Request.Builder()
            .url("https://app.ticketmaster.com/discovery/v2/events?apikey=$tmApiKey&classificationName=Music&stateCode=DC&sort=date,asc\n")
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

                val songList = retrieveSongs(clientID, clientSecret, attraction.getString("name"))
                var song1: String?
                var song2: String?
                var song3: String?
                if (songList.size ==0)
                {
                    song1 = "No songs found on Spotify"
                    song2 = null
                    song3 = null
                }
                else
                {
                    song1 = songList[0]
                    song2 = songList[1]
                    song3 = songList[2]
                }
                results.add(
                    Result(
                        event = event.getString("name"),
                        artist = attraction.getString("name"),
                        date = start.getString("localDate"),
                        song1 = song1,
                        song2 = song2,
                        song3 = song3,
                        saved = false
                    )
                )
            }

            return results
        }
        else
            return listOf (Result(
                event = "Error in retrieving events",
                artist = "",
                date = "",
                song1 = null,
                song2 = null,
                song3 = null,
                saved = false
            ))
    }
}