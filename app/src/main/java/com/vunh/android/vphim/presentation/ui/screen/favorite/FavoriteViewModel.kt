package com.vunh.android.vphim.presentation.ui.screen.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.domain.usecase.GetFavoriteMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteMoviesUseCase: GetFavoriteMoviesUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteUiState())
    val uiState: StateFlow<FavoriteUiState> = _uiState.asStateFlow()

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        getFavoriteMoviesUseCase()
            .onEach { favorites ->
                _uiState.update { it.copy(favorites = favorites) }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: FavoriteUiEvent) {
        when (event) {
            FavoriteUiEvent.Refresh -> {
                // Since we are observing a Flow from repository, 
                // refresh might not be explicitly needed for data updates,
                // but we keep the structure.
            }
        }
    }
}
