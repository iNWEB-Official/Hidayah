package com.hidayah.app

import com.hidayah.app.ui.RecitationQueue
import com.hidayah.app.ui.recitationQueue
import org.junit.Assert.*
import org.junit.Test

class RecitationQueueTest {
    @Test fun singleAyahDoesNotIncludeFollowingAyahs() {
        assertEquals(RecitationQueue(2, 3), recitationQueue(7, 2, single = true))
    }

    @Test fun selectedAyahToEndPreservesContinuousPlayback() {
        assertEquals(RecitationQueue(2, 7), recitationQueue(7, 2, single = false))
    }

    @Test fun wholeSurahStartsAtFirstAndEndsAfterLastAyah() {
        assertEquals(RecitationQueue(0, 7), recitationQueue(7, 0, single = false))
    }

    @Test fun lastAyahHasOneItemInEitherMode() {
        assertEquals(RecitationQueue(6, 7), recitationQueue(7, 6, single = true))
        assertEquals(RecitationQueue(6, 7), recitationQueue(7, 6, single = false))
    }

    @Test fun invalidIndicesAndEmptyChaptersAreRejected() {
        assertNull(recitationQueue(0, 0, single = true))
        assertNull(recitationQueue(7, -1, single = false))
        assertNull(recitationQueue(7, 7, single = true))
    }
}
