package com.hidayah.app

import com.hidayah.app.data.DeedDao
import com.hidayah.app.data.DeedRecord
import com.hidayah.app.data.RoomTrackerRepository
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class RoomTrackerRepositoryTest {
    private class FakeDao : DeedDao {
        var range: Pair<String, String>? = null
        val writes = mutableListOf<DeedRecord>()
        override fun observeBetween(first: String, last: String): Flow<List<DeedRecord>> {
            range = first to last
            return flowOf(emptyList())
        }
        override suspend fun upsert(record: DeedRecord) { writes.add(record) }
    }

    @Test fun usesIsoDatesForReadsAndWrites() = runTest {
        val dao = FakeDao()
        val repo = RoomTrackerRepository(dao)
        val day = LocalDate.of(2027, 1, 1)
        repo.observeBetween(day.minusDays(6), day)
        assertEquals("2026-12-26" to "2027-01-01", dao.range)
        repo.setCompleted(day, "fajr", true)
        repo.setCompleted(day, "fajr", false)
        assertEquals(listOf(DeedRecord("2027-01-01", "fajr", true), DeedRecord("2027-01-01", "fajr", false)), dao.writes)
    }

    @Test fun unknownDeedIsNotWritten() = runTest {
        val dao = FakeDao()
        try {
            RoomTrackerRepository(dao).setCompleted(LocalDate.of(2027, 1, 1), "unknown", true)
            fail("Unknown deed must be rejected")
        } catch (_: IllegalArgumentException) {
            assertTrue(dao.writes.isEmpty())
        }
    }
}
