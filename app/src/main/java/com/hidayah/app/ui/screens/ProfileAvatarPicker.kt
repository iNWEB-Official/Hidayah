package com.hidayah.app.ui.screens

import androidx.compose.foundation.Canvas as DrawCanvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.data.AvatarStyle
import com.hidayah.app.data.ProfileAvatar
import com.hidayah.app.data.ProfileAvatars
import com.hidayah.app.data.profileAvatar
import com.hidayah.app.ui.theme.CardSurface
import com.hidayah.app.ui.theme.Forest
import com.hidayah.app.ui.theme.Ink
import com.hidayah.app.ui.theme.Muted

/** Selection is stored privately on this device per Firebase user, never in gallery or Firebase. */
@Composable
internal fun ProfileAvatarPicker(uid: String) {
    val context = LocalContext.current
    val preferences = remember(context) { context.getSharedPreferences("profile_avatars", android.content.Context.MODE_PRIVATE) }
    var selectedId by remember(uid) { mutableStateOf(profileAvatar(preferences.getString(uid, null)).id) }
    val selected = profileAvatar(selectedId)
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AvatarArtwork(selected, Modifier.size(88.dp))
        Spacer(Modifier.height(8.dp))
        Text("পছন্দের প্রোফাইল অ্যাভাটার", color = Forest, style = MaterialTheme.typography.titleMedium)
        Text("পছন্দমতো বেছে নিন; এটি কেবল এই ডিভাইসে থাকবে।", color = Muted, fontSize = 12.sp, textAlign = TextAlign.Center)
        Spacer(Modifier.height(10.dp))
        ProfileAvatars.chunked(3).forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { avatar ->
                    val active = selectedId == avatar.id
                    Column(
                        Modifier.weight(1f).padding(vertical = 4.dp).clip(RoundedCornerShape(14.dp))
                            .background(CardSurface).border(if (active) 2.dp else 1.dp, if (active) Forest else Muted.copy(alpha = .3f), RoundedCornerShape(14.dp))
                            .selectable(selected = active, role = Role.RadioButton, onClick = {
                                selectedId = avatar.id
                                preferences.edit().putString(uid, avatar.id).apply()
                            }).padding(vertical = 8.dp, horizontal = 3.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AvatarArtwork(avatar, Modifier.size(52.dp))
                        Spacer(Modifier.height(4.dp))
                        Text(avatar.label, color = Ink, fontSize = 11.sp, lineHeight = 15.sp, textAlign = TextAlign.Center)
                        if (active) Text("নির্বাচিত", color = Forest, fontSize = 10.sp)
                    }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

/** Small built-in vector-style illustrations, drawn locally; no network or bitmap assets. */
@Composable
private fun AvatarArtwork(avatar: ProfileAvatar, modifier: Modifier = Modifier) {
    val style = avatar.style
    val female = style in listOf(AvatarStyle.GIRL, AvatarStyle.WOMAN, AvatarStyle.SENIOR_WOMAN)
    val child = style in listOf(AvatarStyle.BOY, AvatarStyle.GIRL)
    val senior = style in listOf(AvatarStyle.SENIOR_MAN, AvatarStyle.SENIOR_WOMAN)
    val neutral = style == AvatarStyle.NEUTRAL
    DrawCanvas(modifier.clip(CircleShape).background(Color(0xFFE7F0E3))) {
        val w = size.width; val h = size.height
        val deep = Color(0xFF285B43); val cloth = if (female) Color(0xFF789D79) else Color(0xFF426C54)
        if (neutral) {
            drawCircle(Color(0xFFC7DCC9), w * .20f, Offset(w * .5f, h * .36f))
            drawOval(deep, topLeft = Offset(w * .20f, h * .61f), size = Size(w * .60f, h * .53f))
        } else {
            drawOval(cloth, topLeft = Offset(w * .10f, h * .61f), size = Size(w * .80f, h * .55f))
            if (female) drawOval(deep, topLeft = Offset(w * .19f, h * .10f), size = Size(w * .62f, h * .68f))
            val faceSize = if (child) .38f else .34f
            drawOval(Color(0xFFE7BC93), topLeft = Offset(w * (.5f - faceSize / 2), h * .27f), size = Size(w * faceSize, h * .39f))
            if (!female) {
                drawArc(if (senior) Color(0xFFC3C7BB) else Color(0xFF304B3A), 180f, 180f, true,
                    topLeft = Offset(w * .30f, h * .19f), size = Size(w * .40f, h * .24f))
                drawLine(Color(0xFFD5C8A1), Offset(w * .30f, h * .30f), Offset(w * .70f, h * .30f), strokeWidth = w * .025f)
            }
            val eyeY = h * .44f
            drawCircle(deep, w * .018f, Offset(w * .43f, eyeY)); drawCircle(deep, w * .018f, Offset(w * .57f, eyeY))
            drawArc(Color(0xFF9E6755), 15f, 150f, false, Offset(w * .44f, h * .49f), Size(w * .12f, h * .08f), style = androidx.compose.ui.graphics.drawscope.Stroke(w * .012f))
            if (senior) {
                drawLine(Color(0xFFBE8F76), Offset(w * .37f, h * .49f), Offset(w * .43f, h * .50f), w * .009f)
                drawLine(Color(0xFFBE8F76), Offset(w * .57f, h * .50f), Offset(w * .63f, h * .49f), w * .009f)
            }
        }
    }
}
