package com.example.myweatherapp.models

import com.google.gson.annotations.SerializedName

/**
 * --- NEW MODEL for Nominatim Search ---
 * The search API returns a LIST of these objects.
 */
data class GeocodingResponse(
    @SerializedName("lat") val latitude: String,
    @SerializedName("lon") val longitude: String,
    @SerializedName("display_name") val displayName: String
)

/**
 * --- NEW MODEL for Nominatim Reverse Geocode ---
 * The reverse API returns a single object containing an 'address' object.
 */
data class ReverseGeocodingResponse(
    @SerializedName("address") val address: Address,
    @SerializedName("display_name") val displayName: String? // <-- ADD THIS LINE
)

/**
 * This is the 'address' object inside the ReverseGeocodingResponse.
 * It contains the actual location name.
 */
data class Address(
    @SerializedName("city") val city: String?,
    @SerializedName("town") val town: String?,
    @SerializedName("village") val village: String?,
    @SerializedName("country_code") val countryCode: String?
)