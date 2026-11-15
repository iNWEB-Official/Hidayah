package com.hidayah.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.hidayah.app.data.SettingsStore
import com.hidayah.app.reminders.DailyReminder
import com.hidayah.app.ui.theme.*

@Composable
fun SettingsScreen(store: SettingsStore, onBack: () -> Unit) {
    val settings by store.settings.collectAsState()
    val context = LocalContext.current
    var note by remember { mutableStateOf<String?>(null) }
    fun enableReminder() {
        if (!NotificationManagerCompat.from(context).areNotificationsEnabled()) { note = "সিস্টেম সেটিংস থেকে নোটিফিকেশন অনুমতি চালু করুন।"; return }
        runCatching { DailyReminder.schedule(context); store.setReminders(true) }
            .onFailure { note = "স্মরণবার্তা চালু করা যায়নি।" }
    }
    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) enableReminder() else note = "নোটিফিকেশন অনুমতি দেওয়া হয়নি।"
    }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("সেটিংস", onBack)
        Page {
            Spacer(Modifier.height(20.dp))
            SettingCard("থিম নির্বাচন", if (settings.dark) "ডার্ক মোড" else "লাইট মোড") {
                Switch(checked = settings.dark, onCheckedChange = store::setDark)
            }
            Spacer(Modifier.height(10.dp))
            Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(17.dp)).padding(16.dp)) {
                Text("ফন্ট সাইজ", color = Ink, fontWeight = FontWeight.Bold)
                Text("ডিভাইসের ফন্ট সেটিংসের সঙ্গে মিলিয়ে প্রয়োগ হবে", color = Muted, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(0.9f to "ছোট", 1f to "মাঝারি", 1.2f to "বড়").forEach { (size, name) ->
                        FilterChip(selected = settings.fontScale == size, onClick = { store.setFontScale(size) }, label = { Text(name) })
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            SettingCard("দৈনিক স্মরণবার্তা", "প্রথম লক্ষ্য রাত ৮টা; পরে প্রায় ২৪ ঘণ্টা পরপর। সঠিক সময় নিশ্চিত নয়।") {
                Switch(checked = settings.reminders, onCheckedChange = { enabled ->
                    note = null
                    if (!enabled) {
                        store.setReminders(false)
                        runCatching { DailyReminder.cancel(context) }.onFailure { note = "বন্ধের অনুরোধ সম্পূর্ণ করা যায়নি। আবার চেষ্টা করুন।" }
                    }
                    else if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) permission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    else enableReminder()
                })
            }
            note?.let { Spacer(Modifier.height(8.dp)); Text(it, color = Muted, fontSize = 12.sp) }
            Spacer(Modifier.height(10.dp))
            SettingCard("অ্যাপ সম্পর্কে", "হিদায়া · Android ডেমো সংস্করণ") { Text("✦", color = Forest, fontSize = 22.sp) }
            Spacer(Modifier.height(10.dp))
            Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(17.dp)).padding(16.dp)) {
                Text("গোপনীয়তা সম্পর্কে", color = Ink, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text("আমল ডেটা এই ডিভাইসে থাকে; অ্যাপ মুছে ফেললে তা মুছে যায়। অবস্থান অনুমতি দিলে নামাজ ও কিবলা হিসাব করা হয়। কুরআন, তাফসির, অডিও ও দৈনিক মূল্যবোধের জন্য ইন্টারনেট সেবা ব্যবহার হয়। এটি পূর্ণাঙ্গ প্রকাশিত গোপনীয়তা নীতি নয়।", color = Muted, fontSize = 12.sp, lineHeight = 20.sp)
            }
            Spacer(Modifier.height(28.dp))
        }
    }
}

@Composable
private fun SettingCard(title: String, subtitle: String, trailing: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(17.dp)).padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, color = Ink, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(3.dp)); Text(subtitle, color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        }
        Spacer(Modifier.width(8.dp)); trailing()
    }
}
