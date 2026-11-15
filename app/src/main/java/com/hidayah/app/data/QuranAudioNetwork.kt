package com.hidayah.app.data

import java.io.IOException
import java.net.Proxy
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient

/** Direct TLS to the expected public CDN only. Redirects are rejected rather than followed. */
internal fun createQuranAudioClient(): OkHttpClient = OkHttpClient.Builder()
    .proxy(Proxy.NO_PROXY)
    .dns(PublicImageDns)
    .followRedirects(false)
    .followSslRedirects(false)
    .connectTimeout(12, TimeUnit.SECONDS)
    .readTimeout(20, TimeUnit.SECONDS)
    .addInterceptor { chain ->
        if (!isAllowedRecitationRequest(chain.request().url.toString())) throw IOException("Unexpected audio destination")
        chain.proceed(chain.request())
    }
    .build()
