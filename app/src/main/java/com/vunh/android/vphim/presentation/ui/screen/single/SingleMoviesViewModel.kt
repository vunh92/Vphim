package com.vunh.android.vphim.presentation.ui.screen.single

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.domain.usecase.GetSingleMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SingleMoviesViewModel @Inject constructor(
    private val getSingleMoviesUseCase: GetSingleMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SingleMoviesUiState())
    val uiState: StateFlow<SingleMoviesUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: SingleMoviesUiEvent) {
        when (event) {
            SingleMoviesUiEvent.Refresh -> loadData(isRefreshing = true)
            SingleMoviesUiEvent.LoadMore -> loadMore()
        }
    }

    private fun loadData(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(
                isLoading = !isRefreshing,
                isRefreshing = isRefreshing,
                currentPage = 1,
                errorMessage = null
            ) }
            try {
                val movies = getSingleMoviesUseCase(page = 1)
                _uiState.update { it.copy(
                    movies = movies,
                    isLoading = false,
                    isRefreshing = false,
                    hasMorePages = movies.isNotEmpty()
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    isRefreshing = false,
                    errorMessage = e.message ?: "Không thể tải dữ liệu"
                ) }
            }
        }
    }

    private fun loadMore() {
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMorePages) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingMore = true) }
            try {
                val nextPage = _uiState.value.currentPage + 1
                val newMovies = getSingleMoviesUseCase(page = nextPage)
                _uiState.update { it.copy(
                    movies = it.movies + newMovies,
                    currentPage = nextPage,
                    isLoadingMore = false,
                    hasMorePages = newMovies.isNotEmpty()
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoadingMore = false) }
            }
        }
    }
}
