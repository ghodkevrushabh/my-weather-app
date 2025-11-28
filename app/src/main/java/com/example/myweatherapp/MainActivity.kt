package com.example.myweatherapp

import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myweatherapp.adapter.DailyForecastAdapter
import com.example.myweatherapp.adapter.HourlyForecastAdapter
import com.example.myweatherapp.databinding.ActivityMainBinding
import com.example.myweatherapp.location.LocationHelper
import com.example.myweatherapp.models.CurrentAirQuality // <-- UPDATED IMPORT
import com.example.myweatherapp.models.WeatherResponse
import com.example.myweatherapp.theme.ThemeHelper
import com.example.myweatherapp.utils.formatTemp
import com.example.myweatherapp.utils.getWeatherDescriptionFromCode
import com.example.myweatherapp.utils.getWeatherIconFromCode
import com.example.myweatherapp.viewmodel.WeatherViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var hourlyAdapter: HourlyForecastAdapter
    private lateinit var dailyAdapter: DailyForecastAdapter
    private lateinit var locationHelper: LocationHelper
    private lateinit var themeHelper: ThemeHelper

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        themeHelper = ThemeHelper(this)
        themeHelper.applyTheme(themeHelper.getCurrentTheme())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        locationHelper = LocationHelper(this)

        setupRecyclerViews()
        setupSearchListener()
        setupThemeButton()
        observeViewModel()

        checkLocationPermission()
    }

    private fun setupThemeButton() {
        updateThemeButtonIcon()
        binding.themeToggleButton.setOnClickListener {
            themeHelper.toggleTheme()
            themeHelper.applyTheme(themeHelper.getCurrentTheme())
            updateThemeButtonIcon()
        }
    }

    private fun updateThemeButtonIcon() {
        if (themeHelper.getCurrentTheme() == ThemeHelper.THEME_DARK) {
            binding.themeToggleButton.setImageResource(R.drawable.ic_dark_mode)
        } else {
            binding.themeToggleButton.setImageResource(R.drawable.ic_light_mode)
        }
    }

    private fun checkLocationPermission() {
        if (locationHelper.hasLocationPermission()) {
            weatherViewModel.fetchWeatherForCurrentLocation(locationHelper)
        } else {
            locationHelper.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                weatherViewModel.fetchWeatherForCurrentLocation(locationHelper)
            } else {
                Toast.makeText(this, "Location permission denied. Showing default city.", Toast.LENGTH_LONG).show()
                weatherViewModel.searchForCity("London")
            }
        }
    }

    private fun setupSearchListener() {
        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }
    }

    private fun performSearch() {
        val cityName = binding.searchEditText.text.toString().trim()
        if (cityName.isNotEmpty()) {
            hideKeyboard()
            weatherViewModel.searchForCity(cityName)
        } else {
            Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.searchEditText.windowToken, 0)
    }

    private fun setupRecyclerViews() {
        hourlyAdapter = HourlyForecastAdapter()
        binding.rvHourlyForecast.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        dailyAdapter = DailyForecastAdapter()
        binding.rvDailyForecast.apply {
            adapter = dailyAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun observeViewModel() {
        weatherViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            val isError = weatherViewModel.errorMessage.value != null
            binding.mainContentScrollview.visibility = if (isLoading || isError) View.GONE else View.VISIBLE
        }

        weatherViewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage != null) {
                binding.tvError.text = errorMessage
                binding.tvError.visibility = View.VISIBLE
                binding.mainContentScrollview.visibility = View.GONE
            } else {
                binding.tvError.visibility = View.GONE
            }
        }

        // Weather Observer
        weatherViewModel.weatherData.observe(this) { weather ->
            binding.tvError.visibility = View.GONE
            updateUI(weather)
        }

        // --- UPDATED Air Quality Observer (Open-Meteo) ---
        weatherViewModel.airQualityData.observe(this) { aqResponse ->
            aqResponse?.let {
                // Open-Meteo returns a single "current" object, not a list
                updateAirQualityUI(it.current)
            }
        }

        weatherViewModel.locationName.observe(this) { name ->
            binding.tvLocation.text = name
        }
    }

    private fun updateUI(weather: WeatherResponse) {
        binding.tvCurrentTemp.text = formatTemp(weather.current.temp)
        binding.tvCurrentDescription.text = getWeatherDescriptionFromCode(weather.current.weatherCode)

        val todayDaily = weather.daily
        val highLow = "H:${formatTemp(todayDaily.maxTemps[0])} L:${formatTemp(todayDaily.minTemps[0])}"
        binding.tvCurrentHighLow.text = highLow

        binding.ivCurrentIcon.setImageResource(
            getWeatherIconFromCode(weather.current.weatherCode, weather.current.isDay)
        )

        hourlyAdapter.submitList(weather.hourly)

        val upcomingDaily = weather.daily.copy(
            time = weather.daily.time.drop(1),
            maxTemps = weather.daily.maxTemps.drop(1),
            minTemps = weather.daily.minTemps.drop(1),
            weatherCodes = weather.daily.weatherCodes.drop(1)
        )
        dailyAdapter.submitList(upcomingDaily)
    }

    // --- UPDATED: Update Air Quality UI for US AQI Scale (0-500) ---
    private fun updateAirQualityUI(current: CurrentAirQuality) {
        // Make sure the layout is visible
        binding.layoutAirQuality.root.visibility = View.VISIBLE

        val aqi = current.us_aqi

        // Set numeric values
        binding.layoutAirQuality.tvAqiIndex.text = aqi.toString()

        // Note: Open-Meteo gives direct values, we format them to 1 decimal place
        binding.layoutAirQuality.tvPm25.text = "%.1f".format(current.pm2_5)
        binding.layoutAirQuality.tvPm10.text = "%.1f".format(current.pm10)
        binding.layoutAirQuality.tvSo2.text = "%.1f".format(current.so2)
        binding.layoutAirQuality.tvNo2.text = "%.1f".format(current.no2)
        binding.layoutAirQuality.tvO3.text = "%.1f".format(current.o3)
        binding.layoutAirQuality.tvCo.text = "%.1f".format(current.co)

        // Determine Status and Color based on US AQI Standard
        // Ranges: 0-50 (Good), 51-100 (Moderate), 101-150 (Unhealthy for Sensitive), etc.
        val (status, colorRes) = when (aqi) {
            in 0..50 -> "Good" to "#4CAF50"      // Green
            in 51..100 -> "Moderate" to "#FFC107" // Yellow
            in 101..150 -> "Unhealthy" to "#FF9800" // Orange
            in 151..200 -> "Unhealthy" to "#F44336" // Red
            in 201..300 -> "Very Unhealthy" to "#9C27B0" // Purple
            else -> "Hazardous" to "#880E4F"     // Dark Maroon
        }

        binding.layoutAirQuality.tvAqiStatus.text = status
        binding.layoutAirQuality.tvAqiStatus.setTextColor(Color.parseColor(colorRes))

        // Update Progress Bar
        // We set max to 300 because that covers most typical pollution scenarios.
        binding.layoutAirQuality.progressBarAqi.max = 300
        binding.layoutAirQuality.progressBarAqi.progress = aqi
        binding.layoutAirQuality.progressBarAqi.progressTintList =
            android.content.res.ColorStateList.valueOf(Color.parseColor(colorRes))
    }
}