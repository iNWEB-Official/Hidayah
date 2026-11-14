package com.hidayah.app.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.data.*
import com.hidayah.app.ui.theme.*

private val QuizProgressSaver = listSaver<QuizProgress, Int>(
    save = { listOf(it.position) + it.answers },
    restore = { saved ->
        val position = saved.firstOrNull() ?: 0
        val answers = saved.drop(1)
        if (position !in 0..QuizQuestions.size || answers.size != QuizQuestions.size ||
            answers.withIndex().any { (i, value) -> value != -1 && value !in QuizQuestions[i].answers.indices } ||
            answers.take(position).any { it == -1 } || answers.drop(position + 1).any { it != -1 }) QuizProgress()
        else QuizProgress(position, answers)
    }
)

@Composable
fun LearningScreen(initialQuery: String = "", onBack: () -> Unit) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var category by rememberSaveable { mutableStateOf("সব") }
    var query by rememberSaveable { mutableStateOf(initialQuery) }
    var expandedId by rememberSaveable { mutableStateOf("") }
    var progress by rememberSaveable(stateSaver = QuizProgressSaver) { mutableStateOf(QuizProgress()) }
    var sourceError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val stories = remember(query, category) { searchStories(query, category) }
    fun openSource(url: String) {
        sourceError = null
        runCatching { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url))) }
            .onFailure { sourceError = "সূত্র খুলতে ব্রাউজার পাওয়া যায়নি।" }
    }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("শিক্ষণীয় গল্প ও কুইজ", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("গল্প", "কুইজ").forEachIndexed { index, label ->
                        FilterChip(selected = tab == index, onClick = { tab = index; sourceError = null },
                            label = { Text(label) }, modifier = Modifier.weight(1f))
                    }
                }
                sourceError?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp) }
            }
            if (tab == 0) {
                item {
                    Text("কুরআন থেকে সংক্ষিপ্ত পরিচিতি", color = Forest, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("এগুলো আয়াতের সরাসরি অনুবাদ নয়। মূল অংশ পড়তে সূত্র দেখুন।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), label = { Text("গল্প বা সূত্র খুঁজুন") }, singleLine = true)
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(StoryCategories) { value -> FilterChip(selected = category == value, onClick = { category = value }, label = { Text(value, fontSize = 12.sp) }) }
                    }
                }
                if (stories.isEmpty()) item { Text("কোনো গল্প পাওয়া যায়নি। অন্য শব্দ বা বিভাগ দিয়ে চেষ্টা করুন।", color = Muted, fontSize = 14.sp) }
                items(stories, key = { it.id }) { story ->
                    StoryCard(story, expanded = expandedId == story.id,
                        onToggle = { expandedId = if (expandedId == story.id) "" else story.id },
                        onSource = { openSource(story.url) })
                }
            } else if (!progress.complete) {
                val question = QuizQuestions[progress.position]
                val selected = progress.answers[progress.position]
                item {
                    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
                        Text("প্রশ্ন ${progress.position + 1} / ${QuizQuestions.size}", color = Forest, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(progress = { (progress.position + 1).toFloat() / QuizQuestions.size }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(14.dp))
                        Text(question.question, color = Ink, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp)
                        Spacer(Modifier.height(12.dp))
                        question.answers.forEachIndexed { index, text ->
                            OutlinedButton(onClick = { progress = answerQuiz(progress, index) }, modifier = Modifier.fillMaxWidth(), enabled = selected == -1) {
                                Text(if (selected == index) "আপনার উত্তর: $text" else text, lineHeight = 22.sp)
                            }
                        }
                        if (selected >= 0) {
                            Spacer(Modifier.height(8.dp))
                            Text(if (selected == question.correct) "সঠিক উত্তর!" else "সঠিক উত্তর: ${question.answers[question.correct]}", color = Forest, fontWeight = FontWeight.Bold, lineHeight = 23.sp)
                            Spacer(Modifier.height(6.dp))
                            Text(question.explanation, color = Ink, fontSize = 14.sp, lineHeight = 23.sp)
                            Text("সূত্র: ${question.source}", color = Muted, fontSize = 12.sp)
                            TextButton(onClick = { openSource(question.url) }) { Text("মূল আয়াত দেখুন") }
                            Button(onClick = { progress = advanceQuiz(progress) }, modifier = Modifier.fillMaxWidth()) {
                                Text(if (progress.position == QuizQuestions.lastIndex) "ফলাফল দেখুন" else "পরবর্তী প্রশ্ন")
                            }
                        }
                    }
                }
            } else {
                item {
                    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
                        Text("কুইজ সম্পন্ন", color = Forest, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text("সঠিক উত্তর: ${progress.score} / ${QuizQuestions.size}", color = Ink, fontSize = 16.sp)
                        Text("নিচে উত্তর ও সূত্র আবার দেখুন।", color = Muted, fontSize = 13.sp)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { progress = QuizProgress() }) { Text("আবার শুরু করুন") }
                    }
                }
                itemsIndexed(QuizQuestions, key = { _, item -> item.id }) { index, question ->
                    QuizReviewCard(question, progress.answers[index], onSource = { openSource(question.url) })
                }
            }
            item {
                Text("সংক্ষিপ্ত পরিচিতি ও ব্যাখ্যাগুলো প্রকাশের আগে যোগ্য পর্যালোচনা প্রয়োজন। মূল সূত্র খুলতে ইন্টারনেট লাগবে।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                Spacer(Modifier.height(14.dp))
            }
        }
    }
}

@Composable
private fun StoryCard(story: LearningStory, expanded: Boolean, onToggle: () -> Unit, onSource: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(story.category, color = Muted, fontSize = 12.sp)
        Text(story.title, color = Forest, fontSize = 18.sp, fontWeight = FontWeight.Bold, lineHeight = 27.sp)
        Spacer(Modifier.height(4.dp))
        Text("সূত্র: ${story.source}", color = Muted, fontSize = 12.sp)
        if (expanded) {
            Spacer(Modifier.height(12.dp))
            Text(story.summary, color = Ink, fontSize = 14.sp, lineHeight = 24.sp)
            Spacer(Modifier.height(10.dp))
            Text("ভাবনার বিষয়", color = Forest, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(story.reflection, color = Ink, fontSize = 14.sp, lineHeight = 23.sp)
        }
        TextButton(onClick = onToggle) { Text(if (expanded) "সংক্ষিপ্ত করুন" else "পরিচিতি পড়ুন") }
        if (expanded) TextButton(onClick = onSource) { Text("মূল আয়াত দেখুন") }
    }
}

@Composable
private fun QuizReviewCard(question: QuizQuestion, answer: Int, onSource: () -> Unit) {
    Column(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(18.dp)).padding(18.dp)) {
        Text(question.question, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 23.sp)
        Spacer(Modifier.height(8.dp))
        Text("আপনার উত্তর: ${question.answers.getOrNull(answer) ?: "দেওয়া হয়নি"}", color = if (answer == question.correct) Forest else MaterialTheme.colorScheme.error, fontSize = 13.sp, lineHeight = 21.sp)
        Text("সঠিক উত্তর: ${question.answers[question.correct]}", color = Forest, fontSize = 13.sp, lineHeight = 21.sp)
        Spacer(Modifier.height(5.dp))
        Text(question.explanation, color = Ink, fontSize = 13.sp, lineHeight = 22.sp)
        Text("সূত্র: ${question.source}", color = Muted, fontSize = 12.sp)
        TextButton(onClick = onSource) { Text("মূল আয়াত দেখুন") }
    }
}
