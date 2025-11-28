package com.example.myweatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.R
import com.example.myweatherapp.models.HourlyForecast
import com.example.myweatherapp.utils.formatIsoToHour
import com.example.myweatherapp.utils.formatTemp
import com.example.myweatherapp.utils.getWeatherIconFromCode

/**
 * This is our custom data class.
 * We've added 'isDay' to it.
 */
data class HourlyForecastItem(
    val time: String,
    val temp: Double,
    val weatherCode: Int,
    val isDay: Int // <-- CHANGE 1: ADDED THIS
)

class HourlyForecastAdapter :
    RecyclerView.Adapter<HourlyForecastAdapter.HourlyViewHolder>() {

    private val forecastItems = mutableListOf<HourlyForecastItem>()

    class HourlyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val time: TextView = itemView.findViewById(R.id.tv_hourly_time)
        val icon: ImageView = itemView.findViewById(R.id.iv_hourly_icon)
        val temp: TextView = itemView.findViewById(R.id.tv_hourly_temp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyViewHolder(view)
    }

    override fun getItemCount(): Int = forecastItems.size

    /**
     * This is where we bind the data to the views.
     */
    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val item = forecastItems[position]

        holder.time.text = formatIsoToHour(item.time)
        holder.temp.text = formatTemp(item.temp)

        // --- CHANGE 3: THE FINAL FIX ---
        // We now pass 'item.isDay' to the icon function.
        // This will show a moon icon when it's night.
        holder.icon.setImageResource(
            getWeatherIconFromCode(item.weatherCode, item.isDay)
        )
    }

    /**
     * This function now combines all three lists.
     */
    fun submitList(hourlyData: HourlyForecast) {
        forecastItems.clear()

        // We only want the next 24 hours
        val maxItems = minOf(hourlyData.time.size, 24)

        for (i in 0 until maxItems) {
            val item = HourlyForecastItem(
                time = hourlyData.time[i],
                temp = hourlyData.temperatures[i],
                weatherCode = hourlyData.weatherCodes[i],
                isDay = hourlyData.isDay[i] // <-- CHANGE 2: ADDED THIS
            )
            forecastItems.add(item)
        }

        notifyDataSetChanged()
    }
}