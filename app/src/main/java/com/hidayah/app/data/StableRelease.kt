package com.hidayah.app.data

import java.net.URI
import org.json.JSONObject

/** Strict release contract for a future optional updater; this does not download or install an APK. */
data class StableRelease(val versionName: String, val versionCode: Long, val downloadUrl: String)

fun parseStableRelease(json: JSONObject, installedVersionCode: Long): StableRelease? {
    return try {
    if (json.getBoolean("draft") || json.getBoolean("prerelease") || installedVersionCode <= 0) return null
    val tag = json.getString("tag_name")
    val match = Regex("v(20[0-9]{2})\\.(0|[1-9][0-9]?)\\.(0|[1-9][0-9]?)").matchEntire(tag) ?: return null
    val (year, major, patch) = match.destructured
    val code = year.toLong() * 10000 + major.toLong() * 100 + patch.toLong()
    if (code <= installedVersionCode) return null
    val name = "$year.$major.$patch"
    val filename = "Hidayah-$name.apk"
    val assets = json.getJSONArray("assets")
    val matches = (0 until assets.length()).map { assets.getJSONObject(it) }
        .filter { it.optString("name") == filename && it.optString("state") == "uploaded" }
    if (matches.size != 1) return null
    val url = matches.single().getString("browser_download_url")
    val uri = URI(url)
    if (!uri.scheme.equals("https", ignoreCase = true) || !uri.host.equals("github.com", ignoreCase = true) ||
        uri.userInfo != null || uri.port !in listOf(-1, 443) || uri.rawQuery != null || uri.rawFragment != null ||
        uri.rawPath != "/iNWEB-Official/HidayahAPPS/releases/download/$tag/$filename") return null
    StableRelease(name, code, url)
    } catch (_: Exception) { null }
}
