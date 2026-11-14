package com.hidayah.app

import com.hidayah.app.data.isPublicImageAddress
import com.hidayah.app.data.resolvePublicImageHost
import java.net.InetAddress
import java.net.UnknownHostException
import org.junit.Assert.*
import org.junit.Test

class PublicImageDnsTest {
    // Numeric literals only: these test cases do not perform DNS requests.
    private fun ip(value: String) = InetAddress.getByName(value)

    @Test fun blocksLocalPrivateReservedAndDocumentationIpv4() {
        val blocked = listOf("0.0.0.1", "10.0.0.1", "100.64.0.1", "127.0.0.1", "169.254.169.254",
            "172.16.1.2", "192.168.1.1", "192.0.0.1", "192.0.2.1", "198.18.0.1", "198.51.100.1",
            "203.0.113.1", "224.0.0.1", "240.0.0.1")
        blocked.forEach { assertFalse(it, isPublicImageAddress(ip(it))) }
    }

    @Test fun allowsCommonPublicIpv4AndIpv6() {
        listOf("8.8.8.8", "1.1.1.1", "2606:4700:4700::1111", "2001:4860:4860::8888")
            .forEach { assertTrue(it, isPublicImageAddress(ip(it))) }
    }

    @Test fun blocksPrivateAndSpecialIpv6IncludingMappedAddresses() {
        listOf("::", "::1", "fc00::1", "fe80::1", "ff02::1", "2001:db8::1", "2002::1", "3fff::1", "::ffff:127.0.0.1")
            .forEach { assertFalse(it, isPublicImageAddress(ip(it))) }
    }

    @Test fun checkedResolverReturnsTheSamePublicAddressesForConnection() {
        val addresses = listOf(ip("1.1.1.1"), ip("8.8.8.8"))
        assertEquals(addresses, resolvePublicImageHost("trusted.example") { addresses })
    }

    @Test fun mixedPublicAndPrivateDnsAnswersAreRejected() {
        try {
            resolvePublicImageHost("unsafe.example") { listOf(ip("1.1.1.1"), ip("192.168.1.1")) }
            fail("mixed DNS results should fail closed")
        } catch (_: UnknownHostException) { }
    }

    @Test fun emptyDnsAnswerIsRejected() {
        try {
            resolvePublicImageHost("empty.example") { emptyList() }
            fail("empty DNS results should fail closed")
        } catch (_: UnknownHostException) { }
    }
}
