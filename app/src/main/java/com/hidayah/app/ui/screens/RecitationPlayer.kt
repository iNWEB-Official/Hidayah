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
import com.hidayah.app.data.QuranChapter
import com.hidayah.app.ui.theme.*

@Stable
class RecitationState(private val player: ExoPlayer) {
    var playing by mutableStateOf(false)
        private set
    var index by mutableIntStateOf(0)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) { playing = isPlaying }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) { index = player.currentMediaItemIndex.coerceAtLeast(0) }
        override fun onPlayerError(error: PlaybackException) { this@RecitationState.error = "অডিও পাওয়া যায়নি। ইন্টারনেট পরীক্ষা করে আবার চেষ্টা করুন।" }
    }
    fun playFrom(index: Int) {
        error = null
        player.seekTo(index, 0L)
        if (player.playbackState == Player.STATE_IDLE) player.prepare()
        player.play()
    }
    fun toggle() {
        if (player.isPlaying) player.pause() else {
            error = null
            if (player.playbackState == Player.STATE_ENDED) player.seekTo(0, 0L)
            if (player.playbackState == Player.STATE_IDLE) player.prepare()
            player.play()
        }
    }
    fun pause() { player.pause() }
}

@Composable
fun rememberRecitationPlayer(chapter: QuranChapter): RecitationState {
    val context = LocalContext.current
    val owner = LocalLifecycleOwner.current
    val player = remember(chapter.surah.number) {
        ExoPlayer.Builder(context.applicationContext).build().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_SPEECH).build(), true)
            setHandleAudioBecomingNoisy(true)
        }
    }
    val state = remember(player) { RecitationState(player) }
    DisposableEffect(player, owner, chapter) {
        player.setMediaItems(chapter.verses.map { MediaItem.fromUri(it.audioUrl) })
        player.addListener(state.listener)
        val observer = LifecycleEventObserver { _, event -> if (event == Lifecycle.Event.ON_PAUSE) state.pause() }
        owner.lifecycle.addObserver(observer)
        onDispose { owner.lifecycle.removeObserver(observer); player.removeListener(state.listener); player.release() }
    }
    return state
}

@Composable
fun RecitationControls(player: RecitationState) {
    Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).background(Sage, RoundedCornerShape(16.dp)).padding(14.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("মিশারী আল-আফাসী", color = Forest, fontSize = 14.sp)
                Text("আয়াত ${banglaDigits((player.index + 1).toString())} · অনলাইনে", color = Muted, fontSize = 12.sp)
            }
            if (player.playing) OutlinedButton(onClick = player::toggle) { Text("বিরতি") }
            else Button(onClick = player::toggle) { Text("শুনুন") }
        }
        player.error?.let { Text(it, color = androidx.compose.ui.graphics.Color(0xFF9D3E30), fontSize = 12.sp) }
    }
}
