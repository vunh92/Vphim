package com.vunh.android.vphim.presentation.ui.screen.anime

sealed class AnimeMoviesUiEvent {
    object Refresh : AnimeMoviesUiEvent()
    object LoadMore : AnimeMoviesUiEvent()
}
