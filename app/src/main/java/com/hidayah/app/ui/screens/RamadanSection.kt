package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.ui.PrayerUiState
import com.hidayah.app.ui.theme.*
import java.time.format.DateTimeFormatter

@Composable
fun RamadanSection(state: PrayerUiState) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    SectionLabel("রমজানের সময়সূচি")
    Text("ফজরের শুরুতে সেহরি শেষ এবং মাগরিবে ইফতার শুরু। অবস্থান ও নির্বাচিত পদ্ধতি অনুযায়ী সময় হিসাব করা হয়।", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    Spacer(Modifier.height(12.dp))
    val timetable = state.ramadan
    when {
        state.point == null -> Text("সময়সূচি দেখতে উপরে অবস্থান নির্ধারণ করুন।", color = Muted, fontSize = 13.sp)
        state.ramadanLoading -> Text("রমজানের সময়সূচি হিসাব করা হচ্ছে…", color = Muted, fontSize = 13.sp)
        timetable == null -> Text(state.ramadanError ?: "সময়সূচি পাওয়া যায়নি। অবস্থান আবার নির্ধারণ করুন।", color = Muted, fontSize = 13.sp)
        else -> {
            Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(16.dp)) {
                Text("আনুমানিক রমজান ${banglaDigits(timetable.hijriYear.toString())} হিজরি", color = Forest, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                Text("বর্তমান বা আসন্ন রমজান · ${state.zone.id}", color = Muted, fontSize = 12.sp)
                TextButton(onClick = { expanded = !expanded }) { Text(if (expanded) "সময়সূচি বন্ধ করুন" else "${banglaDigits(timetable.days.size.toString())} দিনের সময়সূচি দেখুন") }
                if (expanded) {
                    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("তারিখ", modifier = Modifier.weight(1.1f), color = Muted, fontSize = 12.sp)
                        Text("সেহরি শেষ", modifier = Modifier.weight(1f), color = Muted, fontSize = 12.sp, textAlign = TextAlign.End)
                        Text("ইফতার", modifier = Modifier.weight(1f), color = Muted, fontSize = 12.sp, textAlign = TextAlign.End)
                    }
                    timetable.days.forEach { day ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("${day.day}. ${day.date.format(DateTimeFormatter.ofPattern("d MMM"))}", modifier = Modifier.weight(1.1f), color = Ink, fontSize = 12.sp)
                            Text(day.sehriEnd ?: "—", modifier = Modifier.weight(1f), color = Ink, fontSize = 12.sp, textAlign = TextAlign.End)
                            Text(day.iftar ?: "—", modifier = Modifier.weight(1f), color = Ink, fontSize = 12.sp, textAlign = TextAlign.End)
                        }
                        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
                    }
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))
    Text("তারিখ উম্মুল কুরা ক্যালেন্ডারভিত্তিক আনুমানিক; স্থানীয় চাঁদ দেখা ও ঘোষণার সঙ্গে ভিন্ন হতে পারে। তারিখ ও সময় স্থানীয় বিশ্বস্ত কর্তৃপক্ষের সঙ্গে মিলিয়ে নিন।", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
    Spacer(Modifier.height(20.dp))
    DuaCard("সেহরির খাবারের আগে", "بِسْمِ اللَّهِ", "আল্লাহর নামে।", "খাবারের আগে সাধারণ দোয়া (সুনান আবু দাউদ ৩৭৬৭)। রোজার নিয়ত অন্তরের বিষয়; নির্দিষ্ট বাক্য বলা আবশ্যক নয়।")
    Spacer(Modifier.height(10.dp))
    DuaCard("ইফতারের পর", "ذَهَبَ الظَّمَأُ وَابْتَلَّتِ الْعُرُوقُ وَثَبَتَ الْأَجْرُ إِنْ شَاءَ اللَّهُ", "পিপাসা দূর হয়েছে, শিরা-উপশিরা সিক্ত হয়েছে এবং আল্লাহ চাইলে সওয়াব নিশ্চিত হয়েছে।", "সুনান আবু দাউদ ২৩৫৭")
}

@Composable
private fun DuaCard(title: String, arabic: String, bangla: String, source: String) {
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(16.dp)) {
        Text(title, color = Forest, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        Text(arabic, modifier = Modifier.fillMaxWidth(), color = Ink, fontFamily = ArabicFont, fontSize = 23.sp, lineHeight = 38.sp, textAlign = TextAlign.Right)
        Spacer(Modifier.height(8.dp))
        Text(bangla, color = Ink, fontSize = 14.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(8.dp))
        Text(source, color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
    }
}
