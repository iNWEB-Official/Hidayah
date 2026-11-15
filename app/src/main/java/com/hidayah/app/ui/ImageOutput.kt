package com.hidayah.app.ui

import java.io.OutputStream

/** Checks cancellation around writes and closes the provider stream on success or failure. */
internal fun writeImageOutput(open: () -> OutputStream?, ensureActive: () -> Unit = {}, encode: (OutputStream) -> Boolean) {
    ensureActive()
    val output = open() ?: error("ছবি সংরক্ষণের জায়গা খোলা যায়নি।")
    output.use { raw ->
        val checked = object : OutputStream() {
            override fun write(value: Int) { ensureActive(); raw.write(value) }
            override fun write(bytes: ByteArray, offset: Int, length: Int) { ensureActive(); raw.write(bytes, offset, length) }
            override fun flush() { ensureActive(); raw.flush() }
        }
        ensureActive()
        check(encode(checked)) { "ছবিটি সম্পূর্ণ সংরক্ষণ করা যায়নি।" }
        ensureActive()
    }
}
