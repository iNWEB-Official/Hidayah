package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.data.AppSearchResult
import com.hidayah.app.data.searchApp
import com.hidayah.app.ui.theme.*

@Composable
fun AppSearchScreen(onBack: () -> Unit, onSelect: (AppSearchResult) -> Unit) {
    var query by rememberSaveable { mutableStateOf("") }
    val results = remember(query) { searchApp(query) }
    Column(Modifier.fillMaxSize().background(Canvas)) {
        FeatureHeader("সার্চ", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            item {
                Text("বিভাগ ও বিষয় খুঁজুন", color = Forest, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(Modifier.height(5.dp))
                Text("বিভাগ, মূল্যবোধ, দোয়া ও গল্পের অফলাইন তালিকায় খোঁজা হয়; সম্পূর্ণ কুরআন বা আয়াতের পূর্ণাঙ্গ সার্চ নয়।", color = Muted, fontSize = 12.sp, lineHeight = 20.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = query, onValueChange = { query = it }, modifier = Modifier.fillMaxWidth(), singleLine = true, label = { Text("যা খুঁজছেন লিখুন") })
                Spacer(Modifier.height(8.dp))
                Text(if (query.isBlank()) "বিভাগসমূহ" else "ফলাফল: ${banglaDigits(results.size.toString())}টি", color = Muted, fontSize = 13.sp)
            }
            if (results.isEmpty()) item { Text("কোনো ফল পাওয়া যায়নি। অন্য শব্দ দিয়ে চেষ্টা করুন।", color = Muted, fontSize = 14.sp) }
            items(results, key = { it.id }) { result ->
                Row(Modifier.fillMaxWidth().background(CardSurface, RoundedCornerShape(16.dp)).clickable(role = Role.Button) { onSelect(result) }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(result.title, color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, lineHeight = 23.sp)
                        Text(result.subtitle, color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    }
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null, tint = Forest)
                }
            }
        }
    }
}
