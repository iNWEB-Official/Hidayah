package com.hidayah.app

import com.hidayah.app.data.DuaFavorites
import com.hidayah.app.data.DuaFavoritesStorage
import org.junit.Assert.*
import org.junit.Test

class DuaFavoritesTest {
    private class FakeStore : DuaFavoritesStorage {
        var saved = setOf("wakeup", "future-id")
        var failRead = false
        var failWrite = false
        var writes = 0
        override fun load(): Set<String> { if (failRead) error("read"); return saved }
        override fun save(ids: Set<String>) { if (failWrite) error("write"); writes++; saved = ids.toSet() }
    }
    @Test fun readFailureBlocksWritesUntilRetryAndKeepsUnknownIds() {
        val store = FakeStore().apply { failRead = true }
        val favorites = DuaFavorites(store)
        assertTrue(favorites.state.value.readFailed)
        assertFalse(favorites.setFavorite("iftar", true))
        assertEquals(0, store.writes)
        store.failRead = false
        favorites.retryRead()
        assertFalse(favorites.state.value.readFailed)
        assertTrue(favorites.setFavorite("iftar", true))
        assertEquals(setOf("wakeup", "iftar", "future-id"), store.saved)
    }
    @Test fun failedWriteKeepsCurrentStateAndRetryWorks() {
        val store = FakeStore()
        val favorites = DuaFavorites(store)
        store.failWrite = true
        assertFalse(favorites.setFavorite("wakeup", false))
        assertTrue("wakeup" in favorites.state.value.ids)
        assertNotNull(favorites.state.value.error)
        store.failWrite = false
        assertTrue(favorites.setFavorite("wakeup", false))
        assertFalse("wakeup" in favorites.state.value.ids)
        assertNull(favorites.state.value.error)
        assertTrue("future-id" in store.saved)
    }
}
