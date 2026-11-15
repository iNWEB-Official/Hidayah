package com.hidayah.app.ui

import java.io.OutputStream

/** Writes a complete image or throws; closes the stream on both success and failure. */
internal fun writeImageOutput(open: () -> OutputStream?, encode: (OutputStream) -> Boolean) {
    val output = open() ?: error("ছবি সংরক্ষণের জায়গা খোলা যায়নি।")
    output.use { check(encode(it)) { "ছবিটি সম্পূর্ণ সংরক্ষণ করা যায়নি।" } }
}
