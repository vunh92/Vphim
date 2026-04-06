package com.vunh.android.vphim.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.domain.usecase.GetMoviesUseCase
import com.vunh.android.vphim.domain.usecase.RefreshMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    private val refreshMoviesUseCase: RefreshMoviesUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
        loadMovies()
    }

    private fun observeMovies() {
        getMoviesUseCase()
            .onEach { movies ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        movies = movies,
                        message = if (movies.isEmpty()) "" else "Đã tải ${movies.size} phim mới cập nhật",
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadMovies() {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoading = true, errorMessage = null)
            }
            try {
                refreshMoviesUseCase()
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Không thể tải dữ liệu phim"
                    )
                }
            }
        }
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> loadMovies()
        }
    }
}
