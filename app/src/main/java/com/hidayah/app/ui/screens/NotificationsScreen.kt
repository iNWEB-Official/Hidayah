package com.hidayah.app.ui.screens

import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.hidayah.app.data.SettingsStore
import com.hidayah.app.reminders.DAILY_REMINDER_CHANNEL_ID
import com.hidayah.app.reminders.ReminderChannelState
import com.hidayah.app.reminders.reminderChannelState
import com.hidayah.app.ui.theme.*

private fun readReminderChannel(context: Context): ReminderChannelState = try {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = manager.getNotificationChannel(DAILY_REMINDER_CHANNEL_ID)
    reminderChannelState(channel != null, channel?.importance)
} catch (_: Exception) { ReminderChannelState.UNKNOWN }

@Composable
fun NotificationsScreen(settings: SettingsStore, onBack: () -> Unit, onSettings: () -> Unit) {
    val options by settings.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var permission by remember(context) { mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled()) }
    var channel by remember(context) { mutableStateOf(readReminderChannel(context)) }
    DisposableEffect(owner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permission = NotificationManagerCompat.from(context).areNotificationsEnabled()
                channel = readReminderChannel(context)
            }
        }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("নোটিফিকেশন", onBack)
        Page {
            Spacer(Modifier.height(20.dp))
            Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
                Text("দৈনিক স্মরণবার্তা", color = Forest, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(if (options.reminders) "অ্যাপে স্মরণবার্তা চালু করা হয়েছে।" else "অ্যাপে স্মরণবার্তা বন্ধ আছে।", color = Ink, fontSize = 14.sp)
                if (!permission) Text("ডিভাইসে নোটিফিকেশনের অনুমতি বন্ধ আছে; সিস্টেম সেটিংসে অনুমতি দিন।", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, lineHeight = 21.sp)
                if (channel == ReminderChannelState.BLOCKED) Text("দৈনিক স্মরণ চ্যানেল বন্ধ আছে; ডিভাইসের নোটিফিকেশন সেটিংসে যাচাই করুন।", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, lineHeight = 21.sp)
                if (options.reminders && channel == ReminderChannelState.NOT_CREATED) Text("প্রথম স্মরণবার্তার আগে চ্যানেল তৈরি নাও হতে পারে।", color = Muted, fontSize = 12.sp)
                if (options.reminders && channel == ReminderChannelState.UNKNOWN) Text("স্মরণবার্তার চ্যানেলের অবস্থা যাচাই করা যায়নি।", color = Muted, fontSize = 12.sp)
                Spacer(Modifier.height(6.dp))
                Text("নতুন সময়সূচির প্রথম লক্ষ্য তখনকার স্থানীয় রাত ৮টা; পরে প্রায় ২৪ ঘণ্টা পরপর। সময় অঞ্চল বা দিনের আলো সংরক্ষণ সময় বদলালে রাত ৮টার জন্য স্বয়ংক্রিয়ভাবে নতুন সময়সূচি হয় না। ডিভাইস ও অনুমতির কারণে সময় বদলাতে পারে; পৌঁছানো নিশ্চিত নয়।", color = Muted, fontSize = 13.sp, lineHeight = 21.sp)
                TextButton(onClick = onSettings) { Text("স্মরণবার্তা পরিচালনা করুন") }
            }
            Spacer(Modifier.height(12.dp))
            Text("এটি স্মরণবার্তার অবস্থা দেখায়; অ্যাপে আলাদা নোটিফিকেশন ইনবক্স নেই।", color = Muted, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}
