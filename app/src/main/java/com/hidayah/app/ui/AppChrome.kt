package com.hidayah.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hidayah.app.ui.theme.*

private fun topIcon(name: String, path: String): ImageVector = ImageVector.Builder(name, 24.dp, 24.dp, 24f, 24f).apply {
    addPath(pathData = PathParser().parsePathString(path).toNodes(), fill = SolidColor(Color.Black))
}.build()
private val MoonIcon = topIcon("DarkMode", "M12 3a9 9 0 1 0 9 9c0-.36-.02-.71-.06-1.06A7 7 0 0 1 13.06 3.06C12.71 3.02 12.36 3 12 3z")
private val SunIcon = topIcon("LightMode", "M12 8a4 4 0 1 0 0 8 4 4 0 0 0 0-8z M11 1h2v4h-2z M11 19h2v4h-2z M1 11h4v2H1z M19 11h4v2h-4z M4.22 5.64l1.42-1.42 2.83 2.83-1.42 1.42z M15.53 16.95l1.42-1.42 2.83 2.83-1.42 1.42z M15.53 7.05l2.83-2.83 1.42 1.42-2.83 2.83z M4.22 18.36l2.83-2.83 1.42 1.42-2.83 2.83z")

data class DrawerLink(val route: String, val label: String)
val DrawerLinks = listOf(
    DrawerLink("home", "হোম"), DrawerLink("quran", "আল-কুরআন"), DrawerLink("prayer", "নামাজ ও রমজান"),
    DrawerLink("tracker", "আমল ট্র্যাকার"), DrawerLink("values", "বিষয়ভিত্তিক মূল্যবোধ"), DrawerLink("duas", "দোয়া ও জিকির"),
    DrawerLink("learning", "শিক্ষণীয় গল্প ও কুইজ"), DrawerLink("settings", "সেটিংস"),
)

@Composable
fun HidayahTopBar(dark: Boolean, onMenu: () -> Unit, onToggleDark: () -> Unit, onNotifications: () -> Unit, onSearch: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(DeepForest).windowInsetsPadding(WindowInsets.statusBars).heightIn(min = 56.dp).padding(horizontal = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onMenu) { Icon(Icons.Default.Menu, contentDescription = "মেনু খুলুন", tint = Color.White) }
        Text("হিদায়া", modifier = Modifier.weight(1f), color = Color.White, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold)
        IconButton(onClick = onToggleDark) { Icon(if (dark) SunIcon else MoonIcon, contentDescription = if (dark) "লাইট মোড চালু করুন" else "ডার্ক মোড চালু করুন", tint = Color.White) }
        IconButton(onClick = onNotifications) { Icon(Icons.Default.Notifications, contentDescription = "নোটিফিকেশন ও স্মরণবার্তা", tint = Color.White) }
        IconButton(onClick = onSearch) { Icon(Icons.Default.Search, contentDescription = "সার্চ করুন", tint = Color.White) }
    }
}

@Composable
fun HidayahDrawer(currentRoute: String?, onChoose: (String) -> Unit) {
    ModalDrawerSheet(drawerContainerColor = CardSurface, modifier = Modifier.widthIn(max = 320.dp)) {
        Column(Modifier.fillMaxHeight().verticalScroll(rememberScrollState()).padding(horizontal = 12.dp)) {
            Spacer(Modifier.height(24.dp))
            Text("হিদায়া", color = Forest, fontWeight = FontWeight.Bold, fontSize = 26.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Text("সুন্দর পথে, সুন্দর জীবনে", color = Muted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = Line)
            Spacer(Modifier.height(8.dp))
            DrawerLinks.forEach { link ->
                val selected = currentRoute == link.route || currentRoute?.startsWith("${link.route}?") == true || (link.route == "values" && currentRoute == "values/{topic}") || (link.route == "quran" && currentRoute == "quran/{number}")
                NavigationDrawerItem(label = { Text(link.label, fontSize = 14.sp, lineHeight = 21.sp) }, selected = selected, onClick = { onChoose(link.route) })
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}
