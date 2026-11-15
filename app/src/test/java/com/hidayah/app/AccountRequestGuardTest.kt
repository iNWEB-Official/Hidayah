package com.hidayah.app

import com.hidayah.app.data.AccountRequestGuard
import org.junit.Assert.*
import org.junit.Test

class AccountRequestGuardTest {
    @Test fun laterEventOrSignOutInvalidatesOlderClaimResults() {
        val guard = AccountRequestGuard()
        var old = 0L
        var current = 0L
        val published = mutableListOf<String>()
        guard.onEvent { old = it }
        guard.onEvent { current = it }
        assertFalse(guard.publishIfCurrent(old, "same-user", { "same-user" }) { published.add("stale") })
        assertFalse(guard.publishIfCurrent(current, "same-user", { "other-user" }) { published.add("wrong") })
        assertTrue(guard.publishIfCurrent(current, "same-user", { "same-user" }) { published.add("current") })
        guard.invalidate { }
        assertFalse(guard.publishIfCurrent(current, "same-user", { "same-user" }) { published.add("after-close") })
        assertEquals(listOf("current"), published)
    }
}
