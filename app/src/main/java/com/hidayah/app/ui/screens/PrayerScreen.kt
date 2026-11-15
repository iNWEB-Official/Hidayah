package com.hidayah.app.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hidayah.app.data.AsrMethod
import com.hidayah.app.data.PrayerMethod
import com.hidayah.app.ui.PrayerViewModel
import com.hidayah.app.ui.theme.*
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.delay

@Composable
fun PrayerScreen(viewModel: PrayerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        if (permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true || permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true)
            viewModel.refreshLocation()
        else viewModel.permissionDenied()
    }
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (!fine && !coarse && (viewModel.state.value.point != null || viewModel.state.value.loading)) viewModel.permissionDenied()
                else viewModel.recalculate()
                delay(60_000L)
            }
        }
    }
    Page {
        PageHeader("ইবাদতের সময়", "নামাজের সময়", "সময়মতো নামাজের জন্য প্রস্তুত থাকুন।")
        Column(Modifier.fillMaxWidth().background(DeepForest, RoundedCornerShape(22.dp)).padding(22.dp)) {
            Text("আপনার অবস্থান", color = Gold, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.5.sp)
            Spacer(Modifier.height(12.dp))
            Text(if (state.point == null) "নামাজের সময় দেখুন" else "দৈনিক নামাজ", fontFamily = FontFamily.Serif, fontSize = 25.sp, color = Color.White)
            Spacer(Modifier.height(8.dp))
            Text("নামাজ ও কিবলা হিসাবের জন্য অবস্থান ব্যবহৃত হয়। ফোনের সময় অঞ্চল অবস্থানের সঙ্গে মিলিয়ে নিন।", color = Color(0xFFCDDDD2), fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(14.dp))
            Button(enabled = !state.loading, onClick = {
                val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                if (fine || coarse) viewModel.refreshLocation()
                else permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }) { Text(if (state.loading) "অবস্থান খোঁজা হচ্ছে…" else if (state.point == null) "আমার অবস্থান ব্যবহার করুন" else "অবস্থান হালনাগাদ করুন") }
        }
        state.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialError, fontSize = 13.sp, lineHeight = 19.sp)
        }
        state.point?.accuracyMeters?.let {
            Spacer(Modifier.height(10.dp))
            Text("অবস্থানের নির্ভুলতা: প্রায় ${banglaDigits(it.toInt().toString())} মিটার", color = Muted, fontSize = 12.sp)
        }
        Spacer(Modifier.height(26.dp))
        SectionLabel("হিসাবের সেটিংস")
        Text("আপনার স্থানীয় মসজিদ বা কর্তৃপক্ষের পদ্ধতি নির্বাচন করুন। পছন্দ এই ডিভাইসে সংরক্ষিত হয়।", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        state.settingsError?.let { Text(it, color = MaterialError, fontSize = 12.sp, lineHeight = 18.sp) }
        Spacer(Modifier.height(10.dp))
        SettingMenu("পদ্ধতি", state.settings.method.label, PrayerMethod.entries.toList(), { it.label }, viewModel::setMethod)
        Spacer(Modifier.height(8.dp))
        SettingMenu("আসর", state.settings.asr.label, AsrMethod.entries.toList(), { it.label }, viewModel::setAsr)
        Spacer(Modifier.height(28.dp))
        SectionLabel("আজকের নামাজ", state.date.format(DateTimeFormatter.ofPattern("d MMM yyyy")))
        Text("সময় অঞ্চল: ${state.zone.id}", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp))
        val prayers = state.prayers.ifEmpty { listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha").map { com.hidayah.app.data.Prayer(it) } }
        prayers.forEach { prayer ->
            Row(Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                IconTile(AppIcons.Prayer)
                Spacer(Modifier.width(14.dp))
                Text(mapOf("Fajr" to "ফজর", "Dhuhr" to "যোহর", "Asr" to "আসর", "Maghrib" to "মাগরিব", "Isha" to "এশা")[prayer.name] ?: prayer.name, modifier = Modifier.weight(1f), color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Text(prayer.time ?: "—", color = if (prayer.time == null) Muted else Forest, fontSize = 15.sp, fontWeight = FontWeight.Medium)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
        }
        Spacer(Modifier.height(16.dp))
        Text("সময় আনুমানিক। স্থানীয় মসজিদের সময়সূচির সঙ্গে পদ্ধতি ও সময় মিলিয়ে নিন।", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(30.dp))
        RamadanSection(state)
        Spacer(Modifier.height(30.dp))
        SectionLabel("কিবলার দিক")
        val point = state.point
        val bearing = state.qiblaBearing
        if (point != null && bearing != null) QiblaCompass(point, bearing)
        else Text(if (point == null) "কিবলার দিক দেখতে অবস্থান অনুমতি দিন।" else "এই অবস্থানে কিবলার দিক পাওয়া যায়নি। স্থানীয় নির্ভরযোগ্য উৎসে যাচাই করুন।", color = Muted, fontSize = 13.sp)
        Spacer(Modifier.height(32.dp))
    }
}

private val MaterialError = Color(0xFF9D3E30)

@Composable
private fun <T> SettingMenu(label: String, selected: String, options: List<T>, labelOf: (T) -> String, onSelected: (T) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text("$label: $selected ▾", modifier = Modifier.weight(1f), color = Forest)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(labelOf(option)) }, onClick = { onSelected(option); expanded = false }) }
        }
    }
}
