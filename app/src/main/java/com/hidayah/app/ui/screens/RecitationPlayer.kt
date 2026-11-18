package com.hidayah.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.hidayah.app.data.QuranChapter
import com.hidayah.app.data.createQuranAudioClient
import com.hidayah.app.ui.RecitationPhase
import com.hidayah.app.ui.RecitationToggleAction
import com.hidayah.app.ui.recitationQueue
import com.hidayah.app.ui.recitationToggleAction
import com.hidayah.app.ui.theme.*

@Stable
class RecitationState(private val player: ExoPlayer, private val chapter: QuranChapter) {
    var playing by mutableStateOf(false)
        private set
    var wantsPlayback by mutableStateOf(false)
        private set
    var buffering by mutableStateOf(false)
        private set
    var ended by mutableStateOf(false)
        private set
    val canPause: Boolean get() = wantsPlayback && !ended && error == null
    var index by mutableIntStateOf(0)
        private set
    var error by mutableStateOf<String?>(null)
        private set
    private var queueStart = 0

    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) { wantsPlayback = playWhenReady }
        override fun onPlaybackStateChanged(playbackState: Int) {
            buffering = playbackState == Player.STATE_BUFFERING
            ended = playbackState == Player.STATE_ENDED
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            if (mediaItem != null) index = queueStart + player.currentMediaItemIndex.coerceAtLeast(0)
        }
        override fun onPlayerError(error: PlaybackException) { this@RecitationState.error = "অডিও পাওয়া যায়নি। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।" }
    }
    private fun playQueue(start: Int, single: Boolean) {
        val queue = recitationQueue(chapter.verses.size, start, single) ?: return
        error = null
        queueStart = queue.start
        index = queue.start
        val items = chapter.verses.subList(queue.start, queue.endExclusive).map { MediaItem.fromUri(requireNotNull(it.audioUrl)) }
        player.setMediaItems(items)
        player.prepare()
        player.play()
    }
    fun playFrom(index: Int) = playQueue(index, single = false)
    fun playSingle(index: Int) = playQueue(index, single = true)
    fun playWholeSurah() = playFrom(0)
    fun toggle() {
        val phase = when (player.playbackState) {
            Player.STATE_IDLE -> RecitationPhase.IDLE
            Player.STATE_ENDED -> RecitationPhase.ENDED
            else -> RecitationPhase.OTHER
        }
        when (recitationToggleAction(error != null, player.playWhenReady, phase)) {
            RecitationToggleAction.PAUSE -> player.pause()
            RecitationToggleAction.RESUME -> player.play()
            RecitationToggleAction.PREPARE_AND_PLAY -> { error = null; player.prepare(); player.play() }
            RecitationToggleAction.RESTART_AND_PLAY -> { error = null; player.seekTo(0, 0L); player.play() }
        }
    }
    fun pause() { player.pause() }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun rememberRecitationPlayer(chapter: QuranChapter): RecitationState {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val player = remember(chapter) {
        val mediaSource = DefaultMediaSourceFactory(OkHttpDataSource.Factory(createQuranAudioClient()))
        ExoPlayer.Builder(context.applicationContext).setMediaSourceFactory(mediaSource).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_SPEECH).build(), true)
            setHandleAudioBecomingNoisy(true)
        }
    }
    val state = remember(player, chapter) { RecitationState(player, chapter) }
    DisposableEffect(player, state) {
        player.setMediaItems(chapter.verses.map { MediaItem.fromUri(requireNotNull(it.audioUrl)) })
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
fun RecitationControls(player: RecitationState, onBeforePlay: () -> Unit = {}) {
    Column(Modifier.fillMaxWidth().padding(vertical = 8.dp).background(Sage, RoundedCornerShape(16.dp)).padding(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("মিশারী আল-আফাসী", color = Forest, fontSize = 14.sp)
                Text("আয়াত ${banglaDigits((player.index + 1).toString())} · অনলাইনে", color = Muted, fontSize = 12.sp)
            }
            if (player.canPause) OutlinedButton(onClick = player::toggle) { Text("বিরতি") }
            else Button(onClick = { onBeforePlay(); player.toggle() }) { Text(if (player.error != null) "আবার চেষ্টা করুন" else "শুনুন") }
        }
        if (player.canPause && !player.playing) {
            Text(if (player.buffering) "অডিও লোড হচ্ছে…" else "অডিও সাময়িকভাবে অপেক্ষা করছে।", color = Muted, fontSize = 12.sp)
        }
        androidx.compose.material3.TextButton(onClick = { onBeforePlay(); player.playWholeSurah() }) { Text("▶ পুরো সূরা আরবিতে শুনুন") }
        player.error?.let { Text(it, color = androidx.compose.ui.graphics.Color(0xFF9D3E30), fontSize = 12.sp) }
    }
}
