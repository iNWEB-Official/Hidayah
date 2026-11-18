package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hidayah.app.data.QuranChapter
import com.hidayah.app.data.QuranVerse
import com.hidayah.app.data.banglaSurahUrl
import com.hidayah.app.ui.QuranViewModel
import com.hidayah.app.ui.QuranReaderViewModel
import com.hidayah.app.ui.TafsirState
import com.hidayah.app.ui.theme.*

@Composable
fun QuranScreen(viewModel: QuranViewModel, onOpenSurah: (Int) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LazyColumn(Modifier.fillMaxSize().background(Canvas), contentPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp)) {
        item {
            PageHeader("পাঠ ও ভাবনা", "আল-কুরআন", "১১৪টি সূরা পড়ুন, শুনুন ও অর্থ জানুন।")
            Column(Modifier.fillMaxWidth().background(DeepForest, RoundedCornerShape(22.dp)).padding(22.dp)) {
                Text("পরম করুণাময় আল্লাহর নামে", color = Gold, fontWeight = FontWeight.Bold, fontSize = 10.sp, letterSpacing = 1.5.sp)
                Spacer(Modifier.height(12.dp))
                Text("পাঠ হোক সুন্দর নিয়তে", fontFamily = BanglaFont, fontSize = 25.sp, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text("আরবি, বাংলা অনুবাদ, তাফসির ও তিলাওয়াত।", color = Color(0xFFCDDDD2), fontSize = 13.sp, lineHeight = 19.sp)
            }
            Spacer(Modifier.height(28.dp))
            SectionLabel("সূরাসমূহ", if (state.surahs.isNotEmpty()) "${banglaDigits(state.surahs.size.toString())}টি সূরা" else "")
        }
        if (state.loading) item { Text("সূরার তালিকা লোড হচ্ছে…", color = Muted) }
        state.error?.let { message -> item { ErrorRetry(message, viewModel::load) } }
        items(state.surahs, key = { it.number }) { surah ->
            Row(Modifier.fillMaxWidth().clickable { onOpenSurah(surah.number) }.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(44.dp).background(Sage, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                    Text(surah.number.toString().padStart(2, '0'), color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(surah.name, fontWeight = FontWeight.SemiBold, color = Ink, fontSize = 15.sp)
                    Text("${surah.meaning} · ${banglaDigits(surah.verses.toString())} আয়াত", color = Muted, fontSize = 12.sp, lineHeight = 18.sp)
                }
                Text(surah.arabicName, color = Forest, fontSize = 17.sp, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 120.dp))
                Spacer(Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = "Open ${surah.name}", tint = Forest, modifier = Modifier.size(18.dp))
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
        }
        item { Spacer(Modifier.height(24.dp)); Text("তথ্যসূত্র: আল কুরআন ক্লাউড", color = Muted, fontSize = 11.sp) }
    }
}

@Composable
fun QuranReaderScreen(number: Int, viewModel: QuranReaderViewModel, onBack: () -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    LaunchedEffect(number) { viewModel.load(number) }
    val chapter = state.chapter
    val player = if (chapter != null && chapter.verses.isNotEmpty() && chapter.verses.all { it.audioUrl != null }) rememberRecitationPlayer(chapter) else null
    val banglaPlayer = if (chapter != null && banglaSurahUrl(chapter.surah.number) != null) rememberBanglaSurahPlayer(chapter.surah.number) else null
    Column(Modifier.fillMaxSize().background(Canvas)) {
        Row(Modifier.fillMaxWidth().padding(start = 8.dp, end = 20.dp, top = 12.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back to surahs", tint = Forest) }
            Column(Modifier.weight(1f)) {
                Text(chapter?.surah?.name ?: "সূরা ${banglaDigits(number.toString())}", color = Ink, fontSize = 21.sp, fontFamily = BanglaFont)
                Text(chapter?.surah?.meaning ?: "কুরআন পাঠ", color = Muted, fontSize = 12.sp)
            }
            if (player != null) IconButton(onClick = { banglaPlayer?.pause(); player.playWholeSurah() }) {
                Icon(Icons.Filled.PlayArrow, contentDescription = "সম্পূর্ণ সূরা আরবিতে শুনুন", tint = Forest)
            }
        }
        when {
            state.loading -> Text("আয়াত লোড হচ্ছে…", Modifier.padding(24.dp), color = Muted)
            state.error != null -> ErrorRetry(state.error!!, { viewModel.load(number, retry = true) }, Modifier.padding(24.dp))
            chapter != null -> ReaderContent(chapter, player, banglaPlayer, state.tafsirs, viewModel::toggleTafsir, viewModel::retryTafsir)
        }
    }
}

@Composable
private fun ReaderContent(chapter: QuranChapter, player: RecitationState?, banglaPlayer: BanglaSurahState?, tafsirs: Map<Int, TafsirState>, onTafsir: (Int) -> Unit, onTafsirRetry: (Int) -> Unit) {
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            if (player != null) RecitationControls(player, onBeforePlay = { banglaPlayer?.pause() })
            else Text("আরবি অডিও এখন পাওয়া যাচ্ছে না; পাঠ পড়তে পারবেন। পরে সূরাটি আবার খুলে চেষ্টা করুন।", color = Muted, fontSize = 13.sp)
            if (banglaPlayer != null) BanglaSurahControls(banglaPlayer, onBeforePlay = { player?.pause() })
            else Text("বাংলা অডিও এখন পাওয়া যাচ্ছে না।", color = Muted, fontSize = 12.sp)
        }
        item {
            Text("Arabic: Uthmani · বাংলা অনুবাদ: মুহিউদ্দীন খান (Al Quran Cloud)\nবাংলা তাফসির: আহসানুল বায়ান (Quran.com) · Reciter: Mishary Alafasy", color = Muted, fontSize = 11.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(8.dp))
        }
        items(chapter.verses, key = { it.number }) { verse ->
            VerseCard(chapter, verse, tafsirs[verse.number] ?: TafsirState(), audioAvailable = player != null, onTafsir = { onTafsir(verse.number) }, onTafsirRetry = { onTafsirRetry(verse.number) }, onPlay = { banglaPlayer?.pause(); player?.playFrom(verse.number - 1) }, onPlaySingle = { banglaPlayer?.pause(); player?.playSingle(verse.number - 1) })
        }
        item { Spacer(Modifier.height(18.dp)) }
    }
}

@Composable
private fun VerseCard(chapter: QuranChapter, verse: QuranVerse, tafsir: TafsirState, audioAvailable: Boolean, onTafsir: () -> Unit, onTafsirRetry: () -> Unit, onPlay: () -> Unit, onPlaySingle: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("${chapter.surah.number}:${verse.number}", modifier = Modifier.weight(1f), color = Forest, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            TextButton(onClick = onPlay, enabled = audioAvailable) { Text("▶ এখান থেকে শুনুন") }
        }
        TextButton(onClick = onPlaySingle, enabled = audioAvailable) { Text("▶ শুধু এই আয়াত (আরবি)") }
        Spacer(Modifier.height(10.dp))
        Text(verse.arabic, modifier = Modifier.fillMaxWidth(), color = Ink, fontFamily = ArabicFont, fontSize = 26.sp, lineHeight = 46.sp, textAlign = TextAlign.Right)
        Spacer(Modifier.height(14.dp))
        Text(verse.bangla, color = Ink, fontSize = 15.sp, lineHeight = 25.sp)
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onTafsir) { Text(if (tafsir.expanded) "তাফসির বন্ধ করুন" else "বাংলা তাফসির পড়ুন") }
        if (tafsir.expanded) {
            when {
                tafsir.loading -> Text("তাফসির লোড হচ্ছে…", color = Muted, fontSize = 13.sp)
                tafsir.error != null -> Column { Text(tafsir.error, color = Color(0xFF9D3E30), fontSize = 13.sp); TextButton(onClick = onTafsirRetry) { Text("আবার চেষ্টা করুন") } }
                tafsir.data != null -> {
                    Text(tafsir.data.resourceName, color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    if (tafsir.data.verseKeys.size > 1) Text("Covers: ${tafsir.data.verseKeys.joinToString(", ")}", color = Muted, fontSize = 11.sp)
                    Spacer(Modifier.height(8.dp))
                    val text = remember(tafsir.data.textHtml) { HtmlCompat.fromHtml(tafsir.data.textHtml, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim() }
                    Text(text, color = Ink, fontSize = 14.sp, lineHeight = 24.sp)
                }
            }
        }
    }
}

@Composable
private fun ErrorRetry(message: String, retry: () -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) { Text(message, color = Color(0xFF9D3E30), fontSize = 13.sp); TextButton(onClick = retry) { Text("আবার চেষ্টা করুন") } }
}
