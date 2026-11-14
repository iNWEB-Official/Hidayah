package com.hidayah.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hidayah.app.data.ValueTopics
import com.hidayah.app.data.VerseReference
import com.hidayah.app.data.searchValueTopics
import com.hidayah.app.ui.ValuesViewModel
import com.hidayah.app.ui.VerseLoadState
import com.hidayah.app.ui.theme.*

@Composable
fun ValuesScreen(onBack: () -> Unit, onOpen: (String) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { searchValueTopics(query) }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("বিষয়ভিত্তিক মূল্যবোধ", onBack)
        Page {
            Spacer(Modifier.height(22.dp))
            Text("সুন্দর চরিত্রের পথে", color = Forest, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text("বিষয় বেছে নিন, সংশ্লিষ্ট আয়াত ও বাংলা অর্থ পড়ুন।", color = Muted, fontSize = 13.sp, lineHeight = 20.sp)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                label = { Text("বিষয় বা আয়াত নম্বর খুঁজুন") }, singleLine = true,
                supportingText = { Text("যেমন: ধৈর্য বা ৩৩:৭০") })
            Spacer(Modifier.height(8.dp))
            if (results.isEmpty()) Text("এই খোঁজে কোনো বিষয় পাওয়া যায়নি।", color = Muted, fontSize = 14.sp)
            results.forEach { topic ->
                val originalIndex = ValueTopics.indexOfFirst { it.id == topic.id }
                Row(Modifier.fillMaxWidth().padding(bottom = 12.dp).background(CardSurface, RoundedCornerShape(18.dp))
                    .clickable(role = Role.Button, onClickLabel = "${topic.title} খুলুন") { onOpen(topic.id) }.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(52.dp).background(if (originalIndex % 2 == 0) Forest else Color(0xFFFFDA77), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Text(listOf("✦", "♧", "❋", "◇")[originalIndex], color = if (originalIndex % 2 == 0) MaterialTheme.colorScheme.onPrimary else Color(0xFF075C3A), fontSize = 25.sp)
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(topic.title, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text(topic.subtitle, color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    }
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Forest)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ValueDetailScreen(topicId: String, viewModel: ValuesViewModel, onBack: () -> Unit) {
    val topic = remember(topicId) { ValueTopics.firstOrNull { it.id == topicId } }
    val state by viewModel.verses.collectAsStateWithLifecycle()
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader(topic?.title ?: "বিষয় পাওয়া যায়নি", onBack)
        if (topic == null) {
            Text("এই বিষয়টি পাওয়া যায়নি।", Modifier.padding(20.dp), color = Muted)
        } else {
            LaunchedEffect(topic.id) { topic.verses.forEach { viewModel.load(it) } }
            Page {
                Spacer(Modifier.height(20.dp))
                Column(Modifier.fillMaxWidth().background(DeepForest, RoundedCornerShape(20.dp)).padding(20.dp)) {
                    Text(topic.subtitle, color = Gold, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(topic.detail, color = Color.White, fontSize = 15.sp, lineHeight = 24.sp)
                }
                Spacer(Modifier.height(22.dp))
                SectionLabel("কুরআনের আলোকে", "${banglaDigits(topic.verses.size.toString())}টি আয়াত")
                topic.verses.forEach { ref ->
                    VerseCardForValue(ref, state[ref.key], onRetry = { viewModel.load(ref, retry = true) })
                    Spacer(Modifier.height(12.dp))
                }
                Text("আরবি: উসমানী লিপি · বাংলা অনুবাদ: মুহিউদ্দীন খান · তথ্য: আল কুরআন ক্লাউড।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(8.dp))
                Text("এটি সংক্ষিপ্ত বিষয়ভিত্তিক পরিচিতি, পূর্ণাঙ্গ তাফসির বা ধর্মীয় সিদ্ধান্ত নয়। মূল প্রসঙ্গসহ পড়ুন।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun VerseCardForValue(reference: VerseReference, state: VerseLoadState?, onRetry: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(reference.label, color = Forest, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(Modifier.height(4.dp))
        Text("কুরআন ${banglaDigits(reference.key)}", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        when (state) {
            null, VerseLoadState.Loading -> Text("আয়াত লোড হচ্ছে…", color = Muted, fontSize = 13.sp)
            VerseLoadState.Error -> {
                Text("আয়াত পাওয়া যায়নি। ইন্টারনেট সংযোগ পরীক্ষা করুন।", color = Muted, fontSize = 13.sp)
                TextButton(onClick = onRetry) { Text("আবার চেষ্টা করুন") }
            }
            is VerseLoadState.Loaded -> {
                val verse = state.verse
                Text(verse.arabic, modifier = Modifier.fillMaxWidth(), fontFamily = ArabicFont, color = Ink, fontSize = 25.sp, lineHeight = 43.sp, textAlign = TextAlign.Right)
                Spacer(Modifier.height(14.dp))
                Text(verse.bangla, color = Ink, fontSize = 15.sp, lineHeight = 25.sp)
                Spacer(Modifier.height(8.dp))
                Text("বাংলা অনুবাদ: মুহিউদ্দীন খান", color = Muted, fontSize = 12.sp)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        val text = "কুরআন ${banglaDigits(reference.key)}\n\n${verse.arabic}\n\n${verse.bangla}\n\nবাংলা অনুবাদ: মুহিউদ্দীন খান · আল কুরআন ক্লাউড"
                        val intent = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, text)
                        context.startActivity(Intent.createChooser(intent, "আয়াত শেয়ার করুন"))
                    }) { Text("শেয়ার করুন") }
                    TextButton(onClick = { runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://quran.com/${reference.chapter}/${reference.verse}"))) } }) { Text("মূল প্রসঙ্গ") }
                }
            }
        }
    }
}
