package com.hidayah.app

import com.hidayah.app.data.parseStableRelease
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class StableReleaseTest {
    private fun release(tag: String = "v2026.0.2", url: String = "https://github.com/iNWEB-Official/Hidayah/releases/download/v2026.0.2/Hidayah-2026.0.2.apk") =
        JSONObject().put("tag_name", tag).put("draft", false).put("prerelease", false)
            .put("assets", JSONArray().put(JSONObject().put("name", "Hidayah-2026.0.2.apk").put("state", "uploaded").put("browser_download_url", url)))

    @Test fun acceptsOnlyNewerValidStableRelease() {
        val result = parseStableRelease(release(), 20260001)!!
        assertEquals("2026.0.2", result.versionName)
        assertEquals(20260002L, result.versionCode)
        assertNull(parseStableRelease(release(), 20260002))
        assertNull(parseStableRelease(release(), 20260003))
    }
    @Test fun rejectsPreReleaseDraftAndMalformedTags() {
        assertNull(parseStableRelease(release().put("prerelease", true), 20260001))
        assertNull(parseStableRelease(release().put("draft", true), 20260001))
        listOf("2026.0.2", "v2026.00.2", "v2026.100.2", "v2026.0.2-beta").forEach {
            assertNull(parseStableRelease(release(tag = it), 20260001))
        }
    }
    @Test fun rejectsUnexpectedDownloadsAndMissingOrDuplicateAssets() {
        listOf("http://github.com/iNWEB-Official/Hidayah/releases/download/v2026.0.2/Hidayah-2026.0.2.apk",
            "https://github.com.evil.example/iNWEB-Official/Hidayah/releases/download/v2026.0.2/Hidayah-2026.0.2.apk",
            "https://github.com/other/Hidayah/releases/download/v2026.0.2/Hidayah-2026.0.2.apk",
            "https://github.com/iNWEB-Official/Hidayah/releases/download/v2026.0.2/Hidayah-2026.0.2.apk?x=1").forEach {
            assertNull(parseStableRelease(release(url = it), 20260001))
        }
        assertNull(parseStableRelease(release().put("assets", JSONArray()), 20260001))
        val duplicate = release(); duplicate.getJSONArray("assets").put(duplicate.getJSONArray("assets").getJSONObject(0))
        assertNull(parseStableRelease(duplicate, 20260001))
    }
    @Test fun malformedMetadataNeverBecomesAnUpdate() {
        assertNull(parseStableRelease(JSONObject(), 20260001))
        assertNull(parseStableRelease(release(), 0))
    }
}
