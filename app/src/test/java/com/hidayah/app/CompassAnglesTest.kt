package com.hidayah.app

import com.hidayah.app.data.normalizeCompassDegrees
import com.hidayah.app.data.relativeCompassDegrees
import org.junit.Assert.*
import org.junit.Test

class CompassAnglesTest {
    @Test fun normalizesHeadingsAndFindsClockwiseRelativeAngle() {
        assertEquals(350.0, normalizeCompassDegrees(-10.0)!!, 0.0001)
        assertEquals(10.0, normalizeCompassDegrees(730.0)!!, 0.0001)
        assertEquals(20.0, relativeCompassDegrees(10.0, 350.0)!!, 0.0001)
        assertEquals(340.0, relativeCompassDegrees(350.0, 10.0)!!, 0.0001)
        assertEquals(0.0, relativeCompassDegrees(360.0, 0.0)!!, 0.0001)
    }
    @Test fun rejectsInvalidReadingsRatherThanShowingAnArrow() {
        assertNull(normalizeCompassDegrees(Double.NaN))
        assertNull(normalizeCompassDegrees(Double.POSITIVE_INFINITY))
        assertNull(relativeCompassDegrees(10.0, Double.NEGATIVE_INFINITY))
        assertNull(relativeCompassDegrees(Double.NaN, 10.0))
    }
}
