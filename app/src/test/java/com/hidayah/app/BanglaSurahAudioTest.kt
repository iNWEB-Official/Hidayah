package com.hidayah.app

import com.hidayah.app.data.BANGLA_AUDIO_ITEM
import com.hidayah.app.data.banglaSurahFilename
import com.hidayah.app.data.banglaSurahUrl
import com.hidayah.app.data.createBanglaAudioClient
import com.hidayah.app.data.isAllowedBanglaAudioUrl
import java.net.Proxy
import okhttp3.HttpUrl.Companion.toHttpUrl
import org.junit.Assert.*
import org.junit.Test

class BanglaSurahAudioTest {
    @Test fun oneNumberedFileIsMappedForEverySurah() {
        val names = (1..114).map { n ->
            val name = banglaSurahFilename(n)!!
            assertTrue(name.startsWith("%03d.".format(n)))
            assertTrue(isAllowedBanglaAudioUrl(banglaSurahUrl(n)!!, n))
            name
        }
        assertEquals(114, names.toSet().size)
        assertNull(banglaSurahFilename(0))
        assertNull(banglaSurahFilename(115))
        assertNull(banglaSurahUrl(-1))
    }
    @Test fun onlySelectedFileAndSpecificArchiveHostsAreAllowed() {
        val file = banglaSurahFilename(1)!!
        fun url(host: String, segments: List<String>) = okhttp3.HttpUrl.Builder().scheme("https").host(host).apply { segments.forEach { addPathSegment(it) } }.build()
        val path = listOf("0", "items", BANGLA_AUDIO_ITEM, file)
        assertTrue(isAllowedBanglaAudioUrl(url("dn710700.ca.archive.org", path), 1))
        assertTrue(isAllowedBanglaAudioUrl(url("ia601307.us.archive.org", path), 1))
        assertFalse(isAllowedBanglaAudioUrl(url("evil.archive.org", path), 1))
        assertFalse(isAllowedBanglaAudioUrl(url("dn710700.ca.archive.org.evil.com", path), 1))
        assertFalse(isAllowedBanglaAudioUrl(url("dn710700.ca.archive.org", path), 2))
        assertFalse(isAllowedBanglaAudioUrl(url("dn710700.ca.archive.org", path + "other"), 1))
        assertFalse(isAllowedBanglaAudioUrl(banglaSurahUrl(1)!!.newBuilder().addQueryParameter("x", "1").build(), 1))
        assertFalse(isAllowedBanglaAudioUrl("http://archive.org/download/$BANGLA_AUDIO_ITEM/file.mp3".toHttpUrl(), 1))
    }
    @Test fun automaticRedirectAndProxyAreDisabledForManualValidation() {
        val client = createBanglaAudioClient(1)
        assertFalse(client.followRedirects)
        assertFalse(client.followSslRedirects)
        assertEquals(Proxy.NO_PROXY, client.proxy)
    }
}
