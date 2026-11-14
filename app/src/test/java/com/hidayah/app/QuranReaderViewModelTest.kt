package com.hidayah.app

import com.hidayah.app.data.*
import com.hidayah.app.ui.QuranReaderViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QuranReaderViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private fun chapter(n: Int) = QuranChapter(Surah(n, "Surah $n", "", 1), listOf(QuranVerse(1, "ar", "bn", "https://example.com/a.mp3")))

    private class FakeRepository : QuranRepository {
        val chapters = mutableMapOf<Int, CompletableDeferred<QuranChapter>>()
        val tafsirs = mutableMapOf<Pair<Int, Int>, CompletableDeferred<Tafsir>>()
        var chapterCalls = 0
        var tafsirCalls = 0
        override suspend fun getSurahs(): List<Surah> = emptyList()
        override suspend fun getVerseText(reference: VerseReference): VerseText = error("unused")
        override suspend fun getChapter(number: Int): QuranChapter {
            chapterCalls++
            return chapters.getValue(number).await()
        }
        override suspend fun getTafsir(surah: Int, verse: Int): Tafsir {
            tafsirCalls++
            return tafsirs.getValue(surah to verse).await()
        }
    }
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun teardown() { Dispatchers.resetMain() }

    @Test fun olderChapterCannotReplaceNewerChapter() = runTest(dispatcher) {
        val repo = FakeRepository()
        val old = CompletableDeferred<QuranChapter>()
        val current = CompletableDeferred<QuranChapter>()
        repo.chapters[1] = old; repo.chapters[2] = current
        val vm = QuranReaderViewModel(repo)
        vm.load(1); runCurrent()
        vm.load(2); runCurrent()
        current.complete(chapter(2)); runCurrent()
        old.complete(chapter(1)); advanceUntilIdle()
        assertEquals(2, vm.state.value.chapter?.surah?.number)
        assertNull(vm.state.value.error)
    }

    @Test fun oldFailureDoesNotHideNewChapter() = runTest(dispatcher) {
        val repo = FakeRepository()
        val old = CompletableDeferred<QuranChapter>()
        val current = CompletableDeferred<QuranChapter>()
        repo.chapters[1] = old; repo.chapters[2] = current
        val vm = QuranReaderViewModel(repo)
        vm.load(1); runCurrent(); vm.load(2); runCurrent()
        current.complete(chapter(2)); runCurrent()
        old.completeExceptionally(IllegalStateException("offline")); advanceUntilIdle()
        assertEquals(2, vm.state.value.chapter?.surah?.number)
        assertNull(vm.state.value.error)
    }

    @Test fun oldTafsirCannotAppearInAnotherChapter() = runTest(dispatcher) {
        val repo = FakeRepository()
        repo.chapters[1] = CompletableDeferred(chapter(1)); repo.chapters[2] = CompletableDeferred(chapter(2))
        val oldTafsir = CompletableDeferred<Tafsir>()
        val newTafsir = CompletableDeferred<Tafsir>()
        repo.tafsirs[1 to 1] = oldTafsir; repo.tafsirs[2 to 1] = newTafsir
        val vm = QuranReaderViewModel(repo)
        vm.load(1); runCurrent(); vm.toggleTafsir(1); runCurrent()
        vm.load(2); runCurrent(); vm.toggleTafsir(1); runCurrent()
        newTafsir.complete(Tafsir("new", "source", listOf("2:1"))); runCurrent()
        oldTafsir.complete(Tafsir("old", "source", listOf("1:1"))); advanceUntilIdle()
        assertEquals("new", vm.state.value.tafsirs[1]?.data?.textHtml)
        assertEquals(2, vm.state.value.chapter?.surah?.number)
    }

    @Test fun tafsirRequiresAnExistingVerseAndCachesSuccessfulResult() = runTest(dispatcher) {
        val repo = FakeRepository()
        repo.chapters[1] = CompletableDeferred(chapter(1))
        repo.tafsirs[1 to 1] = CompletableDeferred(Tafsir("text", "source", listOf("1:1")))
        val vm = QuranReaderViewModel(repo)
        vm.load(1); vm.toggleTafsir(1); runCurrent()
        vm.toggleTafsir(2); vm.toggleTafsir(0); runCurrent()
        assertEquals(0, repo.tafsirCalls)
        vm.toggleTafsir(1); advanceUntilIdle()
        assertEquals(1, repo.tafsirCalls)
        vm.toggleTafsir(1); vm.toggleTafsir(1); advanceUntilIdle()
        assertEquals(1, repo.tafsirCalls)
        assertTrue(vm.state.value.tafsirs[1]!!.expanded)
    }

    @Test fun failedChapterCanBeRetried() = runTest(dispatcher) {
        val repo = FakeRepository()
        repo.chapters[1] = CompletableDeferred<QuranChapter>().also { it.completeExceptionally(IllegalStateException("offline")) }
        val vm = QuranReaderViewModel(repo)
        vm.load(1); advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        repo.chapters[1] = CompletableDeferred(chapter(1))
        vm.load(1, retry = true); advanceUntilIdle()
        assertNull(vm.state.value.error)
        assertEquals(1, vm.state.value.chapter?.surah?.number)
    }
}
