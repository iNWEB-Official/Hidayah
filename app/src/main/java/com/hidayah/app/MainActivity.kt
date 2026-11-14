package com.hidayah.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.hidayah.app.data.SettingsStore
import com.hidayah.app.ui.HidayahApp
import com.hidayah.app.ui.theme.HidayahTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settings = SettingsStore(applicationContext)
        setContent {
            val options by settings.settings.collectAsState()
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, density.fontScale * options.fontScale)) {
                HidayahTheme(dark = options.dark) { HidayahApp(settings) }
            }
        }
    }
}
