package com.example.myweatherapp.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherapp.location.LocationHelper
import com.example.myweatherapp.models.AirQualityResponse
import com.example.myweatherapp.models.WeatherResponse
import com.example.myweatherapp.network.GeocodingRetrofitInstance
import com.example.myweatherapp.network.RetrofitInstance
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val _airQualityData = MutableLiveData<AirQualityResponse>()
    val airQualityData: LiveData<AirQualityResponse> get() = _airQualityData

    private val _locationName = MutableLiveData<String>()
    val locationName: LiveData<String> = _locationName

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // NO API KEY NEEDED ANYMORE!

    private fun fetchWeather(latitude: Double, longitude: Double, locationName: String) {
        viewModelScope.launch {
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.api.getWeather(
                    latitude = latitude,
                    longitude = longitude
                )

                if (response.isSuccessful && response.body() != null) {
                    _weatherData.value = response.body()
                    _locationName.value = locationName

                    // Fetch Air Quality immediately after
                    fetchAirQuality(latitude, longitude)

                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchAirQuality(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                // Call the new Open-Meteo endpoint (No API Key)
                val response = RetrofitInstance.api.getAirQuality(
                    lat = lat,
                    lon = lon
                )

                if (response.isSuccessful && response.body() != null) {
                    _airQualityData.postValue(response.body())
                } else {
                    Log.e("WeatherViewModel", "AQI Error: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "AQI Exception: ${e.message}")
            }
        }
    }

    fun searchForCity(cityName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val geocodingResponse = GeocodingRetrofitInstance.api.searchForCity(cityName)
                if (geocodingResponse.isSuccessful && geocodingResponse.body() != null) {
                    val results = geocodingResponse.body()!!
                    if (results.isNotEmpty()) {
                        val topResult = results[0]
                        fetchWeather(topResult.latitude.toDouble(), topResult.longitude.toDouble(), topResult.displayName)
                    } else {
                        _errorMessage.value = "City not found: $cityName"
                        _isLoading.value = false
                    }
                } else {
                    _errorMessage.value = "Search Error"
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                _errorMessage.value = "Search Network Error"
                _isLoading.value = false
            }
        }
    }

    fun fetchWeatherForCurrentLocation(locationHelper: LocationHelper) {
        _isLoading.value = true
        _errorMessage.value = null

        locationHelper.getLastLocation(
            onSuccess = { lat, lon ->
                viewModelScope.launch {
                    try {
                        val reverseResponse = GeocodingRetrofitInstance.api.reverseGeocode(lat, lon)
                        if (reverseResponse.isSuccessful && reverseResponse.body() != null) {
                            val geoData = reverseResponse.body()!!
                            val displayName = geoData.displayName ?: "Current Location"
                            fetchWeather(lat, lon, displayName)
                        } else {
                            fetchWeather(lat, lon, "Current Location")
                        }
                    } catch (e: Exception) {
                        fetchWeather(lat, lon, "Current Location")
                    }
                }
            },
            onError = { errorMsg ->
                _errorMessage.value = errorMsg
                _isLoading.value = false
            }
        )
    }

    fun clearError() { _errorMessage.value = null }
}