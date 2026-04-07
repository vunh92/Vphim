package com.vunh.android.vphim.presentation.ui.screen.series

sealed class SeriesUiEvent {
    object Refresh : SeriesUiEvent()
    object LoadMore : SeriesUiEvent()
}
