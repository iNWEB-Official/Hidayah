package com.hidayah.app.reminders

import java.time.Duration
import java.time.ZonedDateTime

const val DAILY_REMINDER_CHANNEL_ID = "daily_reminder"
enum class ReminderChannelState { BLOCKED, ENABLED, NOT_CREATED, UNKNOWN }
fun reminderChannelState(channelExists: Boolean?, importance: Int?): ReminderChannelState = when {
    channelExists == null -> ReminderChannelState.UNKNOWN
    !channelExists -> ReminderChannelState.NOT_CREATED
    importance == null -> ReminderChannelState.UNKNOWN
    importance == 0 -> ReminderChannelState.BLOCKED
    importance > 0 -> ReminderChannelState.ENABLED
    else -> ReminderChannelState.UNKNOWN
}

/** Initial target only; periodic work is not an exact local-time alarm. */
fun initialReminderDelayMillis(now: ZonedDateTime): Long {
    var next = now.withHour(20).withMinute(0).withSecond(0).withNano(0)
    if (!next.isAfter(now)) next = next.plusDays(1)
    return Duration.between(now, next).toMillis().coerceAtLeast(0)
}

fun mayPostReminder(userEnabled: Boolean, notificationsEnabled: Boolean, permissionGranted: Boolean): Boolean =
    userEnabled && notificationsEnabled && permissionGranted
