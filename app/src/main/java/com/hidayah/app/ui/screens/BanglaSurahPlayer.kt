package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.hidayah.app.data.banglaSurahUrl
import com.hidayah.app.data.createBanglaAudioClient
import com.hidayah.app.ui.theme.*

/** Separate from the Arabic player; one recording represents the entire selected surah. */
@Stable
internal class BanglaSurahState(private val player: ExoPlayer) {
    var playing by mutableStateOf(false)
        private set
    var wantsPlayback by mutableStateOf(false)
        private set
    var buffering by mutableStateOf(false)
        private set
    var ended by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    val canPause: Boolean get() = wantsPlayback && !ended && error == null

    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) { wantsPlayback = playWhenReady }
        override fun onPlaybackStateChanged(playbackState: Int) {
            buffering = playbackState == Player.STATE_BUFFERING
            ended = playbackState == Player.STATE_ENDED
        }
        override fun onPlayerError(error: PlaybackException) { this@BanglaSurahState.error = "বাংলা অডিও পাওয়া যায়নি। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।" }
    }
    fun toggle() {
        if (canPause) { player.pause(); return }
        if (player.playbackState == Player.STATE_ENDED) player.seekTo(0)
        if (player.playbackState == Player.STATE_IDLE || error != null) {
            error = null
            player.prepare()
        }
        player.play()
    }
    fun pause() { player.pause() }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
internal fun rememberBanglaSurahPlayer(number: Int): BanglaSurahState {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val player = remember(number) {
        val url = requireNotNull(banglaSurahUrl(number))
        val media = DefaultMediaSourceFactory(OkHttpDataSource.Factory(createBanglaAudioClient(number)))
        ExoPlayer.Builder(context.applicationContext).setMediaSourceFactory(media).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_SPEECH).build(), true)
            setHandleAudioBecomingNoisy(true)
            setMediaItem(MediaItem.fromUri(url.toString()))
        }
    }
    val state = remember(player) { BanglaSurahState(player) }
    DisposableEffect(player, state) {
        player.addListener(state.listener)
        onDispose { player.removeListener(state.listener); player.release() }
    }
    DisposableEffect(player, owner, state) {
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_PAUSE) state.pause() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer) }
    }
    return state
}

@Composable
internal fun BanglaSurahControls(player: BanglaSurahState, onBeforePlay: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Sage, RoundedCornerShape(16.dp)).padding(12.dp)) {
        Text("বাংলা অনুবাদ · Internet Archive থেকে অনলাইনে", color = Forest, fontSize = 13.sp)
        Text("অডিও অনুবাদ প্রদর্শিত বাংলা পাঠ থেকে ভিন্ন হতে পারে। প্রথম ফাইলে ভূমিকা আছে।", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        if (player.canPause) OutlinedButton(onClick = player::toggle) { Text("বাংলা অডিও বিরতি") }
        else Button(onClick = { onBeforePlay(); player.toggle() }) { Text(if (player.error != null) "বাংলা অডিও আবার চেষ্টা করুন" else "▶ পুরো সূরা বাংলায় শুনুন") }
        if (player.canPause && !player.playing) Text(if (player.buffering) "বাংলা অডিও লোড হচ্ছে…" else "বাংলা অডিও অপেক্ষা করছে।", color = Muted, fontSize = 12.sp)
        player.error?.let { Text(it, color = androidx.compose.ui.graphics.Color(0xFF9D3E30), fontSize = 12.sp) }
    }
}
