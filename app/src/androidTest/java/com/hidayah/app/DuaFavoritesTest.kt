package com.hidayah.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hidayah.app.data.DuaFavorites
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DuaFavoritesTest {
    private lateinit var context: Context
    @Before fun setup() { context = ApplicationProvider.getApplicationContext(); clear() }
    @After fun clear() { context.getSharedPreferences("dua_favorites", Context.MODE_PRIVATE).edit().clear().commit() }
    @Test fun favoritesPersistAcrossStoreInstancesAndCanBeRemoved() {
        val first = DuaFavorites(context)
        first.setFavorite("wakeup", true)
        assertTrue(first.ids.value.contains("wakeup"))
        val second = DuaFavorites(context)
        assertTrue(second.ids.value.contains("wakeup"))
        second.setFavorite("wakeup", false)
        assertFalse(DuaFavorites(context).ids.value.contains("wakeup"))
    }
}
