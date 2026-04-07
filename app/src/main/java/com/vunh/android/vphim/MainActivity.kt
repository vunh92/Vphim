package com.vunh.android.vphim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.vunh.android.vphim.presentation.ui.navigation.Destination
import com.vunh.android.vphim.presentation.ui.screen.anime.AnimeMoviesScreen
import com.vunh.android.vphim.presentation.ui.screen.favorite.FavoriteScreen
import com.vunh.android.vphim.presentation.ui.screen.home.HomeScreen
import com.vunh.android.vphim.presentation.ui.screen.series.SeriesScreen
import com.vunh.android.vphim.presentation.ui.screen.single.SingleMoviesScreen
import com.vunh.android.vphim.ui.theme.VphimTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VphimTheme {
                VphimApp()
            }
        }
    }
}

@Composable
fun VphimApp() {
    var currentDestination by rememberSaveable { mutableStateOf(Destination.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            Destination.entries.forEach { destination ->
                item(
                    icon = {
                        Icon(
                            painter = painterResource(destination.icon),
                            contentDescription = destination.label
                        )
                    },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    Destination.HOME -> HomeScreen()
                    Destination.SERIES -> SeriesScreen()
                    Destination.SINGLE -> SingleMoviesScreen()
                    Destination.ANIME -> AnimeMoviesScreen()
                    Destination.FAVORITES -> FavoriteScreen()
                }
            }
        }
    }
}
