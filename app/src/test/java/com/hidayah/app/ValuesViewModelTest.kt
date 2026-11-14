package com.hidayah.app

import com.hidayah.app.data.*
import com.hidayah.app.ui.ValuesViewModel
import com.hidayah.app.ui.VerseLoadState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ValuesViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private class FakeRepository : QuranRepository {
        var calls = 0
        var fail = true
        override suspend fun getVerseText(reference: VerseReference): VerseText {
            calls++
            if (fail) error("offline")
            return VerseText(reference, "العربية", "বাংলা অনুবাদ")
        }
        override suspend fun getSurahs(): List<Surah> = emptyList()
        override suspend fun getChapter(number: Int): QuranChapter = error("unused")
        override suspend fun getTafsir(surah: Int, verse: Int): Tafsir = error("unused")
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun errorsArePerVerseAndRetryWorksWithoutDuplicateRequests() = runTest(dispatcher) {
        val repo = FakeRepository()
        val vm = ValuesViewModel(repo)
        val ref = VerseReference(33, 70, "সঠিক কথা")
        vm.load(ref)
        vm.load(ref)
        assertTrue(vm.verses.value[ref.key] is VerseLoadState.Loading)
        advanceUntilIdle()
        assertEquals(1, repo.calls)
        assertTrue(vm.verses.value[ref.key] is VerseLoadState.Error)
        vm.load(ref) // No implicit retry until requested.
        advanceUntilIdle()
        assertEquals(1, repo.calls)
        repo.fail = false
        vm.load(ref, retry = true)
        advanceUntilIdle()
        assertEquals(2, repo.calls)
        assertEquals("বাংলা অনুবাদ", (vm.verses.value[ref.key] as VerseLoadState.Loaded).verse.bangla)
        vm.load(ref, retry = true) // Already loaded: no extra call.
        advanceUntilIdle()
        assertEquals(2, repo.calls)
    }
}
