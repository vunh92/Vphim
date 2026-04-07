package com.vunh.android.vphim.presentation.ui.screen.single

sealed class SingleMoviesUiEvent {
    object Refresh : SingleMoviesUiEvent()
    object LoadMore : SingleMoviesUiEvent()
}
