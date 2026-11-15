package com.hidayah.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface DuaFavoritesStorage {
    fun load(): Set<String>
    fun save(ids: Set<String>)
}

private class LocalDuaFavoritesStorage(context: Context) : DuaFavoritesStorage {
    private val appContext = context.applicationContext
    private val prefs get() = appContext.getSharedPreferences("dua_favorites", Context.MODE_PRIVATE)
    override fun load(): Set<String> = prefs.getStringSet("ids", emptySet()).orEmpty().toSet()
    override fun save(ids: Set<String>) { prefs.edit().putStringSet("ids", ids.toSet()).apply() }
}

data class DuaFavoritesState(val ids: Set<String> = emptySet(), val readFailed: Boolean = false, val error: String? = null)

/** Local-only; do not overwrite existing data when it could not be read. */
class DuaFavorites(private val storage: DuaFavoritesStorage) {
    constructor(context: Context) : this(LocalDuaFavoritesStorage(context))
    private val mutable = MutableStateFlow(readState())
    val state: StateFlow<DuaFavoritesState> = mutable.asStateFlow()

    private fun readState(): DuaFavoritesState = try { DuaFavoritesState(ids = storage.load().toSet()) }
    catch (_: Exception) { DuaFavoritesState(readFailed = true, error = "পছন্দের তালিকা পড়া যায়নি। আবার চেষ্টা করুন।") }

    fun retryRead() { mutable.value = readState() }
    fun setFavorite(id: String, selected: Boolean): Boolean {
        require(DuaItems.any { it.id == id })
        val old = mutable.value
        if (old.readFailed) return false
        val updated = if (selected) old.ids + id else old.ids - id
        return try {
            storage.save(updated.toSet())
            mutable.value = DuaFavoritesState(ids = updated)
            true
        } catch (_: Exception) {
            mutable.value = old.copy(error = "পছন্দের তালিকায় পরিবর্তন সংরক্ষণ করা যায়নি।")
            false
        }
    }
}
