package com.example.myweatherapp.theme

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

/**
 * This class manages saving and loading the user's theme preference.
 * We will use SharedPreferences for lightweight storage.
 */
class ThemeHelper(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "ThemePrefs"
        private const val KEY_THEME = "theme_mode"

        // Define our theme constants
        const val THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO
        const val THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES
    }

    /**
     * Toggles the theme.
     * It finds the current theme, flips it, saves it, and applies it.
     * Returns the *new* theme.
     */
    fun toggleTheme(): Int {
        val currentTheme = getCurrentTheme()
        val newTheme = if (currentTheme == THEME_DARK) THEME_LIGHT else THEME_DARK

        saveTheme(newTheme)
        applyTheme(newTheme)
        return newTheme
    }

    /**
     * Gets the currently saved theme.
     * Defaults to Light Mode if no preference is saved.
     */
    fun getCurrentTheme(): Int {
        return sharedPreferences.getInt(KEY_THEME, THEME_LIGHT)
    }

    /**
     * Saves the chosen theme to SharedPreferences.
     */
    private fun saveTheme(theme: Int) {
        sharedPreferences.edit().putInt(KEY_THEME, theme).apply()
    }

    /**
     * Applies the given theme to the entire app.
     */
    fun applyTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(theme)
    }
}