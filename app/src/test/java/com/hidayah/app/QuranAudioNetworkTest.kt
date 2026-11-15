package com.hidayah.app

import com.hidayah.app.data.createQuranAudioClient
import com.hidayah.app.data.isAllowedRecitationRequest
import java.net.Proxy
import org.junit.Assert.*
import org.junit.Test

class QuranAudioNetworkTest {
    @Test fun clientDoesNotFollowRedirectsOrUseProxy() {
        val client = createQuranAudioClient()
        assertFalse(client.followRedirects)
        assertFalse(client.followSslRedirects)
        assertEquals(Proxy.NO_PROXY, client.proxy)
    }
    @Test fun onlyExpectedReciterRequestsAreAllowed() {
        assertTrue(isAllowedRecitationRequest("https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3"))
        assertTrue(isAllowedRecitationRequest("https://cdn.islamic.network/quran/audio/128/ar.alafasy/6236.mp3"))
        listOf(
            "http://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3",
            "https://other.example/quran/audio/128/ar.alafasy/1.mp3",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/6237.mp3",
            "https://cdn.islamic.network/quran/audio/128/ar.alafasy/1.mp3?redirect=x",
            "https://cdn.islamic.network/quran/audio/128/ar.other/1.mp3",
        ).forEach { assertFalse(it, isAllowedRecitationRequest(it)) }
    }
}
