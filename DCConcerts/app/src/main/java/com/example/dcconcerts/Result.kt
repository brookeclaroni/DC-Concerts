package com.example.dcconcerts

import java.io.Serializable

data class Result(
        val event: String,
        val location: String,
        val artist: String,
        val date: String,
        val song1: String?,
        val song2: String?,
        val song3: String?,
        var saved: Boolean,
        val link: String
): Serializable