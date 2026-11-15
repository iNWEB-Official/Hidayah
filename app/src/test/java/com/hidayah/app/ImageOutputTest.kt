package com.hidayah.app

import com.hidayah.app.ui.writeImageOutput
import java.io.ByteArrayOutputStream
import org.junit.Assert.*
import org.junit.Test

class ImageOutputTest {
    private class TrackingStream : ByteArrayOutputStream() {
        var closed = false
        override fun close() { closed = true; super.close() }
    }
    @Test fun successfulEncodingWritesAndCloses() {
        val out = TrackingStream()
        writeImageOutput({ out }) { stream -> stream.write(byteArrayOf(1, 2, 3)); true }
        assertArrayEquals(byteArrayOf(1, 2, 3), out.toByteArray())
        assertTrue(out.closed)
    }
    @Test fun failedEncodingClosesAndReportsFailure() {
        val out = TrackingStream()
        try { writeImageOutput({ out }) { false }; fail("Failure must not be reported as success") }
        catch (_: IllegalStateException) { assertTrue(out.closed) }
    }
    @Test fun missingDestinationIsReported() {
        try { writeImageOutput({ null }) { true }; fail("Missing destination must fail") }
        catch (_: IllegalStateException) { }
    }
}
