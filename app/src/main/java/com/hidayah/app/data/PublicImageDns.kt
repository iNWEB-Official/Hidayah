package com.hidayah.app.data

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.UnknownHostException
import okhttp3.Dns

/** Conservative allow policy: reject special-use ranges, not a guarantee against all network threats. */
fun isPublicImageAddress(address: InetAddress): Boolean {
    if (address.isAnyLocalAddress || address.isLoopbackAddress || address.isLinkLocalAddress ||
        address.isSiteLocalAddress || address.isMulticastAddress) return false
    val b = address.address.map { it.toInt() and 0xff }
    if (address is Inet4Address) {
        val a = b[0]; val c = b[1]; val d = b[2]
        return !(a == 0 || a == 10 || a == 127 || a >= 224 ||
            (a == 100 && c in 64..127) || (a == 169 && c == 254) ||
            (a == 172 && c in 16..31) || (a == 192 && c == 168) ||
            (a == 192 && c == 0 && d in listOf(0, 2)) || (a == 192 && c == 88 && d == 99) ||
            (a == 198 && c in 18..19) || (a == 198 && c == 51 && d == 100) ||
            (a == 203 && c == 0 && d == 113))
    }
    if (address is Inet6Address) {
        // Accept only IPv6 global-unicast space, excluding special/transition/documentation ranges.
        if ((b[0] and 0xe0) != 0x20) return false
        if (b[0] == 0x20 && b[1] == 0x01 && (b[2] == 0x0d && b[3] == 0xb8 || b[2] <= 0x01)) return false
        if (b[0] == 0x20 && b[1] == 0x02) return false
        if (b[0] == 0x3f && b[1] == 0xff && (b[2] and 0xf0) == 0) return false
        return true
    }
    return false
}

/** The returned addresses are passed directly to the HTTP client for connection, not re-resolved. */
fun resolvePublicImageHost(host: String, resolver: (String) -> List<InetAddress>): List<InetAddress> {
    val addresses = resolver(host)
    if (addresses.isEmpty() || addresses.any { !isPublicImageAddress(it) })
        throw UnknownHostException("Image host resolved to a non-public address")
    return addresses
}

object PublicImageDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> =
        resolvePublicImageHost(hostname) { Dns.SYSTEM.lookup(it) }
}
