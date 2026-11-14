package com.hidayah.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppSettings(val dark: Boolean = false, val fontScale: Float = 1f, val reminders: Boolean = false)
class SettingsStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("hidayah_settings", Context.MODE_PRIVATE)
    private val mutable = MutableStateFlow(AppSettings(prefs.getBoolean("dark", false), prefs.getFloat("fontScale", 1f).coerceIn(0.9f, 1.25f), prefs.getBoolean("reminders", false)))
    val settings: StateFlow<AppSettings> = mutable.asStateFlow()
    fun setDark(value: Boolean) { prefs.edit().putBoolean("dark", value).apply(); mutable.value = mutable.value.copy(dark = value) }
    fun setFontScale(value: Float) { val size = value.coerceIn(0.9f, 1.25f); prefs.edit().putFloat("fontScale", size).apply(); mutable.value = mutable.value.copy(fontScale = size) }
    fun setReminders(value: Boolean) { prefs.edit().putBoolean("reminders", value).apply(); mutable.value = mutable.value.copy(reminders = value) }
}
