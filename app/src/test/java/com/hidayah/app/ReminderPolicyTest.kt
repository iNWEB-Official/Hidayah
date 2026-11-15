package com.hidayah.app

import com.hidayah.app.reminders.initialReminderDelayMillis
import com.hidayah.app.reminders.mayPostReminder
import com.hidayah.app.reminders.ReminderChannelState
import com.hidayah.app.reminders.reminderChannelState
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Test

class ReminderPolicyTest {
    @Test fun initialDelayTargetsNextEightPmIncludingDstChange() {
        assertEquals(TimeUnit.HOURS.toMillis(1), initialReminderDelayMillis(ZonedDateTime.parse("2025-01-01T19:00:00+07:00[Asia/Vientiane]")))
        assertEquals(TimeUnit.HOURS.toMillis(24), initialReminderDelayMillis(ZonedDateTime.parse("2025-01-01T20:00:00+07:00[Asia/Vientiane]")))
        assertEquals(TimeUnit.HOURS.toMillis(23), initialReminderDelayMillis(ZonedDateTime.parse("2025-03-08T20:00:00-05:00[America/New_York]")))
    }
    @Test fun nextTargetUsesZoneOfSchedulingAndAcrossFallDst() {
        val instant = java.time.Instant.parse("2025-11-01T23:30:00Z")
        val vientiane = instant.atZone(java.time.ZoneId.of("Asia/Vientiane"))
        val newYork = instant.atZone(java.time.ZoneId.of("America/New_York"))
        assertEquals(TimeUnit.HOURS.toMillis(13) + TimeUnit.MINUTES.toMillis(30), initialReminderDelayMillis(vientiane))
        assertEquals(TimeUnit.MINUTES.toMillis(30), initialReminderDelayMillis(newYork))
        val afterEight = ZonedDateTime.parse("2025-11-01T20:00:00-04:00[America/New_York]")
        assertEquals(TimeUnit.HOURS.toMillis(25), initialReminderDelayMillis(afterEight))
    }
    @Test fun channelStateDistinguishesBlockedMissingAndUnknown() {
        assertEquals(ReminderChannelState.BLOCKED, reminderChannelState(true, 0))
        assertEquals(ReminderChannelState.ENABLED, reminderChannelState(true, 3))
        assertEquals(ReminderChannelState.NOT_CREATED, reminderChannelState(false, null))
        assertEquals(ReminderChannelState.UNKNOWN, reminderChannelState(null, null))
        assertEquals(ReminderChannelState.UNKNOWN, reminderChannelState(true, null))
        assertEquals(ReminderChannelState.UNKNOWN, reminderChannelState(true, -1000))
    }
    @Test fun reminderNeedsUserChoiceSystemNotificationsAndPermission() {
        assertTrue(mayPostReminder(true, true, true))
        assertFalse(mayPostReminder(false, true, true))
        assertFalse(mayPostReminder(true, false, true))
        assertFalse(mayPostReminder(true, true, false))
    }
}
