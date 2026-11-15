package com.hidayah.app.reminders

import java.time.Duration
import java.time.ZonedDateTime

/** Initial target only; periodic work is not an exact local-time alarm. */
fun initialReminderDelayMillis(now: ZonedDateTime): Long {
    var next = now.withHour(20).withMinute(0).withSecond(0).withNano(0)
    if (!next.isAfter(now)) next = next.plusDays(1)
    return Duration.between(now, next).toMillis().coerceAtLeast(0)
}

fun mayPostReminder(userEnabled: Boolean, notificationsEnabled: Boolean, permissionGranted: Boolean): Boolean =
    userEnabled && notificationsEnabled && permissionGranted
