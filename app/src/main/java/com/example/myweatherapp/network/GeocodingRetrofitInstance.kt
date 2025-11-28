package com.example.myweatherapp.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * --- UPDATED ---
 * This version adds a "User-Agent" header to every call.
 * This is required by the Nominatim (OpenStreetMap) API to prevent blocking.
 */
object GeocodingRetrofitInstance {

    // The base URL for the Nominatim API
    private const val BASE_URL = "https://nominatim.openstreetmap.org/"

    /**
     * --- NEW ---
     * This is the HTTP client that will send our requests.
     */
    private val client: OkHttpClient by lazy {
        // This interceptor is for logging. It's helpful for debugging.
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        OkHttpClient.Builder()
            .addInterceptor { chain ->
                // This is the interceptor that adds the User-Agent
                val originalRequest = chain.request()
                val requestWithUserAgent = originalRequest.newBuilder()
                    // We identify our app. Change "MyWeatherApp" to your app's name.
                    .header("User-Agent", "MyWeatherApp/1.0")
                    .build()
                chain.proceed(requestWithUserAgent)
            }
            .addInterceptor(logging) // Add the logger (optional but good)
            .build()
    }

    /**
     * --- UPDATED ---
     * This Retrofit instance now uses our new 'client'.
     */
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- THIS IS THE IMPORTANT CHANGE
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: GeocodingApiService by lazy {
        retrofit.create(GeocodingApiService::class.java)
    }
}