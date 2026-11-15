package com.hidayah.app

import com.hidayah.app.data.AppSettingsStorage
import com.hidayah.app.data.SettingsStore
import com.hidayah.app.data.safeAppFontScale
import org.junit.Assert.*
import org.junit.Test

class SettingsStoreTest {
    private class FakeStorage : AppSettingsStorage {
        var dark = true
        var scale = 1.2f
        var reminders = false
        var failDarkRead = false
        var failWrite = false
        override fun readDark(): Boolean { if (failDarkRead) error("read"); return dark }
        override fun readFontScale() = scale
        override fun readReminders() = reminders
        override fun writeDark(value: Boolean) { if (failWrite) error("write"); dark = value }
        override fun writeFontScale(value: Float) { if (failWrite) error("write"); scale = value }
        override fun writeReminders(value: Boolean) { if (failWrite) error("write"); reminders = value }
    }
    @Test fun nonFiniteAndOutOfRangeFontValuesAreSafe() {
        assertEquals(1f, safeAppFontScale(Float.NaN), 0f)
        assertEquals(1f, safeAppFontScale(Float.POSITIVE_INFINITY), 0f)
        assertEquals(0.9f, safeAppFontScale(-1f), 0f)
        assertEquals(1.25f, safeAppFontScale(99f), 0f)
        val store = SettingsStore(FakeStorage().apply { scale = Float.NaN })
        assertEquals(1f, store.settings.value.fontScale, 0f)
        assertNotNull(store.settings.value.warning)
    }
    @Test fun readFailureDoesNotDiscardOtherSettings() {
        val store = SettingsStore(FakeStorage().apply { failDarkRead = true; reminders = true })
        assertFalse(store.settings.value.dark)
        assertEquals(1.2f, store.settings.value.fontScale, 0f)
        assertTrue(store.settings.value.reminders)
        assertNotNull(store.settings.value.warning)
    }
    @Test fun failedWriteKeepsExistingStateAndCanBeRetried() {
        val storage = FakeStorage()
        val store = SettingsStore(storage)
        storage.failWrite = true
        store.setDark(false)
        assertTrue(store.settings.value.dark)
        assertNotNull(store.settings.value.warning)
        assertFalse(store.setReminders(true))
        assertFalse(store.settings.value.reminders)
        storage.failWrite = false
        store.setDark(false)
        assertFalse(store.settings.value.dark)
        assertTrue(store.setReminders(true))
        store.setFontScale(Float.NaN)
        assertEquals(1f, storage.scale, 0f)
        assertNull(store.settings.value.warning)
    }
}
