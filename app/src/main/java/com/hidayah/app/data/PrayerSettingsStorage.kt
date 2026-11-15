package com.hidayah.app.data

import android.content.Context

/** Unknown or missing preference values fall back independently to safe defaults. */
fun decodePrayerSettings(method: String?, asr: String?): PrayerSettings = PrayerSettings(
    method = PrayerMethod.entries.firstOrNull { it.name == method } ?: PrayerSettings().method,
    asr = AsrMethod.entries.firstOrNull { it.name == asr } ?: PrayerSettings().asr,
)

interface PrayerSettingsStorage {
    fun load(): PrayerSettings
    fun save(settings: PrayerSettings)
}

class LocalPrayerSettingsStorage(context: Context) : PrayerSettingsStorage {
    private val preferences = context.applicationContext.getSharedPreferences("hidayah_prayer_settings", Context.MODE_PRIVATE)
    override fun load(): PrayerSettings = decodePrayerSettings(preferences.getString("method", null), preferences.getString("asr", null))
    override fun save(settings: PrayerSettings) {
        preferences.edit().putString("method", settings.method.name).putString("asr", settings.asr.name).apply()
    }
}
