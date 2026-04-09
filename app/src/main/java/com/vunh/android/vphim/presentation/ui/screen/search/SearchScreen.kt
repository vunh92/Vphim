package com.vunh.android.vphim.presentation.ui.screen.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.vunh.android.vphim.domain.model.Movie
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
    onMovieClick: (Movie) -> Unit = {},
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()
    var showFilters by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(gridState) {
        snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { index ->
                if (index != null && index >= uiState.movies.size - 5) {
                    viewModel.onEvent(SearchUiEvent.LoadNextPage)
                }
            }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(
                    title = { Text("Tìm kiếm") },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { showFilters = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filters")
                        }
                    }
                )
                
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onEvent(SearchUiEvent.UpdateSearchQuery(it)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Nhập tên phim...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { 
                                viewModel.onEvent(SearchUiEvent.UpdateSearchQuery(""))
                                viewModel.onEvent(SearchUiEvent.ApplyFilters)
                            }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        viewModel.onEvent(SearchUiEvent.ApplyFilters)
                        focusManager.clearFocus()
                    }),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.movies.isEmpty() && uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.movies.isEmpty()) {
                Text(text = "Không tìm thấy kết quả", modifier = Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    state = gridState,
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.movies) { movie ->
                        SearchMovieItem(movie = movie, onClick = { onMovieClick(movie) })
                    }
                    if (uiState.isLoading) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(8.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }

            if (showFilters) {
                FilterBottomSheet(
                    uiState = uiState,
                    onDismiss = { showFilters = false },
                    onEvent = { event ->
                        viewModel.onEvent(event)
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchMovieItem(
    movie: Movie,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(8.dp)
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
                Text(
                    text = "${movie.year} • ${movie.originTitle}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f),
                    maxLines = 1
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun FilterBottomSheet(
    uiState: SearchUiState,
    onDismiss: () -> Unit,
    onEvent: (SearchUiEvent) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Bộ lọc", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Search Keyword in BottomSheet
            FilterSection("Từ khóa tìm kiếm") {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { onEvent(SearchUiEvent.UpdateSearchQuery(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập tên phim...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Type List
            FilterSection("Loại phim") {
                val types = listOf(
                    "phim-moi-cap-nhat" to "Mới cập nhật",
                    "phim-bo" to "Phim bộ",
                    "phim-le" to "Phim lẻ",
                    "tv-shows" to "TV Shows",
                    "hoat-hinh" to "Hoạt hình",
                    "phim-vietsub" to "Vietsub",
                    "phim-thuyet-minh" to "Thuyết minh",
                    "phim-long-tieng" to "Lồng tiếng"
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { (slug, label) ->
                        FilterChip(
                            selected = uiState.selectedTypeList == slug,
                            onClick = { onEvent(SearchUiEvent.ChangeTypeList(slug)) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Sort Field
            FilterSection("Sắp xếp theo") {
                val fields = listOf(
                    "modified.time" to "Thời gian cập nhật",
                    "_id" to "ID phim",
                    "year" to "Năm phát hành"
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    fields.forEach { (slug, label) ->
                        FilterChip(
                            selected = uiState.selectedSortField == slug,
                            onClick = { onEvent(SearchUiEvent.ChangeSortField(slug)) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Sort Type
            FilterSection("Thứ tự") {
                val types = listOf(
                    "desc" to "Giảm dần",
                    "asc" to "Tăng dần"
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    types.forEach { (slug, label) ->
                        FilterChip(
                            selected = uiState.selectedSortType == slug,
                            onClick = { onEvent(SearchUiEvent.ChangeSortType(slug)) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Sort Lang
            FilterSection("Định dạng") {
                val langs = listOf(
                    null to "Tất cả",
                    "vietsub" to "Vietsub",
                    "thuyet-minh" to "Thuyết minh",
                    "long-tieng" to "Lồng tiếng"
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    langs.forEach { (slug, label) ->
                        FilterChip(
                            selected = uiState.selectedSortLang == slug,
                            onClick = { onEvent(SearchUiEvent.ChangeSortLang(slug)) },
                            label = { Text(label) }
                        )
                    }
                }
            }

            // Year
            FilterSection("Năm") {
                val currentYear = Calendar.getInstance().get(Calendar.YEAR)
                val years = (currentYear downTo 1970).toList()
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.selectedYear == null,
                        onClick = { onEvent(SearchUiEvent.ChangeYear(null)) },
                        label = { Text("Tất cả") }
                    )
                    years.take(15).forEach { year ->
                        FilterChip(
                            selected = uiState.selectedYear == year,
                            onClick = { onEvent(SearchUiEvent.ChangeYear(year)) },
                            label = { Text(year.toString()) }
                        )
                    }
                }
            }

            // Category
            FilterSection("Thể loại") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.selectedCategory == null,
                        onClick = { onEvent(SearchUiEvent.ChangeCategory(null)) },
                        label = { Text("Tất cả") }
                    )
                    uiState.categories.forEach { category ->
                        FilterChip(
                            selected = uiState.selectedCategory == category.slug,
                            onClick = { onEvent(SearchUiEvent.ChangeCategory(category.slug)) },
                            label = { Text(category.name) }
                        )
                    }
                }
            }

            // Country
            FilterSection("Quốc gia") {
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = uiState.selectedCountry == null,
                        onClick = { onEvent(SearchUiEvent.ChangeCountry(null)) },
                        label = { Text("Tất cả") }
                    )
                    uiState.countries.forEach { country ->
                        FilterChip(
                            selected = uiState.selectedCountry == country.slug,
                            onClick = { onEvent(SearchUiEvent.ChangeCountry(country.slug)) },
                            label = { Text(country.name) }
                        )
                    }
                }
            }
            
            Button(
                onClick = {
                    onEvent(SearchUiEvent.ApplyFilters)
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Áp dụng")
            }
        }
    }
}

@Composable
private fun FilterSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FlowRow(horizontalArrangement: Arrangement.Horizontal, content: @Composable () -> Unit) {
    androidx.compose.foundation.layout.FlowRow(
        horizontalArrangement = horizontalArrangement,
        content = { content() }
    )
}
