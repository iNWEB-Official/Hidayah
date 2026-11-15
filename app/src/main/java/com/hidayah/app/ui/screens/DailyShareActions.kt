package com.hidayah.app.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.data.DailyValue
import com.hidayah.app.data.formatDailyValueShare
import com.hidayah.app.ui.HadithSharePreview
import com.hidayah.app.ui.createHadithSharePreview
import com.hidayah.app.ui.shareHadithImage
import com.hidayah.app.ui.saveHadithImage
import com.hidayah.app.ui.theme.Muted
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

@Composable
internal fun DailyShareActions(value: DailyValue) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // A new daily value must not reuse the previous value's preview or error state.
    var busy by remember(value) { mutableStateOf(false) }
    var preview by remember(value) { mutableStateOf<HadithSharePreview?>(null) }
    var error by remember(value) { mutableStateOf<String?>(null) }
    var message by remember(value) { mutableStateOf<String?>(null) }
    var pendingBitmap by remember(value) { mutableStateOf<Bitmap?>(null) }
    val savePicker = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("image/png")) { uri ->
        val bitmap = pendingBitmap
        pendingBitmap = null
        if (uri != null && bitmap != null) {
            busy = true; error = null; message = null
            scope.launch {
                try {
                    saveHadithImage(context, uri, bitmap)
                    preview = null
                    message = "নির্বাচিত স্থানে ছবিটি সংরক্ষণ করা হয়েছে।"
                } catch (e: CancellationException) { throw e }
                catch (_: Exception) { error = "ছবি সংরক্ষণ করা যায়নি। অসম্পূর্ণ ফাইল থাকলে নির্বাচিত স্থানে যাচাই করুন।" }
                finally { busy = false }
            }
        } else if (uri != null) {
            error = "ছবিটি আর পাওয়া যাচ্ছে না। আবার তৈরি করে সংরক্ষণ করুন।"
        } else {
            message = "সংরক্ষণের স্থান নির্বাচন করা হয়নি।"
        }
    }
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        TextButton(onClick = {
            error = null; message = null
            runCatching {
                val send = Intent(Intent.ACTION_SEND).setType("text/plain").putExtra(Intent.EXTRA_TEXT, formatDailyValueShare(value))
                context.startActivity(Intent.createChooser(send, "টেক্সট শেয়ার করুন"))
            }.onFailure { error = "টেক্সট শেয়ার করা যায়নি।" }
        }) { Text("টেক্সট শেয়ার", fontSize = 13.sp) }
        TextButton(enabled = !busy, onClick = {
            error = null; message = null
            busy = true
            scope.launch {
                try { preview = createHadithSharePreview(context, value) }
                catch (e: CancellationException) { throw e }
                catch (e: Exception) { error = e.message ?: "ছবি তৈরি করা যায়নি। টেক্সট হিসেবে শেয়ার করুন।" }
                finally { busy = false }
            }
        }) { Text(if (busy) "ছবি তৈরি হচ্ছে…" else "ছবি শেয়ার", fontSize = 13.sp) }
    }
    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, lineHeight = 19.sp) }
    message?.let { Text(it, color = Muted, fontSize = 12.sp, lineHeight = 19.sp) }
    preview?.let { result ->
        AlertDialog(
            onDismissRequest = { if (!busy) preview = null },
            title = { Text("শেয়ার করার ছবি") },
            text = {
                Column {
                    Image(bitmap = result.bitmap.asImageBitmap(), contentDescription = "হাদিস বা আয়াতের লেখা ও সূত্রসহ শেয়ার ছবি",
                        modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp), contentScale = ContentScale.Fit)
                    Spacer(Modifier.height(8.dp))
                    Text(result.backgroundNote, color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    Text("সংরক্ষণ বাধাগ্রস্ত হলে নির্বাচিত স্থানে অসম্পূর্ণ ফাইল থেকে যেতে পারে।", color = Muted, fontSize = 12.sp, lineHeight = 19.sp)
                    error?.let { Text(it, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, lineHeight = 19.sp) }
                    message?.let { Text(it, color = Muted, fontSize = 12.sp, lineHeight = 19.sp) }
                }
            },
            confirmButton = {
                Row {
                    TextButton(enabled = !busy, onClick = {
                        error = null; message = null; pendingBitmap = result.bitmap
                        try { savePicker.launch("Hidayah-daily.png") }
                        catch (_: Exception) { pendingBitmap = null; error = "সংরক্ষণের স্থান নির্বাচন করা যায়নি।" }
                    }) { Text("সংরক্ষণ") }
                    TextButton(enabled = !busy, onClick = {
                        error = null; busy = true
                        scope.launch {
                            try { shareHadithImage(context, result.bitmap); preview = null }
                            catch (e: CancellationException) { throw e }
                            catch (_: Exception) { error = "ছবি শেয়ার করা যায়নি। আবার চেষ্টা করুন।"; preview = null }
                            finally { busy = false }
                        }
                    }) { Text(if (busy) "প্রস্তুত হচ্ছে…" else "শেয়ার") }
                }
            },
            dismissButton = { TextButton(enabled = !busy, onClick = { preview = null }) { Text("বাতিল") } },
        )
    }
}
