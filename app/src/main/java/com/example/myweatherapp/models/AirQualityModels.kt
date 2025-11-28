package com.example.myweatherapp.models

import com.google.gson.annotations.SerializedName

data class AirQualityResponse(
    @SerializedName("current") val current: CurrentAirQuality
)

data class CurrentAirQuality(
    @SerializedName("us_aqi") val us_aqi: Int,
    @SerializedName("pm2_5") val pm2_5: Double,
    @SerializedName("pm10") val pm10: Double,
    @SerializedName("carbon_monoxide") val co: Double,
    @SerializedName("nitrogen_dioxide") val no2: Double,
    @SerializedName("sulphur_dioxide") val so2: Double,
    @SerializedName("ozone") val o3: Double
)