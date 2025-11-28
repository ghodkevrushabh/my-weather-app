package com.example.myweatherapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * We are switching to Open-Meteo.
 * This is a new, TRULY FREE API that does not require an API key.
 */
object RetrofitInstance {

    // The NEW base URL for the Open-Meteo API
    private const val BASE_URL = "https://api.open-meteo.com/"

    /**
     * This creates the Retrofit client.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Use Gson to parse JSON
            .build()
    }

    /**
     * This creates our API service using the Retrofit client.
     */
    val api: WeatherApiService by lazy {
        retrofit.create(WeatherApiService::class.java)
    }
}