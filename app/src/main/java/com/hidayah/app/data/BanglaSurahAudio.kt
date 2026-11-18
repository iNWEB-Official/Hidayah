package com.hidayah.app.data

import java.io.IOException
import java.net.Proxy
import java.util.concurrent.TimeUnit
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

/** Curated from the archive metadata, one original MP3 per numbered surah. */
internal const val BANGLA_AUDIO_ITEM = "Quranonlybanglavoice-www.eelm.weebly.com-skype-id-azharmea_201601"
private val banglaFiles = listOf(
    "001. Sura Al Fathiha (MK,A-7).mp3",
    "002. Sura  Al Baqura (MD,A-286).mp3",
    "003. Sura Al Imran (MD,A-200).mp3",
    "004. Sura An Nisa (MD,A-176).mp3",
    "005. Sura Al Ma-eda (MD,A-120).mp3",
    "006. Sura Al Ana'm (MK,A-165).mp3",
    "007. Sura Al A'raf (MK,A-206).mp3",
    "008. Sura Al  Anfal (MD,A-75).mp3",
    "009. Sura At Tow'ba (MD,A-129).mp3",
    "010. Sura Younus (MK,A-109).mp3",
    "011. Sura Hood (MK,A-123).mp3",
    "012. Sura Yousuf (MK,A-111).mp3",
    "013. Sura Ar Raad (MD,A-43).mp3",
    "014. Sura Ebrahim (MK,A-52).mp3",
    "015. Sura Al Hijr (MK,A-99).mp3",
    "016. Sura An Nahl (MK,A-128).mp3",
    "017. Sura Bani Esrail (MK,A-111).mp3",
    "018. Sura Al Khaf (MK,A-110).mp3",
    "019. Sura Maryam (MK,A-98).mp3",
    "020. Sura Ta-Ha (MK,A-135).mp3",
    "021. Sura Al Ambia (MK,A-112).mp3",
    "022.  Sura Al Hajj (MD,A-78).mp3",
    "023.  Sura Al Momenoon (MK,A-118).mp3",
    "024. Sura An Noor (MD,A-64).mp3",
    "025. Sura Al Forqan (MK,A-77).mp3",
    "026. Sura Ash Sho'ara (MK,A-227).mp3",
    "027. Sura An Nmal (MK,A-93).mp3",
    "028. Sura Al Qasas (MK,A-88).mp3",
    "029.  Sura  A l  Ankaboot (MK,A-69).mp3",
    "030.  Sura Ar-Rom (MK,A-60).mp3",
    "031.  Sura Loqman (MK,A-34).mp3",
    "032. Sura As Sazda (MK,A-30).mp3",
    "033.  Sura Al  Ahzab (MD,A-73).mp3",
    "034.  Sura As Saba (MK,A-54).mp3",
    "035.  Sura  Fatar (MK,A-45).mp3",
    "036. Sura Yasin (MK,A-83).mp3",
    "037.  Sura As  Saffat (MK,A-182).mp3",
    "038.  Sura  As  Swad (MKA-88).mp3",
    "039.  Sura  Azzumar (MK,A-75).mp3",
    "040.  Sura Al  Mumin (MK,A-85).mp3",
    "041.  Sura  Ha-Meem As Sajdah (MK,A-54).mp3",
    "042. Sura Ashshura (MK,A-53).mp3",
    "043. Sura Jukhrof (MK,A-89).mp3",
    "044. Sura Ad Dokhan (MK,A-59).mp3",
    "045.  Sura  Al  Jasiah (MK,A-37).mp3",
    "046.  Sura Al Ahqaf (MK,A-35).mp3",
    "047.  Sura  Mohmmed (MD,A-38).mp3",
    "048.  Sura  Al Fath (MD,A-29).mp3",
    "049.  Sura Hujurat (MD,A-18).mp3",
    "050.  Sura  Quaf (MK,A-45).mp3",
    "051.  Sura  Aj Jariat (MK,A-60).mp3",
    "052.  Sura At Toor (MK,A-49).mp3",
    "053. Sura An Najm (MK,A-62).mp3",
    "054.  Sura Al Qamar (MK,A-55).mp3",
    "055. Sura Ar  Rahman (MD,A-78).mp3",
    "056.  Sura Al Waqea (MK,A-96).mp3",
    "057.  Sura Al Hadeed (MD,A-29).mp3",
    "058.  Sura Al Mojadela (MD,A-22).mp3",
    "059.  Sura Al Hashr (MD,A-24).mp3",
    "060.  Sura Al Momtahana (MD,A-13).mp3",
    "061. Sura As  Saf (MD,A-14).mp3",
    "062.  Sura Al Jumua (MD,A-11).mp3",
    "063.  Sura Monafeqoon (MD,A-11).mp3",
    "064.  Sura At Tagabon (MD,A-18).mp3",
    "065. Sura At Talaque (MD,A-12).mp3",
    "066.  Sura At Tahreem (MD,A-12).mp3",
    "067. Sura Al Mulk (MK,A-30).mp3",
    "068.  Sura Al Qalam (MK,A-52).mp3",
    "069.  Sura  Al Haqqah (MK,A-52).mp3",
    "070.  Sura Al Ma'rej (MK,A-44).mp3",
    "071. Sura Nuh (MK,A-28).mp3",
    "072. Sura Al Jin (MK,A-28).mp3",
    "073. Sura Al Muzzammel (MK,A-20).mp3",
    "074. Sura Al Muddasser (MK,A-56).mp3",
    "075. Sura Al Qeamah (MK,A-40).mp3",
    "076. Sura Ad Dahr (MD,A-31).mp3",
    "077. Sura Al Morsalat (MK,A-50).mp3",
    "078. Sura Naba (MK,A-40).mp3",
    "079. Sura An Nazeat (MK,A-46).mp3",
    "080. Sura Abasa (MK,A-42).mp3",
    "081. Sura At Takweer (MK,A-29).mp3",
    "082.  Sura Al Infetar (MK,A-19).mp3",
    "083. Sura Mutaeeffin (MK,A-36).mp3",
    "084. Sura Al Insheqaq (MK,A-25).mp3",
    "085.  Sura Al Buruj (Mk,A-22).mp3",
    "086. Sura At Tareq (MK,A-17).mp3",
    "087. Sura Al A'la (MK,A-19).mp3",
    "088. Sura Al  Ghshiah (MK,A-26).mp3",
    "089. Sura Al Fajar (MK,A-30).mp3",
    "090. Sura Al Balad (MK,A-20).mp3",
    "091. Sura Ash Shams (MK,A-15).mp3",
    "092. Sura Al Lail (MK,A-21).mp3",
    "093. Sura Ad Doha (MK,A-11).mp3",
    "094. Sura Al Insherah (MK,A-8).mp3",
    "095. Sura At Teen (MK,A-8).mp3",
    "096. Sura Al Alaque (MK,A-19).mp3",
    "097. Sura Al  Qadar (MK,A-5).mp3",
    "098. Sura Al Bayenah (MK,A-8).mp3",
    "099. Sura Al Zelzal (MD,A-8).mp3",
    "100. Sura Al  Adiat (MK,A-11).mp3",
    "101. Sura Al  Qaria (MK,A-11).mp3",
    "102. Sura At Takasur (MK,A-8).mp3",
    "103. Sura Al  Asar (MK,A-3).mp3",
    "104. Sura Al  Humazha (MK,A-9).mp3",
    "105. Sura Al  Feel (MK,A-5).mp3",
    "106. Sura  Al  Qurish (MK,A-4).mp3",
    "107.  Sura  Al  Maun (MK,A-7).mp3",
    "108.  Sura  Al  Kausar (MK,A-3).mp3",
    "109.  Sura  Al  Kaferoon (MK,A-6).mp3",
    "110.  Sura  An  Nasr (MD,A-3).mp3",
    "111.  Sura  Al  Lahab (MK,A-5).mp3",
    "112.  Sura  Al  Ikhlas (MK,A-4).mp3",
    "113. Sura Al Falaque (MD,A-5).mp3",
    "114. Sura An Nas (MD,A-6).mp3",
)

internal fun banglaSurahFilename(number: Int): String? = banglaFiles.getOrNull(number - 1)

internal fun banglaSurahUrl(number: Int): HttpUrl? {
    val file = banglaSurahFilename(number) ?: return null
    return HttpUrl.Builder().scheme("https").host("archive.org")
        .addPathSegment("download").addPathSegment(BANGLA_AUDIO_ITEM).addPathSegment(file).build()
}

/** Validates every request and redirect against the same selected file. */
internal fun isAllowedBanglaAudioUrl(url: HttpUrl, number: Int): Boolean {
    val filename = banglaSurahFilename(number) ?: return false
    if (url.scheme != "https" || url.port != 443 || url.username.isNotEmpty() || url.password.isNotEmpty() ||
        url.query != null || url.fragment != null) return false
    val path = url.pathSegments
    return if (url.host == "archive.org") {
        path == listOf("download", BANGLA_AUDIO_ITEM, filename)
    } else {
        url.host.matches(Regex("(?:dn|ia)[0-9]+\\.(?:us|ca|eu)\\.archive\\.org")) &&
            path == listOf("0", "items", BANGLA_AUDIO_ITEM, filename)
    }
}

/** Resolve redirects manually so every target is checked before DNS or connection. */
internal fun createBanglaAudioClient(number: Int): OkHttpClient = OkHttpClient.Builder()
    .proxy(Proxy.NO_PROXY).dns(PublicImageDns)
    .followRedirects(false).followSslRedirects(false)
    .connectTimeout(12, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        var request = chain.request()
        for (attempt in 0..3) {
            if (request.method !in setOf("GET", "HEAD") || !isAllowedBanglaAudioUrl(request.url, number))
                throw IOException("Unexpected Bengali audio destination")
            val response = chain.proceed(request)
            if (response.code !in setOf(301, 302, 303, 307, 308)) return@addInterceptor response
            val target = response.header("Location")?.let { request.url.resolve(it) }
            response.close()
            if (target == null || !isAllowedBanglaAudioUrl(target, number)) throw IOException("Unexpected Bengali audio redirect")
            request = request.newBuilder().url(target).removeHeader("Authorization").removeHeader("Cookie").build()
        }
        throw IOException("Too many Bengali audio redirects")
    }.build()
