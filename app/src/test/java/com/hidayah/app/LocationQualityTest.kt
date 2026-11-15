package com.hidayah.app

import com.hidayah.app.data.*
import org.junit.Assert.*
import org.junit.Test

class LocationQualityTest {
    @Test fun rejectsOutOfRangeAndNonFiniteCoordinates() {
        assertNull(sanitizeGeoPoint(GeoPoint(Double.NaN, 0.0)))
        assertNull(sanitizeGeoPoint(GeoPoint(91.0, 0.0)))
        assertNull(sanitizeGeoPoint(GeoPoint(0.0, Double.POSITIVE_INFINITY)))
        assertNull(sanitizeGeoPoint(GeoPoint(0.0, -181.0)))
    }
    @Test fun invalidAccuracyIsUnknownRatherThanPrecise() {
        val value = sanitizeGeoPoint(GeoPoint(23.8, 90.4, Double.NaN, Float.NaN))!!
        assertEquals(0.0, value.altitudeMeters, 0.0)
        assertNull(value.accuracyMeters)
        assertEquals(LocationAccuracy.UNKNOWN, locationAccuracy(value))
        assertEquals(LocationAccuracy.UNKNOWN, locationAccuracy(value.copy(accuracyMeters = -1f)))
        assertEquals(LocationAccuracy.APPROXIMATE, locationAccuracy(value.copy(accuracyMeters = 999f)))
        assertEquals(LocationAccuracy.VERY_APPROXIMATE, locationAccuracy(value.copy(accuracyMeters = 1000f)))
    }
}
