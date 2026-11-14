package com.hidayah.app.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Stored only on this installation; callers never mutate SharedPreferences' returned set. */
class DuaFavorites(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences("dua_favorites", Context.MODE_PRIVATE)
    private val mutableIds = MutableStateFlow(prefs.getStringSet("ids", emptySet()).orEmpty().toSet())
    val ids: StateFlow<Set<String>> = mutableIds.asStateFlow()
    fun setFavorite(id: String, selected: Boolean) {
        require(DuaItems.any { it.id == id })
        val updated = if (selected) mutableIds.value + id else mutableIds.value - id
        prefs.edit().putStringSet("ids", updated.toSet()).apply()
        mutableIds.value = updated
    }
}
