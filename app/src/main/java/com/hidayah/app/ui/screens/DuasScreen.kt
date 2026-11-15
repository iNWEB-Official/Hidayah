package com.hidayah.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hidayah.app.data.*
import com.hidayah.app.ui.DuaSpeech
import com.hidayah.app.ui.theme.*

@Composable
fun DuasScreen(initialQuery: String = "", onBack: () -> Unit) {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val favorites = remember(context.applicationContext) { DuaFavorites(context) }
    val speech = remember(context.applicationContext, owner) { DuaSpeech(context) }
    val favoriteState by favorites.state.collectAsStateWithLifecycle()
    val favoriteIds = favoriteState.ids
    val speechState by speech.state.collectAsStateWithLifecycle()
    var category by rememberSaveable { mutableStateOf("সব") }
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var actionError by remember { mutableStateOf<Pair<String, String>?>(null) }
    val results = remember(query, category, favoriteIds) { searchDuas(query, category, favoriteIds) }
    DisposableEffect(owner, speech) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_PAUSE) speech.stop() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); speech.close() }
    }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("দৈনন্দিন দোয়া ও জিকির", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("দোয়া, অর্থ ও সূত্র", color = Forest, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text("বিভাগ নির্বাচন করুন বা দোয়া খুঁজুন।", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(),
                    label = { Text("দোয়া, অর্থ বা সূত্র খুঁজুন") }, singleLine = true)
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    items(DuaCategories) { item -> FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item, fontSize = 12.sp) }) }
                }
            }
            item {
                if (favoriteState.readFailed) {
                    Text(favoriteState.error ?: "পছন্দের তালিকা পড়া যায়নি।", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                    TextButton(onClick = favorites::retryRead) { Text("পছন্দের তালিকা আবার পড়ুন") }
                }
                Text("যন্ত্রে শোনার কণ্ঠটি কৃত্রিম; উচ্চারণে ভুল হতে পারে। নির্ভরযোগ্য পাঠের সঙ্গে মিলিয়ে নিন।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                if (speechState.playingId != null) TextButton(onClick = speech::stop) { Text("পাঠ বন্ধ করুন") }
                speechState.message?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, lineHeight = 19.sp) }
            }
            if (results.isEmpty()) item {
                Text(if (category == "পছন্দ" && favoriteState.readFailed) "পছন্দের তালিকা এখন দেখানো যাচ্ছে না।" else if (category == "পছন্দ" && query.isBlank()) "এখনো কোনো দোয়া পছন্দের তালিকায় নেই।" else "কোনো দোয়া পাওয়া যায়নি। অন্য শব্দ বা বিভাগ দিয়ে চেষ্টা করুন।", color = Muted, fontSize = 14.sp)
            }
            items(results, key = { it.id }) { dua ->
                DuaCard(dua = dua, isFavorite = dua.id in favoriteIds, favoriteEnabled = !favoriteState.readFailed, playing = speechState.playingId == dua.id,
                    actionError = actionError?.takeIf { it.first == dua.id }?.second,
                    onFavorite = {
                        actionError = null
                        if (!favorites.setFavorite(dua.id, it)) actionError = dua.id to "পছন্দের পরিবর্তন সংরক্ষণ করা যায়নি। আবার চেষ্টা করুন।"
                    },
                    onSpeak = { if (speechState.playingId == dua.id) speech.stop() else speech.play(dua) },
                    onShare = {
                        actionError = null
                        runCatching {
                            val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, formatDuaShare(dua))
                            context.startActivity(Intent.createChooser(send, "দোয়া শেয়ার করুন"))
                        }.onFailure { actionError = dua.id to "দোয়া শেয়ার করা যায়নি। আবার চেষ্টা করুন।" }
                    },
                    onSource = {
                        actionError = null
                        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(dua.sourceUrl))) }
                            .onFailure { actionError = dua.id to "সূত্র খোলা যায়নি। আবার চেষ্টা করুন।" }
                    })
            }
            item { Spacer(Modifier.height(14.dp)); Text("আরবি ও সূত্রের পাশাপাশি বাংলা অর্থ ও উচ্চারণ যোগ করা হয়েছে। উচ্চারণ সহায়ক মাত্র; কনটেন্ট প্রকাশের আগে পর্যালোচনা প্রয়োজন।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp) }
        }
    }
}

@Composable
private fun DuaCard(dua: DuaItem, isFavorite: Boolean, favoriteEnabled: Boolean, playing: Boolean, actionError: String?, onFavorite: (Boolean) -> Unit, onSpeak: () -> Unit, onShare: () -> Unit, onSource: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(dua.category, color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(4.dp))
        Text(dua.title, color = Forest, fontSize = 17.sp, fontWeight = FontWeight.Bold, lineHeight = 26.sp)
        Spacer(Modifier.height(14.dp))
        Text(dua.arabic, modifier = Modifier.fillMaxWidth(), fontFamily = ArabicFont, color = Ink, fontSize = 27.sp, lineHeight = 45.sp, textAlign = TextAlign.Right)
        Spacer(Modifier.height(12.dp))
        Text("উচ্চারণ: ${dua.pronunciation}", color = Ink, fontSize = 14.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(8.dp))
        Text("অর্থ: ${dua.meaning}", color = Ink, fontSize = 14.sp, lineHeight = 23.sp)
        Spacer(Modifier.height(8.dp))
        Text("সূত্র: ${dua.source}", color = Muted, fontSize = 12.sp)
        actionError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, lineHeight = 19.sp) }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Button(onClick = onSpeak, modifier = Modifier.weight(1f)) { Text(if (playing) "থামান" else "যন্ত্রে শুনুন", fontSize = 12.sp) }
            OutlinedButton(onClick = onShare, modifier = Modifier.weight(1f)) { Text("শেয়ার", fontSize = 12.sp) }
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            TextButton(enabled = favoriteEnabled, onClick = { onFavorite(!isFavorite) }) { Text(if (isFavorite) "♥ পছন্দে আছে" else "♡ পছন্দে রাখুন", fontSize = 12.sp) }
            TextButton(onClick = onSource) { Text("মূল সূত্র", fontSize = 12.sp) }
        }
    }
}
