package com.hidayah.app.ui.screens

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
import com.hidayah.app.ui.theme.*

@Composable
fun NotificationsScreen(settings: SettingsStore, onBack: () -> Unit, onSettings: () -> Unit) {
    val options by settings.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    var permission by remember(context) { mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled()) }
    DisposableEffect(owner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) permission = NotificationManagerCompat.from(context).areNotificationsEnabled()
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
                Text(if (options.reminders) "অ্যাপের স্মরণবার্তা চালু আছে।" else "অ্যাপের স্মরণবার্তা বন্ধ আছে।", color = Ink, fontSize = 14.sp)
                if (!permission) Text("ডিভাইসে নোটিফিকেশনের অনুমতি বন্ধ আছে; সিস্টেম সেটিংসে অনুমতি দিন।", color = MaterialTheme.colorScheme.error, fontSize = 13.sp, lineHeight = 21.sp)
                Spacer(Modifier.height(6.dp))
                Text("স্মরণবার্তাটি প্রায় রাত ৮টায় আসে; ডিভাইসের নিয়মে দেরি হতে পারে।", color = Muted, fontSize = 13.sp, lineHeight = 21.sp)
                TextButton(onClick = onSettings) { Text("স্মরণবার্তা পরিচালনা করুন") }
            }
            Spacer(Modifier.height(12.dp))
            Text("এটি স্মরণবার্তার অবস্থা দেখায়; অ্যাপে আলাদা নোটিফিকেশন ইনবক্স নেই।", color = Muted, fontSize = 12.sp, lineHeight = 20.sp)
        }
    }
}
