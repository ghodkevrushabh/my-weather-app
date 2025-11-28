package com.example.myweatherapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweatherapp.R
import com.example.myweatherapp.models.DailyForecast
import com.example.myweatherapp.utils.formatIsoToDay
import com.example.myweatherapp.utils.formatTemp
import com.example.myweatherapp.utils.getWeatherIconFromCode

/**
 * A custom data class to combine the daily forecast lists.
 */
data class DailyForecastItem(
    val time: String,
    val maxTemp: Double,
    val minTemp: Double,
    val weatherCode: Int
)

class DailyForecastAdapter :
    RecyclerView.Adapter<DailyForecastAdapter.DailyViewHolder>() {

    // This will hold our list of combined forecast items
    private val forecastItems = mutableListOf<DailyForecastItem>()

    /**
     * The ViewHolder. This holds the references to the views in item_daily_forecast.xml
     */
    class DailyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val day: TextView = itemView.findViewById(R.id.tv_daily_day)
        val icon: ImageView = itemView.findViewById(R.id.iv_daily_icon)
        val temp: TextView = itemView.findViewById(R.id.tv_daily_temp)
    }

    /**
     * Creates the item_daily_forecast.xml layout.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return DailyViewHolder(view)
    }

    /**
     * Gets the total count of items in the list.
     */
    override fun getItemCount(): Int = forecastItems.size

    /**
     * Binds the daily forecast data to the views.
     */
    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val item = forecastItems[position]

        // Bind data using utility functions
        holder.day.text = formatIsoToDay(item.time)

        // Combine high and low temps
        val highLow = "${formatTemp(item.maxTemp)} / ${formatTemp(item.minTemp)}"
        holder.temp.text = highLow

        // Use our new function to set the icon from our drawables
        holder.icon.setImageResource(getWeatherIconFromCode(item.weatherCode))
    }

    /**
     * This is a new function we'll call from MainActivity.
     * It takes the Open-Meteo 'DailyForecast' object (with its separate lists)
     * and combines them into one 'forecastItems' list.
     */
    fun submitList(dailyData: DailyForecast) {
        forecastItems.clear()

        // Loop through the lists and combine them
        for (i in dailyData.time.indices) {
            val item = DailyForecastItem(
                time = dailyData.time[i],
                maxTemp = dailyData.maxTemps[i],
                minTemp = dailyData.minTemps[i],
                weatherCode = dailyData.weatherCodes[i]
            )
            forecastItems.add(item)
        }

        // Tell the adapter that the data has changed
        notifyDataSetChanged()
    }
}