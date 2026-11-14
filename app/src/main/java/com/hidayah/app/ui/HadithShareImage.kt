package com.hidayah.app.ui

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.hidayah.app.R
import com.hidayah.app.data.DailyValue
import com.hidayah.app.data.safeShareBackgroundUrl
import com.hidayah.app.data.selectHadithBackground
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Rendering uses only the exact published text. AI never creates or rewrites quotations. */
data class HadithSharePreview(val bitmap: Bitmap, val backgroundNote: String)

private val localBackgrounds = mapOf(
    "patience_a" to R.drawable.hadith_patience_a, "patience_b" to R.drawable.hadith_patience_b,
    "compassion_a" to R.drawable.hadith_compassion_a, "compassion_b" to R.drawable.hadith_compassion_b,
    "charity_a" to R.drawable.hadith_charity_a, "charity_b" to R.drawable.hadith_charity_b,
    "prayer_a" to R.drawable.hadith_prayer_a, "prayer_b" to R.drawable.hadith_prayer_b,
    "honesty_a" to R.drawable.hadith_honesty_a, "honesty_b" to R.drawable.hadith_honesty_b,
    "knowledge_a" to R.drawable.hadith_knowledge_a, "knowledge_b" to R.drawable.hadith_knowledge_b,
    "family_a" to R.drawable.hadith_family_a, "family_b" to R.drawable.hadith_family_b,
    "gratitude_a" to R.drawable.hadith_gratitude_a, "gratitude_b" to R.drawable.hadith_gratitude_b,
    "neighbor_a" to R.drawable.hadith_neighbor_a, "neighbor_b" to R.drawable.hadith_neighbor_b,
    "general_a" to R.drawable.hadith_general_a, "general_b" to R.drawable.hadith_general_b,
)
private const val MAX_DOWNLOAD = 5 * 1024 * 1024
private const val MAX_IMAGE_AREA = 100_000_000L
private const val OUTPUT_WIDTH = 1080
private const val MAX_OUTPUT_HEIGHT = 6000

/** HTTPS only, no redirects, bounded bytes, bounded decoded dimensions and short timeouts. */
private fun fetchBackground(raw: String?): Bitmap? {
    val safeUrl = safeShareBackgroundUrl(raw) ?: return null
    val connection = (URL(safeUrl).openConnection() as HttpsURLConnection).apply {
        connectTimeout = 8_000; readTimeout = 8_000; instanceFollowRedirects = false
        setRequestProperty("Accept", "image/jpeg, image/png, image/webp")
    }
    try {
        if (connection.responseCode != 200 || connection.contentLengthLong > MAX_DOWNLOAD) return null
        val mime = connection.contentType?.substringBefore(';')?.lowercase() ?: return null
        if (mime !in setOf("image/jpeg", "image/png", "image/webp")) return null
        val bytes = ByteArrayOutputStream().use { output ->
            connection.inputStream.use { input ->
                val buffer = ByteArray(8192)
                var total = 0
                while (true) {
                    val n = input.read(buffer)
                    if (n < 0) break
                    total += n
                    if (total > MAX_DOWNLOAD) return null
                    output.write(buffer, 0, n)
                }
            }
            output.toByteArray()
        }
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, bounds)
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0 || bounds.outWidth.toLong() * bounds.outHeight > MAX_IMAGE_AREA) return null
        var sample = 1
        while (bounds.outWidth / sample > 2000 || bounds.outHeight / sample > 3000) sample *= 2
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = sample })
    } finally { connection.disconnect() }
}

private fun layoutText(text: String, typeface: Typeface, size: Float, color: Int, arabic: Boolean = false): StaticLayout {
    val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { this.typeface = typeface; textSize = size; this.color = color }
    return StaticLayout.Builder.obtain(text, 0, text.length, paint, 840)
        .setAlignment(Layout.Alignment.ALIGN_CENTER)
        .setTextDirection(if (arabic) TextDirectionHeuristics.FIRSTSTRONG_RTL else TextDirectionHeuristics.FIRSTSTRONG_LTR)
        .setIncludePad(true).setLineSpacing(8f, 1.12f).build()
}

private fun renderCard(context: Context, value: DailyValue, background: Bitmap): Bitmap {
    require(value.title.length + value.referenceText.length + value.reference.length + (value.referenceArabic?.length ?: 0) <= 4000) {
        "লেখাটি ছবির জন্য খুব দীর্ঘ। সম্পূর্ণ লেখা টেক্সট হিসেবে শেয়ার করুন।"
    }
    val bn = ResourcesCompat.getFont(context, R.font.noto_bengali) ?: Typeface.DEFAULT
    val ar = ResourcesCompat.getFont(context, R.font.noto_arabic) ?: Typeface.DEFAULT
    val green = Color.rgb(8, 62, 37)
    val black = Color.rgb(29, 42, 33)
    val blocks = mutableListOf<StaticLayout>()
    blocks += layoutText(if (value.referenceType == "Hadith") "দৈনিক হাদিস" else "দৈনিক আয়াত", bn, 48f, green)
    blocks += layoutText(value.title, bn, 52f, green)
    value.referenceArabic?.takeIf { it.isNotBlank() && it != value.referenceText }?.let {
        blocks += layoutText(it, ar, 56f, black, arabic = true)
    }
    blocks += layoutText(value.referenceText, bn, 46f, black)
    blocks += layoutText("সূত্র: ${value.reference}", bn, 37f, green)
    val contentHeight = blocks.sumOf { it.height } + (blocks.size - 1) * 26
    val panelHeight = contentHeight + 128
    val outputHeight = maxOf(1350, panelHeight + 250)
    require(outputHeight <= MAX_OUTPUT_HEIGHT) { "লেখাটি ছবির জন্য খুব দীর্ঘ। সম্পূর্ণ লেখা টেক্সট হিসেবে শেয়ার করুন।" }
    val output = Bitmap.createBitmap(OUTPUT_WIDTH, outputHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val bgAspect = background.width.toFloat() / background.height
    val outAspect = OUTPUT_WIDTH.toFloat() / outputHeight
    val src = if (bgAspect > outAspect) {
        val width = (background.height * outAspect).toInt().coerceAtLeast(1)
        val x = (background.width - width) / 2
        Rect(x, 0, x + width, background.height)
    } else {
        val height = (background.width / outAspect).toInt().coerceAtMost(background.height).coerceAtLeast(1)
        val y = (background.height - height) / 2
        Rect(0, y, background.width, y + height)
    }
    canvas.drawBitmap(background, src, Rect(0, 0, OUTPUT_WIDTH, outputHeight), Paint(Paint.FILTER_BITMAP_FLAG))
    canvas.drawColor(Color.argb(105, 0, 35, 20))
    val panelTop = (outputHeight - panelHeight) / 2f
    val panel = RectF(70f, panelTop, 1010f, panelTop + panelHeight)
    canvas.drawRoundRect(panel, 40f, 40f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.argb(245, 255, 253, 242) })
    canvas.drawRoundRect(panel, 40f, 40f, Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(211, 179, 93); style = Paint.Style.STROKE; strokeWidth = 3f })
    var y = panelTop + 64f
    blocks.forEach { block ->
        canvas.save(); canvas.translate(120f, y); block.draw(canvas); canvas.restore()
        y += block.height + 26f
    }
    return output
}

suspend fun createHadithSharePreview(context: Context, value: DailyValue): HadithSharePreview = withContext(Dispatchers.IO) {
    val selection = selectHadithBackground(value)
    val requestedRemote = !value.shareBackgroundUrl.isNullOrBlank()
    val remote = if (requestedRemote) runCatching { fetchBackground(value.shareBackgroundUrl) }.getOrNull() else null
    val local = localBackgrounds[selection.id] ?: R.drawable.hadith_general_a
    val background = remote ?: BitmapFactory.decodeResource(context.resources, local)
        ?: error("ব্যাকগ্রাউন্ড ছবি পাওয়া যায়নি।")
    try {
        val note = when {
            remote != null -> "অ্যাডমিনের নির্ধারিত ব্যাকগ্রাউন্ড"
            requestedRemote -> "নতুন ছবি পাওয়া যায়নি; ডিফল্ট ছবি ব্যবহার হয়েছে।"
            selection.selectedByAdmin -> "অ্যাডমিনের নির্বাচিত ডিফল্ট ব্যাকগ্রাউন্ড"
            else -> "শব্দ মিলিয়ে নির্বাচিত ডিফল্ট ব্যাকগ্রাউন্ড"
        }
        HadithSharePreview(renderCard(context, value, background), note)
    } finally { background.recycle() }
}

suspend fun shareHadithImage(context: Context, bitmap: Bitmap) {
    val file = withContext(Dispatchers.IO) {
        val directory = File(context.cacheDir, "daily-share")
        check(directory.exists() || directory.mkdirs()) { "ছবি সংরক্ষণের জায়গা পাওয়া যায়নি।" }
        val old = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        directory.listFiles()?.filter { it.isFile && it.lastModified() < old }?.forEach { it.delete() }
        val dest = File.createTempFile("hidayah-", ".png", directory)
        try { dest.outputStream().use { check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)) }; dest }
        catch (e: Exception) { dest.delete(); throw e }
    }
    val uri = FileProvider.getUriForFile(context, "${context.packageName}.sharefiles", file)
    val send = Intent(Intent.ACTION_SEND).setType("image/png")
        .putExtra(Intent.EXTRA_STREAM, uri).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        .apply { clipData = ClipData.newUri(context.contentResolver, "হিদায়া দৈনিক শেয়ার", uri) }
    context.startActivity(Intent.createChooser(send, "ছবি শেয়ার করুন"))
}
