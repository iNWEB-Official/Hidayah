package com.hidayah.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import android.net.Uri
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hidayah.app.data.SettingsStore
import com.hidayah.app.ui.screens.ValuesScreen
import com.hidayah.app.ui.screens.ValueDetailScreen
import com.hidayah.app.ui.screens.DuasScreen
import com.hidayah.app.ui.screens.LearningScreen
import com.hidayah.app.ui.screens.AccountScreen
import com.hidayah.app.ui.screens.SettingsScreen
import com.hidayah.app.ui.screens.AppSearchScreen
import com.hidayah.app.ui.screens.NotificationsScreen
import com.hidayah.app.ui.screens.HomeScreen
import com.hidayah.app.ui.screens.PrayerScreen
import com.hidayah.app.ui.screens.QuranScreen
import com.hidayah.app.ui.screens.QuranReaderScreen
import com.hidayah.app.ui.screens.TrackerScreen
import com.hidayah.app.ui.theme.AppIcons
import com.hidayah.app.ui.theme.Forest
import com.hidayah.app.ui.theme.Muted
import com.hidayah.app.ui.theme.Sage

private data class Tab(val route: String, val label: String, val icon: ImageVector)
private val tabs = listOf(
    Tab("home", "হোম", Icons.Outlined.Home),
    Tab("quran", "কুরআন", AppIcons.Quran),
    Tab("prayer", "নামাজ", AppIcons.Prayer),
    Tab("tracker", "আমল", Icons.Outlined.CheckCircle),
)

@Composable
fun HidayahApp(settings: SettingsStore) {
    val navController = rememberNavController()
    val entry by navController.currentBackStackEntryAsState()
    val context = LocalContext.current.applicationContext
    val factory = remember(context) { HidayahViewModelFactory(context) }
    val options by settings.settings.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    fun navigate(route: String) { navController.navigate(route) { launchSingleTop = true } }
    fun navigateTab(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
        HidayahDrawer(entry?.destination?.route) { route ->
            scope.launch {
                drawerState.close()
                if (route in tabs.map { it.route }) navigateTab(route) else navigate(route)
            }
        }
    }) {
    Scaffold(topBar = {
        HidayahTopBar(dark = options.dark,
            onMenu = { scope.launch { drawerState.open() } },
            onToggleDark = { settings.setDark(!options.dark) },
            onNotifications = { navigate("notifications") }, onSearch = { navigate("search") })
    }, bottomBar = {
        NavigationBar(containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface) {
            tabs.forEach { tab ->
                NavigationBarItem(
                    selected = entry?.destination?.route == tab.route || (tab.route == "quran" && entry?.destination?.route == "quran/{number}") || (tab.route == "home" && entry?.destination?.route in listOf("values", "values/{topic}", "duas?query={query}", "learning?query={query}", "settings", "account", "notifications", "search")),
                    onClick = {
                        navigateTab(tab.route)
                    },
                    icon = { Icon(tab.icon, contentDescription = null) },
                    label = { Text(tab.label, maxLines = 1) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Forest, selectedTextColor = Forest,
                        indicatorColor = Sage, unselectedIconColor = Muted, unselectedTextColor = Muted,
                    ),
                )
            }
        }
    }) { padding ->
        NavHost(navController = navController, startDestination = "home", modifier = Modifier.padding(padding)) {
            composable("home") {
                val vm: HomeViewModel = viewModel(factory = factory)
                HomeScreen(vm, onOpenQuran = { navController.navigate("quran") { launchSingleTop = true } },
                    onOpenPrayer = { navController.navigate("prayer") { launchSingleTop = true } },
                    onOpenTracker = { navController.navigate("tracker") { launchSingleTop = true } },
                    onValues = { navController.navigate("values") }, onDuas = { navController.navigate("duas") },
                    onLearning = { navController.navigate("learning") })
            }
            composable("values") { ValuesScreen(onBack = { navController.popBackStack() }, onOpen = { navController.navigate("values/$it") }) }
            composable("values/{topic}", arguments = listOf(navArgument("topic") { type = NavType.StringType })) { backStack ->
                val vm: ValuesViewModel = viewModel(factory = factory)
                ValueDetailScreen(backStack.arguments?.getString("topic").orEmpty(), vm, onBack = { navController.popBackStack() })
            }
            composable("duas?query={query}", arguments = listOf(navArgument("query") { type = NavType.StringType; defaultValue = "" })) { backStack ->
                DuasScreen(initialQuery = backStack.arguments?.getString("query").orEmpty(), onBack = { navController.popBackStack() })
            }
            composable("learning?query={query}", arguments = listOf(navArgument("query") { type = NavType.StringType; defaultValue = "" })) { backStack ->
                LearningScreen(initialQuery = backStack.arguments?.getString("query").orEmpty(), onBack = { navController.popBackStack() })
            }
            composable("settings") { SettingsScreen(settings, onBack = { navController.popBackStack() }) }
            composable("account") {
                val vm: AccountViewModel = viewModel(factory = factory)
                AccountScreen(vm, onBack = { navController.popBackStack() })
            }
            composable("notifications") { NotificationsScreen(settings, onBack = { navController.popBackStack() }, onSettings = { navigate("settings") }) }
            composable("search") {
                AppSearchScreen(onBack = { navController.popBackStack() }, onSelect = { result ->
                    when (result.destination) {
                        "value" -> navigate("values/${result.queryText}")
                        "dua" -> navigate("duas?query=${Uri.encode(result.queryText)}")
                        "story" -> navigate("learning?query=${Uri.encode(result.queryText)}")
                        in listOf("home", "quran", "prayer", "tracker") -> navigateTab(result.destination)
                        else -> navigate(result.destination)
                    }
                })
            }
            composable("quran") {
                val vm: QuranViewModel = viewModel(factory = factory)
                QuranScreen(vm, onOpenSurah = { navController.navigate("quran/$it") })
            }
            composable("quran/{number}", arguments = listOf(navArgument("number") { type = NavType.IntType })) { backStack ->
                val vm: QuranReaderViewModel = viewModel(factory = factory)
                QuranReaderScreen(backStack.arguments?.getInt("number") ?: 0, vm, onBack = { navController.popBackStack() })
            }
            composable("prayer") {
                val vm: PrayerViewModel = viewModel(factory = factory)
                PrayerScreen(vm)
            }
            composable("tracker") {
                val vm: TrackerViewModel = viewModel(factory = factory)
                TrackerScreen(vm)
            }
        }
    }
    }
}
