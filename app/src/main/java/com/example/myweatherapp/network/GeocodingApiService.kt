package com.example.myweatherapp.network

import com.example.myweatherapp.models.GeocodingResponse
import com.example.myweatherapp.models.ReverseGeocodingResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * --- UPDATED for Nominatim API ---
 * This interface defines the API calls for the new service.
 */
interface GeocodingApiService {

    /**
     * This searches for a city by its name.
     * Nominatim uses 'q' for the query and 'format=json'.
     */
    @GET("search")
    suspend fun searchForCity(
        @Query("q") cityName: String,
        @Query("format") format: String = "jsonv2", // Request JSON format
        @Query("limit") limit: Int = 1 // We only want the top result
    ): Response<List<GeocodingResponse>> // Nominatim search returns a LIST

    /**
     * This finds a city name from GPS coordinates.
     * Nominatim uses 'lat', 'lon', and 'format=json'.
     */
    @GET("reverse")
    suspend fun reverseGeocode(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "jsonv2" // Request JSON format
    ): Response<ReverseGeocodingResponse>
}