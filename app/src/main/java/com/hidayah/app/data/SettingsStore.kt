package com.hidayah.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

fun safeAppFontScale(value: Float): Float = if (value.isFinite()) value.coerceIn(0.9f, 1.25f) else 1f

data class AppSettings(val dark: Boolean = false, val fontScale: Float = 1f, val reminders: Boolean = false, val warning: String? = null)

interface AppSettingsStorage {
    fun readDark(): Boolean
    fun readFontScale(): Float
    fun readReminders(): Boolean
    fun writeDark(value: Boolean)
    fun writeFontScale(value: Float)
    fun writeReminders(value: Boolean)
}

private class LocalAppSettingsStorage(context: Context) : AppSettingsStorage {
    private val appContext = context.applicationContext
    private val prefs get() = appContext.getSharedPreferences("hidayah_settings", Context.MODE_PRIVATE)
    override fun readDark() = prefs.getBoolean("dark", false)
    override fun readFontScale() = prefs.getFloat("fontScale", 1f)
    override fun readReminders() = prefs.getBoolean("reminders", false)
    override fun writeDark(value: Boolean) { prefs.edit().putBoolean("dark", value).apply() }
    override fun writeFontScale(value: Float) { prefs.edit().putFloat("fontScale", value).apply() }
    override fun writeReminders(value: Boolean) { prefs.edit().putBoolean("reminders", value).apply() }
}

class SettingsStore(private val storage: AppSettingsStorage) {
    constructor(context: Context) : this(LocalAppSettingsStorage(context))
    private var readProblem = false
    private val readWarning = "কিছু সেটিংস পড়া যায়নি। মানগুলো যাচাই করুন।"
    private fun <T> read(default: T, block: () -> T): T = try { block() } catch (_: Exception) { readProblem = true; default }
    private val initial = run {
        val dark = read(false, storage::readDark)
        val scale = read(1f, storage::readFontScale)
        if (!scale.isFinite()) readProblem = true
        val reminders = read(false, storage::readReminders)
        AppSettings(dark, safeAppFontScale(scale), reminders, if (readProblem) readWarning else null)
    }
    private val mutable = MutableStateFlow(initial)
    val settings: StateFlow<AppSettings> = mutable.asStateFlow()
    private fun save(write: () -> Unit, updated: (AppSettings) -> AppSettings): Boolean = try {
        write()
        mutable.value = updated(mutable.value).copy(warning = if (readProblem) readWarning else null)
        true
    } catch (_: Exception) {
        mutable.value = mutable.value.copy(warning = "সেটিংস সংরক্ষণ করা যায়নি। আবার চেষ্টা করুন।")
        false
    }
    fun setDark(value: Boolean) { save({ storage.writeDark(value) }) { it.copy(dark = value) } }
    fun setFontScale(value: Float) {
        val size = safeAppFontScale(value)
        save({ storage.writeFontScale(size) }) { it.copy(fontScale = size) }
    }
    fun setReminders(value: Boolean): Boolean = save({ storage.writeReminders(value) }) { it.copy(reminders = value) }
}
