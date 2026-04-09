package com.vunh.android.vphim.presentation.ui.screen.search

sealed class SearchUiEvent {
    object Refresh : SearchUiEvent()
    object LoadNextPage : SearchUiEvent()
    data class SearchByKeyword(val keyword: String) : SearchUiEvent()
    data class UpdateSearchQuery(val query: String) : SearchUiEvent()
    data class ChangeTypeList(val typeList: String) : SearchUiEvent()
    data class ChangeSortField(val sortField: String) : SearchUiEvent()
    data class ChangeSortType(val sortType: String) : SearchUiEvent()
    data class ChangeSortLang(val sortLang: String?) : SearchUiEvent()
    data class ChangeCategory(val category: String?) : SearchUiEvent()
    data class ChangeCountry(val country: String?) : SearchUiEvent()
    data class ChangeYear(val year: Int?) : SearchUiEvent()
    data class ChangeLimit(val limit: Int) : SearchUiEvent()
    object ApplyFilters : SearchUiEvent()
}
