package com.hidayah.app

import com.hidayah.app.ui.writeImageOutput
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.CancellationException
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
    @Test fun cancellationDuringEncodingClosesWithoutReportingSuccess() {
        val out = TrackingStream()
        var checks = 0
        try {
            writeImageOutput({ out }, { if (++checks >= 3) throw CancellationException("cancelled") }) { stream ->
                stream.write(1); true
            }
            fail("Cancellation must propagate")
        } catch (_: CancellationException) { assertTrue(out.closed) }
    }
    @Test fun providerWriteFailureClosesStream() {
        var closed = false
        val out = object : java.io.OutputStream() {
            override fun write(value: Int) { throw IOException("provider failure") }
            override fun close() { closed = true }
        }
        try { writeImageOutput({ out }) { it.write(1); true }; fail("Write failure must propagate") }
        catch (_: IOException) { assertTrue(closed) }
    }
    @Test fun missingDestinationIsReported() {
        try { writeImageOutput({ null }) { true }; fail("Missing destination must fail") }
        catch (_: IllegalStateException) { }
    }
}
