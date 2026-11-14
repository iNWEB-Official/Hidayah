package com.hidayah.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Online-only Quran data. Sources are explicitly identified in the reader. */
class CloudQuranRepository : QuranRepository {
    override suspend fun getSurahs(): List<Surah> = withContext(Dispatchers.IO) {
        val data = request("https://api.alquran.cloud/v1/surah").getJSONArray("data")
        val list = (0 until data.length()).map { parseSurah(data.getJSONObject(it)) }
        check(list.size == 114 && list.map { it.number } == (1..114).toList()) { "Surah list is incomplete" }
        list
    }

    override suspend fun getChapter(number: Int): QuranChapter = withContext(Dispatchers.IO) {
        require(number in 1..114)
        val root = request("https://api.alquran.cloud/v1/surah/$number/editions/quran-uthmani,bn.bengali,ar.alafasy")
        val editions = root.getJSONArray("data")
        val byEdition = (0 until editions.length()).associate { index ->
            val edition = editions.getJSONObject(index)
            edition.getJSONObject("edition").getString("identifier") to edition
        }
        val arabic = requireNotNull(byEdition["quran-uthmani"])
        val bangla = requireNotNull(byEdition["bn.bengali"])
        val audio = requireNotNull(byEdition["ar.alafasy"])
        val surah = parseSurah(arabic)
        check(surah.number == number)
        fun ayahs(edition: JSONObject): Map<Int, JSONObject> {
            val array = edition.getJSONArray("ayahs")
            return (0 until array.length()).map { array.getJSONObject(it) }.associateBy { it.getInt("numberInSurah") }
        }
        val ar = ayahs(arabic); val bn = ayahs(bangla); val recordings = ayahs(audio)
        check(ar.size == surah.verses && bn.size == surah.verses && recordings.size == surah.verses) { "Incomplete chapter" }
        val verses = (1..surah.verses).map { index ->
            val uri = requireNotNull(recordings[index]).getString("audio")
            check(uri.startsWith("https://")) { "Untrusted audio URL" }
            QuranVerse(index, requireNotNull(ar[index]).getString("text"), requireNotNull(bn[index]).getString("text"), uri)
        }
        QuranChapter(surah, verses)
    }

    override suspend fun getVerseText(reference: VerseReference): VerseText = withContext(Dispatchers.IO) {
        require(reference.chapter in 1..114 && reference.verse in 1..286)
        val data = request("https://api.alquran.cloud/v1/ayah/${reference.chapter}:${reference.verse}/editions/quran-uthmani,bn.bengali").getJSONArray("data")
        val editions = (0 until data.length()).associate { index ->
            val entry = data.getJSONObject(index)
            entry.getJSONObject("edition").getString("identifier") to entry
        }
        fun verifiedText(id: String): String {
            val entry = requireNotNull(editions[id])
            check(entry.getJSONObject("surah").getInt("number") == reference.chapter && entry.getInt("numberInSurah") == reference.verse) { "Verse mismatch" }
            return entry.getString("text").trim().also { check(it.isNotEmpty()) }
        }
        VerseText(reference, verifiedText("quran-uthmani"), verifiedText("bn.bengali"))
    }

    override suspend fun getTafsir(surah: Int, verse: Int): Tafsir = withContext(Dispatchers.IO) {
        require(surah in 1..114 && verse in 1..286)
        val obj = request("https://api.quran.com/api/v4/tafsirs/165/by_ayah/$surah:$verse").getJSONObject("tafsir")
        check(obj.getInt("resource_id") == 165 && obj.getInt("language_id") == 20)
        val keys = obj.optJSONObject("verses")?.keys()?.asSequence()?.toList().orEmpty()
        Tafsir(obj.getString("text"), obj.optString("resource_name", "Tafsir Ahsanul Bayaan"), keys)
    }

    private fun parseSurah(o: JSONObject) = Surah(
        o.getInt("number"), o.getString("englishName"), o.getString("englishNameTranslation"),
        o.getInt("numberOfAyahs"), o.getString("name"),
    )

    /** All endpoint URLs are fixed HTTPS URLs; do not follow redirects to untrusted schemes. */
    private fun request(url: String): JSONObject {
        val conn = URL(url).openConnection() as HttpURLConnection
        conn.connectTimeout = 15_000; conn.readTimeout = 30_000
        conn.instanceFollowRedirects = false
        conn.setRequestProperty("Accept", "application/json")
        return try {
            val status = conn.responseCode
            check(status in 200..299) { "Server returned HTTP $status" }
            val body = conn.inputStream.bufferedReader(Charsets.UTF_8).use { reader ->
                val out = StringBuilder(); val buffer = CharArray(8192)
                while (true) {
                    val n = reader.read(buffer); if (n < 0) break
                    out.append(buffer, 0, n)
                    check(out.length <= 8_000_000) { "Response too large" }
                }
                out.toString()
            }
            JSONObject(body)
        } finally { conn.disconnect() }
    }
}
