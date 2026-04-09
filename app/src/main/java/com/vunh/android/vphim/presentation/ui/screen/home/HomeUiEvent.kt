package com.vunh.android.vphim.presentation.ui.screen.home

sealed class HomeUiEvent {
    object Refresh : HomeUiEvent()
    object OnSeeMoreActionMovies : HomeUiEvent()
    object OnSeeMoreSeriesMovies : HomeUiEvent()
    object OnSeeMoreSingleMovies : HomeUiEvent()
    object OnSeeMoreAnimeMovies : HomeUiEvent()
    object OnLogout : HomeUiEvent()
}
