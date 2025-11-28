package com.example.myweatherapp.models

import com.google.gson.annotations.SerializedName

/**
 * This is the new main response object for Open-Meteo.
 */
data class WeatherResponse(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("current") val current: CurrentWeather,
    @SerializedName("hourly") val hourly: HourlyForecast,
    @SerializedName("daily") val daily: DailyForecast
)

/**
 * New current weather model.
 * Note how the fields match the @Query in our API call.
 */
data class CurrentWeather(
    @SerializedName("time") val time: String,
    @SerializedName("temperature_2m") val temp: Double,
    @SerializedName("apparent_temperature") val feelsLike: Double,
    @SerializedName("relative_humidity_2m") val humidity: Int,
    @SerializedName("is_day") val isDay: Int,
    @SerializedName("weather_code") val weatherCode: Int,
    @SerializedName("wind_speed_10m") val windSpeed: Double

)

/**
 * New hourly forecast model.
 * Open-Meteo groups data into lists (e.g., a list of all times, a list of all temps).
 */
data class HourlyForecast(
    @SerializedName("time") val time: List<String>,
    @SerializedName("temperature_2m") val temperatures: List<Double>,
    @SerializedName("weather_code") val weatherCodes: List<Int>,
    @SerializedName("is_day") val isDay: List<Int> // <-- ADD THIS LINE
)

/**
 * New daily forecast model.
 * Also grouped into lists.
 */
data class DailyForecast(
    @SerializedName("time") val time: List<String>,
    @SerializedName("weather_code") val weatherCodes: List<Int>,
    @SerializedName("temperature_2m_max") val maxTemps: List<Double>,
    @SerializedName("temperature_2m_min") val minTemps: List<Double>
)