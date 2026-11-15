package com.hidayah.app.reminders

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import com.hidayah.app.MainActivity
import java.time.ZonedDateTime
import java.util.concurrent.TimeUnit

class DailyReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    override fun doWork(): Result {
        val enabled = try { applicationContext.getSharedPreferences("hidayah_settings", Context.MODE_PRIVATE).getBoolean("reminders", false) }
            catch (_: Exception) { false }
        val permission = Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        if (!mayPostReminder(enabled, NotificationManagerCompat.from(applicationContext).areNotificationsEnabled(), permission)) return Result.success()
        val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(NotificationChannel(DAILY_REMINDER_CHANNEL_ID, "দৈনিক স্মরণ", NotificationManager.IMPORTANCE_DEFAULT))
            if (manager.getNotificationChannel(DAILY_REMINDER_CHANNEL_ID)?.importance == NotificationManager.IMPORTANCE_NONE) return Result.success()
        }
        val intent = Intent(applicationContext, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val pending = PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(applicationContext, DAILY_REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("হিদায়া")
            .setContentText("আজকের আমল ও মূল্যবোধের জন্য একটু সময় রাখুন।")
            .setAutoCancel(true).setContentIntent(pending).build()
        try { manager.notify(104, notification) } catch (_: SecurityException) { /* Permission may change after the check. */ }
        return Result.success()
    }
}
object DailyReminder {
    private const val NAME = "hidayah_daily_reminder"
    fun schedule(context: Context) {
        val delay = initialReminderDelayMillis(ZonedDateTime.now())
        val request = PeriodicWorkRequestBuilder<DailyReminderWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(NAME, ExistingPeriodicWorkPolicy.UPDATE, request)
    }
    fun cancel(context: Context) = WorkManager.getInstance(context).cancelUniqueWork(NAME)
}
