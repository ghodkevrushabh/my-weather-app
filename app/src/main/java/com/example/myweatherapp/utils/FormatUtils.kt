package com.example.myweatherapp.utils

import com.example.myweatherapp.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

// --- New Time Formatters for Open-Meteo ---

// Open-Meteo gives time in ISO-8601 format (e.g., "2025-11-12T19:00")
private val isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

// We want to display "h a" (e.g., "7 PM")
private val hourFormatter = DateTimeFormatter.ofPattern("h a", Locale.getDefault())

// Open-Meteo gives daily time as "2025-11-12"
private val dayOnlyFormatter = DateTimeFormatter.ISO_LOCAL_DATE

// We want to display "EEEE" (e.g., "Wednesday")
private val dayFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.getDefault())

/**
 * Converts an ISO-8601 string (e.g., "2025-11-12T19:00")
 * to a formatted hour string (e.g., "7 PM").
 */
fun formatIsoToHour(isoTimeString: String): String {
    return try {
        val localTime = LocalDateTime.parse(isoTimeString, isoFormatter)
        localTime.format(hourFormatter).uppercase()
    } catch (e: Exception) {
        "N/A"
    }
}

/**
 * Converts an ISO-8601 date string (e.g., "2025-11-12")
 * to a formatted day string (e.g., "Wednesday").
 */
fun formatIsoToDay(isoDateString: String): String {
    return try {
        val localDate = LocalDate.parse(isoDateString, dayOnlyFormatter)
        localDate.format(dayFormatter)
    } catch (e: Exception) {
        "N/A"
    }
}

/**
 * Rounds a Double to the nearest Int and adds the degree symbol.
 * e.g., 15.7 becomes "16°"
 */
fun formatTemp(temp: Double): String {
    return "${temp.roundToInt()}°"
}

/**
 * This is the new, most important function.
 * It maps the Open-Meteo weather code to a drawable icon resource.
 *
 * We will add these drawables in the next step.
 */
fun getWeatherIconFromCode(code: Int, isDay: Int = 1): Int {
    return when (code) {
        0 -> if (isDay == 1) R.drawable.ic_sun else R.drawable.ic_moon // Clear sky
        1 -> if (isDay == 1) R.drawable.ic_sun_cloud else R.drawable.ic_moon_cloud // Mainly clear
        2 -> R.drawable.ic_cloud // Partly cloudy
        3 -> R.drawable.ic_cloud_heavy // Overcast
        45, 48 -> R.drawable.ic_fog // Fog
        51, 53, 55 -> R.drawable.ic_drizzle // Drizzle
        56, 57 -> R.drawable.ic_drizzle // Freezing Drizzle
        61, 63, 65 -> R.drawable.ic_rain // Rain
        66, 67 -> R.drawable.ic_rain // Freezing Rain
        71, 73, 75 -> R.drawable.ic_snow // Snow fall
        77 -> R.drawable.ic_snow // Snow grains
        80, 81, 82 -> R.drawable.ic_rain_heavy // Rain showers
        85, 86 -> R.drawable.ic_snow_heavy // Snow showers
        95 -> R.drawable.ic_thunderstorm // Thunderstorm
        96, 99 -> R.drawable.ic_thunderstorm_rain // Thunderstorm with rain
        else -> R.drawable.ic_cloud // Default
    }
}

/**
 * We also need a function to get a text description from the code.
 */
fun getWeatherDescriptionFromCode(code: Int): String {
    return when (code) {
        0 -> "Clear sky"
        1 -> "Mainly clear"
        2 -> "Partly cloudy"
        3 -> "Overcast"
        45, 48 -> "Fog"
        51, 53, 55 -> "Drizzle"
        56, 57 -> "Freezing Drizzle"
        61, 63, 65 -> "Rain"
        66, 67 -> "Freezing Rain"
        71, 73, 75 -> "Snow"
        77 -> "Snow grains"
        80, 81, 82 -> "Rain showers"
        85, 86 -> "Snow showers"
        95 -> "Thunderstorm"
        96, 99 -> "Thunderstorm with rain"
        else -> "Cloudy"
    }
}