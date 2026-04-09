package com.vunh.android.vphim.presentation.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vunh.android.vphim.data.local.ProfileManager
import com.vunh.android.vphim.domain.usecase.GetAnimeMoviesUseCase
import com.vunh.android.vphim.domain.usecase.GetCategoriesUseCase
import com.vunh.android.vphim.domain.usecase.GetMoviesByCategoryUseCase
import com.vunh.android.vphim.domain.usecase.GetMoviesUseCase
import com.vunh.android.vphim.domain.usecase.GetSeriesMoviesUseCase
import com.vunh.android.vphim.domain.usecase.GetSingleMoviesUseCase
import com.vunh.android.vphim.domain.usecase.RefreshMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
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
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getMoviesByCategoryUseCase: GetMoviesByCategoryUseCase,
    private val getSeriesMoviesUseCase: GetSeriesMoviesUseCase,
    private val getSingleMoviesUseCase: GetSingleMoviesUseCase,
    private val getAnimeMoviesUseCase: GetAnimeMoviesUseCase,
    private val profileManager: ProfileManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeMovies()
        observeCurrentUser()
        loadData()
    }

    private fun observeMovies() {
        getMoviesUseCase()
            .onEach { movies ->
                _uiState.update { state ->
                    state.copy(
                        movies = movies,
                        message = if (movies.isEmpty()) "" else "Đã tải ${movies.size} phim mới cập nhật",
                        errorMessage = null
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadData(isRefreshing: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = !isRefreshing,
                    isRefreshing = isRefreshing,
                    errorMessage = null
                )
            }
            try {
                val categoriesDeferred = async { getCategoriesUseCase() }
                val actionMoviesDeferred = async { getMoviesByCategoryUseCase(categorySlug = "hanh-dong") }
                val seriesMoviesDeferred = async { getSeriesMoviesUseCase() }
                val singleMoviesDeferred = async { getSingleMoviesUseCase() }
                val animeMoviesDeferred = async { getAnimeMoviesUseCase() }
                val refreshMoviesDeferred = async { refreshMoviesUseCase() }

                val categories = categoriesDeferred.await()
                val actionMovies = actionMoviesDeferred.await()
                val seriesMovies = seriesMoviesDeferred.await()
                val singleMovies = singleMoviesDeferred.await()
                val animeMovies = animeMoviesDeferred.await()
                refreshMoviesDeferred.await()

                _uiState.update { state ->
                    state.copy(
                        categories = categories,
                        actionMovies = actionMovies,
                        seriesMovies = seriesMovies,
                        singleMovies = singleMovies,
                        animeMovies = animeMovies,
                        isLoading = false,
                        isRefreshing = false
                    )
                }
            } catch (exception: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        isRefreshing = false,
                        errorMessage = exception.message ?: "Không thể tải dữ liệu"
                    )
                }
            }
        }
    }

    private fun observeCurrentUser() {
        profileManager.currentUser
            .onEach { user ->
                _uiState.update { state ->
                    state.copy(user = user)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.Refresh -> loadData(isRefreshing = true)
            HomeUiEvent.OnSeeMoreActionMovies -> {}
            HomeUiEvent.OnSeeMoreSeriesMovies -> {}
            HomeUiEvent.OnSeeMoreSingleMovies -> {}
            HomeUiEvent.OnSeeMoreAnimeMovies -> {}
            HomeUiEvent.OnLogout -> {
                profileManager.clearUser()
            }
        }
    }
}
