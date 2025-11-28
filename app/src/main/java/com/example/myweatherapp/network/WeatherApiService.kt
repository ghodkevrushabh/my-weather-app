package com.example.myweatherapp.network

import com.example.myweatherapp.models.AirQualityResponse
import com.example.myweatherapp.models.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface WeatherApiService {

    // --- Existing Weather Call ---
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m",
        @Query("hourly") hourly: String = "temperature_2m,weather_code,is_day",
        @Query("daily") daily: String = "weather_code,temperature_2m_max,temperature_2m_min",
        @Query("timezone") timezone: String = "auto"
    ): Response<WeatherResponse>

    // --- UPDATED Air Quality Call (Open-Meteo) ---
    // FREE, No API Key required
    @GET
    suspend fun getAirQuality(
        @Url url: String = "https://air-quality-api.open-meteo.com/v1/air-quality",
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String = "us_aqi,pm2_5,pm10,carbon_monoxide,nitrogen_dioxide,sulphur_dioxide,ozone"
    ): Response<AirQualityResponse>
}