package com.hidayah.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.hidayah.app.R
import com.hidayah.app.data.DailyValueStatus
import com.hidayah.app.ui.HomeViewModel
import com.hidayah.app.ui.TrackerViewModel
import com.hidayah.app.ui.theme.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

fun banglaDigits(value: String): String = value.map { if (it in '0'..'9') '০' + (it - '0') else it }.joinToString("")
private val banglaLocale = Locale("bn", "BD")
@Composable
internal fun Page(content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(Canvas).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp), content = content)
}
@Composable
internal fun PageHeader(eyebrow: String, title: String, subtitle: String) {
    Spacer(Modifier.height(30.dp)); Text(eyebrow, color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp)); Text(title, fontSize = 30.sp, lineHeight = 40.sp, color = Ink, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(5.dp)); Text(subtitle, color = Muted, fontSize = 14.sp, lineHeight = 21.sp); Spacer(Modifier.height(24.dp))
}
@Composable
internal fun SectionLabel(title: String, trailing: String = "") {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold)
        if (trailing.isNotEmpty()) Text(trailing, color = Muted, fontSize = 12.sp)
    }; Spacer(Modifier.height(12.dp))
}
@Composable
internal fun IconTile(icon: ImageVector, background: Color = Sage, tint: Color = Forest) {
    Box(Modifier.size(48.dp).background(background, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(23.dp))
    }
}

@Composable
fun HomeScreen(viewModel: HomeViewModel, onOpenQuran: () -> Unit, onOpenPrayer: () -> Unit, onOpenTracker: () -> Unit,
               onValues: () -> Unit, onDuas: () -> Unit, onLearning: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner, viewModel) { owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { while (true) { viewModel.refreshDate(); delay(60_000L) } } }
    Column(Modifier.fillMaxSize().background(Canvas).verticalScroll(rememberScrollState())) {
        Box(Modifier.fillMaxWidth().heightIn(min = 216.dp)) {
            Image(painterResource(R.drawable.hidayah_banner), contentDescription = null, modifier = Modifier.matchParentSize(), contentScale = ContentScale.Crop)
            Box(Modifier.matchParentSize().background(Brush.horizontalGradient(listOf(Color(0xD9002818), Color(0x22002818)))))
            Column(Modifier.align(Alignment.CenterStart).padding(start = 24.dp, end = 90.dp, top = 32.dp, bottom = 24.dp)) {
                Text("হিদায়া", color = Color.White, fontSize = 37.sp, fontWeight = FontWeight.Bold)
                Text("সুন্দর পথে, সুন্দর জীবনে", color = Color(0xFFFFE29B), fontSize = 13.sp)
                Spacer(Modifier.height(12.dp)); Text("আসসালামু আলাইকুম", color = Color.White, fontSize = 13.sp)
            }
        }
        Column(Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(20.dp))
            Text(banglaDigits(state.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy, EEEE", banglaLocale))), color = Muted, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))
            Column(Modifier.fillMaxWidth().background(DeepForest, RoundedCornerShape(21.dp)).padding(20.dp)) {
                Text("আজকের মূল্যবোধ", color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                when (val daily = state.dailyValue) {
                    DailyValueStatus.Loading -> Text("আজকের মূল্যবোধ লোড হচ্ছে…", color = Color.White)
                    is DailyValueStatus.Missing -> {
                        Text(if (daily.fromCache) "সংরক্ষিত তথ্য পাওয়া যায়নি। ইন্টারনেট সংযোগ পরীক্ষা করুন।" else "আজকের মূল্যবোধ এখনো প্রকাশিত হয়নি।", color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
                        TextButton(onClick = viewModel::retry) { Text("আবার চেষ্টা করুন", color = Gold) }
                    }
                    DailyValueStatus.Invalid -> {
                        Text("আজকের বিষয়বস্তু সম্পূর্ণ পাওয়া যায়নি। পরে আবার দেখুন।", color = Color.White)
                        TextButton(onClick = viewModel::retry) { Text("আবার চেষ্টা করুন", color = Gold) }
                    }
                    is DailyValueStatus.Error -> { Text("তথ্য আনা যায়নি। ইন্টারনেট সংযোগ পরীক্ষা করে আবার চেষ্টা করুন।", color = Color.White, fontSize = 13.sp); TextButton(onClick = viewModel::retry) { Text("আবার চেষ্টা করুন", color = Gold) } }
                    is DailyValueStatus.Available -> {
                        Text(daily.value.title, fontSize = 24.sp, lineHeight = 33.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(6.dp)); Text(daily.value.reflection, color = Color(0xFFE3EDE3), fontSize = 14.sp, lineHeight = 22.sp)
                        if (daily.fromCache) Text("সংরক্ষিত কপি", color = Gold, fontSize = 11.sp)
                        if (daily.isFallback) {
                            Text("বিকল্প দৈনিক লেখা দেখানো হচ্ছে।", color = Gold, fontSize = 11.sp)
                            TextButton(onClick = viewModel::retry) { Text("অনলাইনে আবার চেষ্টা করুন", color = Gold) }
                        }
                    }
                }
            }
            val daily = state.dailyValue
            if (daily is DailyValueStatus.Available) {
                Spacer(Modifier.height(14.dp))
                Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(20.dp)).border(1.dp, Gold.copy(alpha = .5f), RoundedCornerShape(20.dp)).padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(if (daily.value.referenceType == "Hadith") "দৈনিক হাদিস" else "দৈনিক আয়াত", color = Forest, fontWeight = FontWeight.Bold)
                    daily.value.referenceArabic?.let { arabic ->
                        Spacer(Modifier.height(10.dp))
                        Text(arabic, modifier = Modifier.fillMaxWidth(), color = Ink, fontFamily = ArabicFont,
                            fontSize = 25.sp, lineHeight = 42.sp, textAlign = TextAlign.Center)
                    }
                    Spacer(Modifier.height(10.dp)); Text(daily.value.referenceText, color = Ink, fontSize = 16.sp, lineHeight = 26.sp, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(6.dp)); Text("সূত্র: ${daily.value.reference}", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
                    DailyShareActions(daily.value)
                }
            }
            Spacer(Modifier.height(24.dp)); SectionLabel("আপনার দৈনন্দিন সঙ্গী")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                HomeTile("মূল্যবোধ\nশিখুন", AppIcons.Quran, Modifier.weight(1f), onValues)
                HomeTile("আমল\nট্র্যাকার", Icons.Outlined.CheckCircle, Modifier.weight(1f), onOpenTracker)
                HomeTile("দোয়া ও\nজিকির", AppIcons.Prayer, Modifier.weight(1f), onDuas)
            }
            Spacer(Modifier.height(20.dp)); SectionLabel("আরও জানুন")
            HomeLink("আল-কুরআন", "বাংলা অনুবাদ, তাফসির ও তিলাওয়াত", onOpenQuran)
            HomeLink("নামাজের সময় ও রমজান", "সময়সূচি ও কিবলা", onOpenPrayer)
            HomeLink("শিক্ষণীয় গল্প ও কুইজ", "সংক্ষিপ্ত গল্প পড়ুন ও কুইজ খেলুন", onLearning)
            Spacer(Modifier.height(24.dp))
        }
    }
}
@Composable
private fun HomeTile(label: String, icon: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.heightIn(min = 105.dp).clip(RoundedCornerShape(17.dp)).background(CardSurface).clickable(role = Role.Button, onClick = onClick).padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Icon(icon, contentDescription = null, tint = Forest, modifier = Modifier.size(27.dp)); Spacer(Modifier.height(5.dp))
        Text(label, color = Ink, fontSize = 12.sp, lineHeight = 18.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}
@Composable
private fun HomeLink(title: String, subtitle: String, onClick: () -> Unit) {
    Row(Modifier.fillMaxWidth().padding(bottom = 9.dp).clip(RoundedCornerShape(16.dp)).background(CardSurface).clickable(role = Role.Button, onClick = onClick).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(title, color = Ink, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, fontSize = 12.sp) }
        Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Forest)
    }
}

@Composable
fun TrackerScreen(viewModel: TrackerViewModel) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    LaunchedEffect(owner, viewModel) { owner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) { while (true) { viewModel.refreshDate(); delay(60_000L) } } }
    val habits = state.snapshot.habits
    val completed = habits.count { it.completed }
    val labels = mapOf("fajr" to "ফজর", "dhuhr" to "যোহর", "asr" to "আসর", "maghrib" to "মাগরিব", "isha" to "এশা", "quran" to "কুরআন তিলাওয়াত", "good_deed" to "একটি ভালো কাজ")
    Page {
        PageHeader("প্রতিদিন একটু ভালো", "দৈনিক আমল ট্র্যাকার", "আপনার ছোট ছোট ভালো কাজের হিসাব রাখুন।")
        Column(Modifier.fillMaxWidth().background(DeepForest, RoundedCornerShape(20.dp)).padding(20.dp)) {
            Text("আজ · ${banglaDigits(state.snapshot.date.format(DateTimeFormatter.ofPattern("d MMMM yyyy", banglaLocale)))}", color = Gold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp)); Text("${banglaDigits(completed.toString())} / ${banglaDigits(habits.size.toString())}", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Text("আজকের আমল সম্পন্ন", color = Color(0xFFD9E9DA), fontSize = 13.sp)
        }
        state.error?.let { Spacer(Modifier.height(10.dp)); Text("তথ্য সংরক্ষণ বা পড়া যায়নি।", color = Color(0xFFB24838), fontSize = 13.sp) }
        Spacer(Modifier.height(22.dp)); SectionLabel("আজকের তালিকা", if (state.loading) "লোড হচ্ছে…" else "ট্যাপ করে চিহ্নিত করুন")
        habits.forEach { habit ->
            Row(Modifier.fillMaxWidth().padding(bottom = 8.dp).background(CardSurface, RoundedCornerShape(16.dp)).toggleable(value = habit.completed, enabled = !state.loading, role = Role.Checkbox, onValueChange = { viewModel.setCompleted(habit.id, it) }).padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(labels[habit.id] ?: habit.title, modifier = Modifier.weight(1f), color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                Box(Modifier.size(28.dp).background(if (habit.completed) Forest else Color.Transparent, CircleShape).border(1.dp, if (habit.completed) Forest else Muted, CircleShape), contentAlignment = Alignment.Center) {
                    if (habit.completed) Icon(Icons.Outlined.Check, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(17.dp))
                }
            }
        }
        Spacer(Modifier.height(20.dp)); SectionLabel("সাপ্তাহিক অগ্রগতি", "আজসহ সাত দিন")
        Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(17.dp)).padding(16.dp)) {
            val totalDone = state.snapshot.history.sumOf { it.completed }
            val totalPossible = state.snapshot.history.sumOf { it.total }
            val percent = if (totalPossible == 0) 0 else totalDone * 100 / totalPossible
            Text("${banglaDigits(percent.toString())}% সম্পন্ন", color = Forest, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(progress = { percent / 100f }, modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), color = Forest, trackColor = Sage)
            state.snapshot.history.forEach { day ->
                Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(banglaDigits(day.date.format(DateTimeFormatter.ofPattern("EEE, d MMM", banglaLocale))), color = Ink, fontSize = 12.sp)
                    Text("${banglaDigits(day.completed.toString())} / ${banglaDigits(day.total.toString())}", color = Forest, fontSize = 12.sp)
                }
            }
        }
        Spacer(Modifier.height(12.dp)); Text("তথ্য এই ডিভাইসে সংরক্ষিত থাকে। স্থানীয় সময় অনুযায়ী নতুন দিন শুরু হয়।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(24.dp))
    }
}
