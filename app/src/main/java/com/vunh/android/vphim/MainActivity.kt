package com.vunh.android.vphim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vunh.android.vphim.domain.model.Movie
import com.vunh.android.vphim.presentation.ui.navigation.Destination
import com.vunh.android.vphim.presentation.ui.screen.anime.AnimeMoviesScreen
import com.vunh.android.vphim.presentation.ui.screen.detail.MovieDetailScreen
import com.vunh.android.vphim.presentation.ui.screen.favorite.FavoriteScreen
import com.vunh.android.vphim.presentation.ui.screen.home.HomeScreen
import com.vunh.android.vphim.presentation.ui.screen.profile.ProfileScreen
import com.vunh.android.vphim.presentation.ui.screen.series.SeriesScreen
import com.vunh.android.vphim.presentation.ui.screen.single.SingleMoviesScreen
import com.vunh.android.vphim.presentation.ui.screen.search.SearchScreen
import com.vunh.android.vphim.ui.theme.VphimTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val showNavigationBar = Destination.entries.any { destination ->
        currentDestination?.hierarchy?.any { it.route == destination.route } == true
    }

    NavigationSuiteScaffold(
        layoutType = if (showNavigationBar) {
            androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.NavigationBar
        } else {
            androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType.None
        },
        navigationSuiteItems = {
            if (showNavigationBar) {
                Destination.entries.forEach { destination ->
                    item(
                        icon = {
                            Icon(
                                painter = painterResource(destination.icon),
                                contentDescription = destination.label,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = { Text(destination.label) },
                        selected = currentDestination
                            ?.hierarchy
                            ?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Destination.HOME.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Destination.HOME.route) {
                    HomeScreen(
                        onNavigateToDestination = { destination ->
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onMovieClick = { movie ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set(SELECTED_MOVIE_KEY, movie)
                            navController.navigate("$DETAIL_ROUTE/${movie.slug}")
                        },
                        onLoginClick = {
                            navController.navigate(LOGIN_ROUTE)
                        },
                        onSearchClick = {
                            navController.navigate(SEARCH_ROUTE)
                        }
                    )
                }
                composable(Destination.SERIES.route) { SeriesScreen() }
                composable(Destination.SINGLE.route) { SingleMoviesScreen() }
                composable(Destination.ANIME.route) { AnimeMoviesScreen() }
                composable(Destination.FAVORITES.route) { FavoriteScreen() }
                composable(LOGIN_ROUTE) {
                    ProfileScreen(
                        onBackClick = { navController.navigateUp() }
                    )
                }
                composable(SEARCH_ROUTE) {
                    SearchScreen(
                        onBackClick = { navController.navigateUp() },
                        onMovieClick = { movie ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set(SELECTED_MOVIE_KEY, movie)
                            navController.navigate("$DETAIL_ROUTE/${movie.slug}")
                        }
                    )
                }
                composable(
                    route = "$DETAIL_ROUTE/{$DETAIL_SLUG_ARG}",
                    arguments = listOf(
                        navArgument(DETAIL_SLUG_ARG) { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val movie = navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Movie>(SELECTED_MOVIE_KEY)
                        ?: fallbackMovie(
                            slug = backStackEntry.arguments?.getString(DETAIL_SLUG_ARG).orEmpty()
                        )

                    MovieDetailScreen(
                        movie = movie,
                        onBack = { navController.navigateUp() }
                    )
                }
            }
        }
    }
}

private const val DETAIL_ROUTE = "detail"
private const val DETAIL_SLUG_ARG = "slug"
private const val SELECTED_MOVIE_KEY = "selected_movie"
private const val LOGIN_ROUTE = "login"
private const val SEARCH_ROUTE = "search"

private fun fallbackMovie(slug: String): Movie {
    return Movie(
        id = slug,
        title = slug,
        originTitle = "",
        slug = slug,
        posterUrl = "",
        thumbUrl = "",
        year = 0,
        modifiedTime = "",
        imdbId = null,
        type = null
    )
}
