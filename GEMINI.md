# AGENTS.md - BHX Jetpack Android Project

This file contains guidelines for AI agents working in this repository.

## Project Overview

- **Architecture**: Clean Architecture with MVVM
- **Tech Stack**: Kotlin, Jetpack Compose, Hilt, Room, Retrofit, Navigation Compose
- **Min SDK**: 33 (Android 13)
- **Target SDK**: 35

## 🎯 Các Layer chính:
### 1. **Presentation Layer** (Màu xanh lá)
- UI với Jetpack Compose (có @Preview)
- ViewModel được inject bởi Hilt (@HiltViewModel)

### 2. **Domain Layer** (Màu xanh dương)  
- Use Cases (business logic)
- Domain Models
- Repository Interfaces (không phụ thuộc Android)

### 3. **Data Layer** (Màu cam)
- Repository Implementation
- Remote DataSource (Retrofit API)
- Local DataSource (Room + MMKV)

### 4. **DI Layer** (Màu tím)
- @HiltAndroidApp
- Hilt Modules (Network, Database, Repository)
- Tự động inject toàn bộ dependencies

## 🔑 Điểm quan trọng:
- **Dependency Rule**: Luôn đi từ ngoài vào trong (Presentation → Domain → Data)
- **Strings**: Phải trong `strings.xml`, không hardcode
- **DI**: Hilt inject tất cả, không khởi tạo manual


## Directory Structure

```
app/src/main/java/com/vunh/jetpack/bhx/
├── data/           # Data layer (repositories impl, remote, local)
│    ├── repository/          # Repository implementations
│    ├── remote/             # API - Network calls
│    │   ├── api/           # Retrofit services
│    │   ├── dto/           # Data Transfer Objects
│    │   └── datasource/    # Remote data sources
│    ├── local/             # Database - Local storage
│    │   ├── database/      # Room database
│    │   ├── dao/           # Data Access Objects
│    │   ├── entity/        # Room entities
│    │   └── datasource/    # Local data sources
│    ├── mapper/            # DTO ↔ Domain Model converters
│    └── cache/             # Cache management (MMKV)
├── di/             # Hilt dependency injection modules
│    ├── AppModule.kt           # App-level dependencies
│    ├── NetworkModule.kt       # Retrofit, OkHttp, API services
│    ├── DatabaseModule.kt      # Room database, DAOs
│    ├── RepositoryModule.kt    # Repository bindings
│    ├── UseCaseModule.kt       # Use case dependencies (optional)
│    ├── DataSourceModule.kt    # Data sources
│    └── UtilModule.kt          # Utilities (Gson, DateFormatter, etc.)
├── domain/         # Domain layer (models, repositories interfaces, use cases)
│    ├── model/              # Domain Models (Business entities)
│    ├── repository/         # Repository Interfaces
│    ├── usecase/           # Use Cases (Business logic)
│    └── exception/         # Custom exceptions
├── presentation/   # UI layer (screens, viewmodels, common components)
│    ├── ui/
│    │   ├── theme/              # App theme, colors, typography
│    │   ├── components/         # Reusable composables
│    │   ├── screen/            # Screens (features)
│    │   │   ├── home/
│    │   │   │   ├── HomeScreen.kt
│    │   │   │   ├── HomeViewModel.kt
│    │   │   │   ├── HomeUiState.kt
│    │   │   │   └── HomeUiEvent.kt
│    │   │   ├── product/
│    │   │   │   ├── ProductListScreen.kt
│    │   │   │   ├── ProductDetailScreen.kt
│    │   │   │   ├── ProductViewModel.kt
│    │   │   │   └── ProductUiState.kt
│    │   │   └── profile/
│    │   └── navigation/        # Navigation graph
│    ├── mapper/                # Domain Model -> UI Model
│    └── util/                  # UI utilities
└── ui/theme/       # Compose theme (colors, typography)
```

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Run unit tests
./gradlew test

# Run a specific test class
./gradlew test --tests "com.vunh.jetpack.bhx.ExampleUnitTest"

# Run a specific test method
./gradlew test --tests "com.vunh.jetpack.bhx.ExampleUnitTest.addition_isCorrect"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean and rebuild
./gradlew clean assembleDebug

# Run lint
./gradlew lint

# Build with dependency report
./gradlew dependencies > dependencies.txt
```

## Code Style Guidelines

### Kotlin Conventions

- **Use `object` over companion objects** for module-level singletons
- **Prefer extension functions** for utility operations
- **Use `invoke` operator** in UseCases to make them callable like functions
- **Use `@Inject constructor`** for all injectable classes
- **Prefer `flow` over LiveData** for reactive data streams

### Naming Conventions

- **ViewModels**: `ScreenName + ViewModel` (e.g., `HomeViewModel`)
- **Screens**: `ScreenName + Screen` (e.g., `HomeScreen`)
- **UseCases**: `Action + UseCase` (e.g., `SyncPostsUseCase`, `ObservePostsUseCase`)
- **Repository interfaces**: `DomainName + Repository` (e.g., `HomeRepository`)
- **Repository implementations**: `DomainName + RepositoryImpl`
- **State classes**: `Name + State` (e.g., `UiState`)
- **Event classes**: `Name + Event`

### Compose Guidelines

- **Screen composables**: Public, take ViewModel as parameter with default `hiltViewModel()`
- **Helper composables**: Private or internal, prefixed with descriptive name
- **Use `Modifier`** consistently as the last parameter
- **Extract complex UI** into separate composable functions
- **Use `remember` and `rememberSaveable`** appropriately for state
- **Prefer `collectAsState()`** over `collectAsStateWithLifecycle()`

### Import Organization

```kotlin
// 1. Android/Compose imports
import androidx.compose.foundation.background
import androidx.compose.material3.*

// 2. Hilt/Dagger imports
import dagger.hilt.android.lifecycle.HiltViewModel

// 3. Kotlin Standard Library
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// 4. Project imports (grouped by package depth)
 - domain.models
 - domain.repository
 - domain.usecase
 - presentation.screens
// - presentation.common
import com.vunh.android.vphim.domain.model.*
import com.vunh.android.vphim.presentation.home.*
```

### Error Handling

- **ViewModels**: Wrap async operations in try-catch, emit error states
- **Repositories**: Let exceptions propagate, handle in UseCase/ViewModel
- **UI**: Show user-friendly error messages via Snackbar or Dialog
- **Coroutines**: Use `catch` operator on flows, handle in `finally` blocks

### State Management Pattern

# Presentation Layer
## UI State Management
### UI State Classes
```kotlin
// presentation/screen/home/HomeUiState.kt
data class HomeUiState(
    val isLoading: Boolean = false,
    val user: UserUiModel? = null,
    val featuredProducts: List<ProductUiModel> = emptyList(),
    val categories: List<CategoryUiModel> = emptyList(),
    val errorMessage: String? = null,
    val isRefreshing: Boolean = false
) {
    val isEmpty: Boolean
        get() = featuredProducts.isEmpty() && categories.isEmpty()
    
    val hasError: Boolean
        get() = errorMessage != null
}

// presentation/screen/product/ProductListUiState.kt
data class ProductListUiState(
    val products: List<ProductUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val searchQuery: String = "",
    val selectedCategory: CategoryUiModel? = null,
    val sortBy: ProductSortBy = ProductSortBy.POPULAR,
    val filters: ProductFilters = ProductFilters(),
    val errorMessage: String? = null,
    val hasMorePages: Boolean = true
)

data class ProductFilters(
    val onlyInStock: Boolean = false,
    val onlyOnSale: Boolean = false,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val minRating: Float? = null
)

// presentation/screen/product/ProductDetailUiState.kt
data class ProductDetailUiState(
    val product: ProductUiModel? = null,
    val isLoading: Boolean = false,
    val selectedQuantity: Int = 1,
    val isAddingToCart: Boolean = false,
    val addToCartSuccess: Boolean = false,
    val errorMessage: String? = null,
    val relatedProducts: List<ProductUiModel> = emptyList()
)

// presentation/screen/cart/CartUiState.kt
data class CartUiState(
    val items: List<CartItemUiModel> = emptyList(),
    val subtotal: Double = 0.0,
    val tax: Double = 0.0,
    val shipping: Double = 0.0,
    val total: Double = 0.0,
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null
) {
    val isEmpty: Boolean get() = items.isEmpty()
    val itemCount: Int get() = items.sumOf { it.quantity }
}

```
### UI Events
```kotlin
// presentation/screen/home/HomeUiEvent.kt
sealed class HomeUiEvent {
    object Refresh : HomeUiEvent()
    data class OnProductClick(val productId: String) : HomeUiEvent()
    data class OnCategoryClick(val categoryId: String) : HomeUiEvent()
    object OnProfileClick : HomeUiEvent()
    object OnCartClick : HomeUiEvent()
    object DismissError : HomeUiEvent()
}

// presentation/screen/product/ProductListUiEvent.kt
sealed class ProductListUiEvent {
    data class OnSearchQueryChange(val query: String) : ProductListUiEvent()
    data class OnCategorySelect(val category: CategoryUiModel?) : ProductListUiEvent()
    data class OnSortChange(val sortBy: ProductSortBy) : ProductListUiEvent()
    data class OnFilterChange(val filters: ProductFilters) : ProductListUiEvent()
    data class OnProductClick(val productId: String) : ProductListUiEvent()
    object OnLoadMore : ProductListUiEvent()
    object OnRefresh : ProductListUiEvent()
}

// presentation/screen/product/ProductDetailUiEvent.kt
sealed class ProductDetailUiEvent {
    data class OnQuantityChange(val quantity: Int) : ProductDetailUiEvent()
    object OnAddToCart : ProductDetailUiEvent()
    object OnBuyNow : ProductDetailUiEvent()
    data class OnRelatedProductClick(val productId: String) : ProductDetailUiEvent()
    object DismissSuccess : ProductDetailUiEvent()
}

// presentation/screen/cart/CartUiEvent.kt
sealed class CartUiEvent {
    data class OnQuantityChange(val itemId: String, val quantity: Int) : CartUiEvent()
    data class OnRemoveItem(val itemId: String) : CartUiEvent()
    object OnClearCart : CartUiEvent()
    object OnCheckout : CartUiEvent()
}

```
## ViewModels
### Base ViewModel
```kotlin
// presentation/base/BaseViewModel.kt
abstract class BaseViewModel<State, Event> : ViewModel() {
    
    protected abstract val _uiState: MutableStateFlow<State>
    abstract val uiState: StateFlow<State>
    
    abstract fun onEvent(event: Event)
    
    protected fun <T> Flow<T>.collectInViewModel(
        onEach: suspend (T) -> Unit
    ) {
        viewModelScope.launch {
            collect(onEach)
        }
    }
}

```
### Home ViewModel
```kotlin
// presentation/screen/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getFeaturedProductsUseCase: GetFeaturedProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val productMapper: ProductUiMapper,
    private val userMapper: UserUiMapper,
    private val categoryMapper: CategoryUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.Refresh -> loadData()
            is HomeUiEvent.OnProductClick -> navigateToProduct(event.productId)
            is HomeUiEvent.OnCategoryClick -> navigateToCategory(event.categoryId)
            is HomeUiEvent.OnProfileClick -> navigateToProfile()
            is HomeUiEvent.OnCartClick -> navigateToCart()
            is HomeUiEvent.DismissError -> dismissError()
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Load user, products, categories in parallel
            val userDeferred = async { getCurrentUserUseCase() }
            val productsDeferred = async { getFeaturedProductsUseCase() }
            val categoriesDeferred = async { getCategoriesUseCase() }

            val userResult = userDeferred.await()
            val productsResult = productsDeferred.await()
            val categoriesResult = categoriesDeferred.await()

            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    user = (userResult as? Result.Success)?.data?.let { 
                        userMapper.toUiModel(it) 
                    },
                    featuredProducts = (productsResult as? Result.Success)?.data?.map { 
                        productMapper.toUiModel(it) 
                    } ?: emptyList(),
                    categories = (categoriesResult as? Result.Success)?.data?.map { 
                        categoryMapper.toUiModel(it) 
                    } ?: emptyList(),
                    errorMessage = when {
                        productsResult is Result.Error -> "Failed to load products"
                        categoriesResult is Result.Error -> "Failed to load categories"
                        else -> null
                    }
                )
            }
        }
    }

    private fun navigateToProduct(productId: String) {
        // Navigation handled in UI layer
    }

    private fun navigateToCategory(categoryId: String) {
        // Navigation handled in UI layer
    }

    private fun navigateToProfile() {
        // Navigation handled in UI layer
    }

    private fun navigateToCart() {
        // Navigation handled in UI layer
    }

    private fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}

```
### Product List ViewModel
```kotlin
// presentation/screen/product/ProductListViewModel.kt
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val productMapper: ProductUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    private val searchQueryDebounce = MutableStateFlow("")

    init {
        observeSearchQuery()
        loadProducts()
    }

    fun onEvent(event: ProductListUiEvent) {
        when (event) {
            is ProductListUiEvent.OnSearchQueryChange -> updateSearchQuery(event.query)
            is ProductListUiEvent.OnCategorySelect -> updateCategory(event.category)
            is ProductListUiEvent.OnSortChange -> updateSort(event.sortBy)
            is ProductListUiEvent.OnFilterChange -> updateFilters(event.filters)
            is ProductListUiEvent.OnProductClick -> { /* Navigate */ }
            is ProductListUiEvent.OnLoadMore -> loadMore()
            is ProductListUiEvent.OnRefresh -> refresh()
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            searchQueryDebounce
                .debounce(500) // Wait 500ms after user stops typing
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotEmpty()) {
                        searchProducts(query)
                    } else {
                        loadProducts()
                    }
                }
        }
    }

    private fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryDebounce.value = query
    }

    private fun updateCategory(category: CategoryUiModel?) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadProducts()
    }

    private fun updateSort(sortBy: ProductSortBy) {
        _uiState.update { it.copy(sortBy = sortBy) }
        loadProducts()
    }

    private fun updateFilters(filters: ProductFilters) {
        _uiState.update { it.copy(filters = filters) }
        loadProducts()
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = getProductsUseCase(
                GetProductsUseCase.Params(
                    category = _uiState.value.selectedCategory?.toDomain(),
                    sortBy = _uiState.value.sortBy,
                    onlyInStock = _uiState.value.filters.onlyInStock,
                    onlyOnSale = _uiState.value.filters.onlyOnSale,
                    maxPrice = _uiState.value.filters.maxPrice
                )
            )

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        products = result.data.map { productMapper.toUiModel(it) }
                    )
                    is Result.Error -> state.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    else -> state
                }
            }
        }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val result = searchProductsUseCase(SearchProductsUseCase.Params(query))

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        products = result.data.map { productMapper.toUiModel(it) }
                    )
                    is Result.Error -> state.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    else -> state
                }
            }
        }
    }

    private fun loadMore() {
        // Pagination logic
    }

    private fun refresh() {
        loadProducts()
    }
}

```
### Product Detail ViewModel
```kotlin
// presentation/screen/product/ProductDetailViewModel.kt
@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val getProductUseCase: GetProductUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val productMapper: ProductUiMapper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId: String = savedStateHandle.get<String>("productId") 
        ?: throw IllegalArgumentException("Product ID is required")

    private val _uiState = MutableStateFlow(ProductDetailUiState())
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    // One-time events
    private val _uiEvent = Channel<ProductDetailUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        loadProduct()
    }

    fun onEvent(event: ProductDetailUiEvent) {
        when (event) {
            is ProductDetailUiEvent.OnQuantityChange -> updateQuantity(event.quantity)
            is ProductDetailUiEvent.OnAddToCart -> addToCart()
            is ProductDetailUiEvent.OnBuyNow -> buyNow()
            is ProductDetailUiEvent.OnRelatedProductClick -> { /* Navigate */ }
            is ProductDetailUiEvent.DismissSuccess -> dismissSuccess()
        }
    }

    private fun loadProduct() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val result = getProductUseCase(GetProductUseCase.Params(productId))

            _uiState.update { state ->
                when (result) {
                    is Result.Success -> state.copy(
                        isLoading = false,
                        product = productMapper.toUiModel(result.data)
                    )
                    is Result.Error -> state.copy(
                        isLoading = false,
                        errorMessage = result.exception.message
                    )
                    else -> state
                }
            }
        }
    }

    private fun updateQuantity(quantity: Int) {
        val product = _uiState.value.product ?: return
        
        if (quantity in 1..product.stock) {
            _uiState.update { it.copy(selectedQuantity = quantity) }
        }
    }

    private fun addToCart() {
        viewModelScope.launch {
            _uiState.update { it.copy(isAddingToCart = true) }

            val result = addToCartUseCase(
                AddToCartUseCase.Params(
                    productId = productId,
                    quantity = _uiState.value.selectedQuantity
                )
            )

            when (result) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            isAddingToCart = false,
                            addToCartSuccess = true
                        ) 
                    }
                    // Auto dismiss after 2 seconds
                    delay(2000)
                    dismissSuccess()
                }
                is Result.Error -> {
                    _uiState.update { 
                        it.copy(
                            isAddingToCart = false,
                            errorMessage = result.exception.message
                        ) 
                    }
                }
                else -> {}
            }
        }
    }

    private fun buyNow() {
        // Navigate to checkout
    }

    private fun dismissSuccess() {
        _uiState.update { it.copy(addToCartSuccess = false) }
    }
}

```
### Cart ViewModel
```kotlin
// presentation/screen/cart/CartViewModel.kt
@HiltViewModel
class CartViewModel @Inject constructor(
    private val observeCartUseCase: ObserveCartUseCase,
    private val updateCartItemUseCase: UpdateCartItemUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val getCartTotalUseCase: GetCartTotalUseCase,
    private val cartItemMapper: CartItemUiMapper
) : ViewModel() {

    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        observeCart()
    }

    fun onEvent(event: CartUiEvent) {
        when (event) {
            is CartUiEvent.OnQuantityChange -> updateQuantity(event.itemId, event.quantity)
            is CartUiEvent.OnRemoveItem -> removeItem(event.itemId)
            is CartUiEvent.OnClearCart -> clearCart()
            is CartUiEvent.OnCheckout -> checkout()
        }
    }

    private fun observeCart() {
        viewModelScope.launch {
            observeCartUseCase()
                .collect { result ->
                    when (result) {
                        is Result.Success -> {
                            val items = result.data.map { cartItemMapper.toUiModel(it) }
                            updateCartState(items)
                        }
                        is Result.Error -> {
                            _uiState.update { 
                                it.copy(errorMessage = result.exception.message) 
                            }
                        }
                        else -> {}
                    }
                }
        }
    }

    private suspend fun updateCartState(items: List<CartItemUiModel>) {
        val totalResult = getCartTotalUseCase()
        
        if (totalResult is Result.Success) {
            val total = totalResult.data
            _uiState.update {
                it.copy(
                    items = items,
                    subtotal = total.subtotal,
                    tax = total.tax,
                    shipping = total.shipping,
                    total = total.total,
                    isLoading = false
                )
            }
        }
    }

    private fun updateQuantity(itemId: String, quantity: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }

            val result = updateCartItemUseCase(
                UpdateCartItemUseCase.Params(itemId, quantity)
            )

            if (result is Result.Error) {
                _uiState.update { 
                    it.copy(
                        isUpdating = false,
                        errorMessage = result.exception.message
                    ) 
                }
            } else {
                _uiState.update { it.copy(isUpdating = false) }
            }
        }
    }

    private fun removeItem(itemId: String) {
        viewModelScope.launch {
            removeFromCartUseCase(RemoveFromCartUseCase.Params(itemId))
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }

    private fun checkout() {
        // Navigate to checkout
    }
}

```
## UI Models (Presentation Models)
```kotlin
// presentation/model/UserUiModel.kt
data class UserUiModel(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val displayName: String,
    val isAdmin: Boolean
)

// presentation/model/ProductUiModel.kt
data class ProductUiModel(
    val id: String,
    val name: String,
    val description: String,
    val price: String,  // Formatted: "$99.99"
    val originalPrice: String?,  // "$129.99" if on sale
    val discountLabel: String?,  // "-23%"
    val stock: Int,
    val stockLabel: String,  // "In Stock" or "Only 3 left"
    val category: CategoryUiModel,
    val imageUrls: List<String>,
    val rating: Float,
    val ratingText: String,  // "4.5 (123 reviews)"
    val isInStock: Boolean,
    val isOnSale: Boolean,
    val canAddToCart: Boolean
)

// presentation/model/CategoryUiModel.kt
data class CategoryUiModel(
    val id: String,
    val name: String,
    val iconUrl: String?,
    val productCount: String  // "123 products"
)

// presentation/model/CartItemUiModel.kt
data class CartItemUiModel(
    val id: String,
    val product: ProductUiModel,
    val quantity: Int,
    val subtotal: String,  // "$199.98"
    val canIncreaseQuantity: Boolean,
    val canDecreaseQuantity: Boolean
)

```
## UI Mappers
```kotlin
// presentation/mapper/ProductUiMapper.kt
class ProductUiMapper @Inject constructor(
    private val categoryMapper: CategoryUiMapper
) {
    fun toUiModel(domain: Product): ProductUiModel {
        return ProductUiModel(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            price = formatPrice(domain.getFinalPrice()),
            originalPrice = if (domain.isOnSale()) formatPrice(domain.price) else null,
            discountLabel = domain.getDiscountLabel(),
            stock = domain.stock,
            stockLabel = getStockLabel(domain.stock),
            category = categoryMapper.toUiModel(domain.category),
            imageUrls = domain.imageUrls,
            rating = domain.rating,
            ratingText = "${domain.rating} (${domain.reviewCount} reviews)",
            isInStock = domain.isInStock(),
            isOnSale = domain.isOnSale(),
            canAddToCart = domain.isInStock()
        )
    }

    private fun formatPrice(price: Double): String {
        return "$%.2f".format(price)
    }

    private fun getStockLabel(stock: Int): String {
        return when {
            stock == 0 -> "Out of Stock"
            stock < 5 -> "Only $stock left"
            else -> "In Stock"
        }
    }
}

// presentation/mapper/UserUiMapper.kt
class UserUiMapper @Inject constructor() {
    fun toUiModel(domain: User): UserUiModel {
        return UserUiModel(
            id = domain.id,
            name = domain.name,
            email = domain.email,
            avatarUrl = domain.avatarUrl,
            displayName = domain.getDisplayName(),
            isAdmin = domain.isAdmin()
        )
    }
}

```
## Jetpack Compose UI
### Screens
```kotlin
// presentation/screen/home/HomeScreen.kt
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onProductClick = onProductClick,
        onCategoryClick = onCategoryClick,
        onProfileClick = onProfileClick,
        onCartClick = onCartClick
    )
}

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onEvent: (HomeUiEvent) -> Unit,
    onProductClick: (String) -> Unit,
    onCategoryClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            HomeTopBar(
                user = uiState.user,
                onProfileClick = onProfileClick,
                onCartClick = onCartClick
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> LoadingScreen()
            uiState.hasError -> ErrorScreen(
                message = uiState.errorMessage ?: stringResource(R.string.error_generic),
                onRetry = { onEvent(HomeUiEvent.Refresh) }
            )
            uiState.isEmpty -> EmptyScreen(
                message = stringResource(R.string.home_empty)
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Welcome Section
                    item {
                        WelcomeSection(userName = uiState.user?.displayName)
                    }

                    // Categories
                    item {
                        CategoriesSection(
                            categories = uiState.categories,
                            onCategoryClick = { category ->
                                onEvent(HomeUiEvent.OnCategoryClick(category.id))
                                onCategoryClick(category.id)
                            }
                        )
                    }

                    // Featured Products
                    item {
                        Text(
                            text = stringResource(R.string.home_featured_products),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(
                        items = uiState.featuredProducts,
                        key = { it.id }
                    ) { product ->
                        ProductCard(
                            product = product,
                            onClick = {
                                onEvent(HomeUiEvent.OnProductClick(product.id))
                                onProductClick(product.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    user: UserUiModel?,
    onProfileClick: () -> Unit,
    onCartClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(stringResource(R.string.app_name))
        },
        actions = {
            IconButton(onClick = onCartClick) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = stringResource(R.string.cart)
                )
            }
            IconButton(onClick = onProfileClick) {
                if (user?.avatarUrl != null) {
                    AsyncImage(
                        model = user.avatarUrl,
                        contentDescription = stringResource(R.string.profile),
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.profile)
                    )
                }
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        HomeContent(
            uiState = HomeUiState(
                user = UserUiModel(
                    id = "1",
                    name = "John Doe",
                    email = "john@example.com",
                    avatarUrl = null,
                    displayName = "John Doe",
                    isAdmin = false
                ),
                featuredProducts = listOf(
                    // Preview data
                ),
                categories = listOf(
                    // Preview data
                )
            ),
            onEvent = {},
            onProductClick = {},
            onCategoryClick = {},
            onProfileClick = {},
            onCartClick = {}
        )
    }
}

```
### Reusable Components
```kotlin
// presentation/components/ProductCard.kt
@Composable
fun ProductCard(
    product: ProductUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            // Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                AsyncImage(
                    model = product.imageUrls.firstOrNull(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Discount Badge
                product.discountLabel?.let { discount ->
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        color = MaterialTheme.colorScheme.error,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = discount,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Stock Badge
                if (!product.isInStock) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp),
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.out_of_stock),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White
                        )
                    }
                }
            }

            // Product Info
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = product.category.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = product.ratingText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.price,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    product.originalPrice?.let { originalPrice ->
                        Text(
                            text = originalPrice,
                            style = MaterialTheme.typography.bodyMedium,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Text(
                    text = product.stockLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (product.isInStock) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProductCardPreview() {
    AppTheme {
        ProductCard(
            product = ProductUiModel(
                id = "1",
                name = "Wireless Headphones",
                description = "Premium noise cancelling headphones",
                price = "$99.99",
                originalPrice = "$129.99",
                discountLabel = "-23%",
                stock = 15,
                stockLabel = "In Stock",
                category = CategoryUiModel("1", "Electronics", null, "100 products"),
                imageUrls = listOf(),
                rating = 4.5f,
                ratingText = "4.5 (123 reviews)",
                isInStock = true,
                isOnSale = true,
                canAddToCart = true
            ),
            onClick = {}
        )
    }
}

```
### Common UI Components
```kotlin
// presentation/components/LoadingScreen.kt
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

// presentation/components/ErrorScreen.kt
@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onRetry) {
            Text(stringResource(R.string.retry))
        }
    }
}

// presentation/components/EmptyScreen.kt
@Composable
fun EmptyScreen(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

```
## Navigation
```kotlin
// presentation/navigation/NavGraph.kt
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onProductClick = { productId ->
                    navController.navigate(Screen.ProductDetail.createRoute(productId))
                },
                onCategoryClick = { categoryId ->
                    navController.navigate(Screen.ProductList.createRoute(categoryId))
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onCartClick = {
                    navController.navigate(Screen.Cart.route)
                }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(
                navArgument("productId") { type = NavType.StringType }
            )
        ) {
            ProductDetailScreen(
                onBackClick = { navController.popBackStack() },
                onCartClick = { navController.navigate(Screen.Cart.route) }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                onBackClick = { navController.popBackStack() },
                onCheckout = { navController.navigate(Screen.Checkout.route) }
            )
        }

        // More screens...
    }
}

// presentation/navigation/Screen.kt
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object ProductList : Screen("product_list/{categoryId}") {
        fun createRoute(categoryId: String) = "product_list/$categoryId"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: String) = "product_detail/$productId"
    }
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object Profile : Screen("profile")
}

```
## Theme
```kotlin
// presentation/ui/theme/Color.kt
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// presentation/ui/theme/Theme.kt
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> darkColorScheme(
            primary = Purple80,
            secondary = PurpleGrey80,
            tertiary = Pink80
        )
        else -> lightColorScheme(
            primary = Purple40,
            secondary = PurpleGrey40,
            tertiary = Pink40
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

```
## Presentation Layer Best Practices
### Luôn có Preview
```kotlin
@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ProductCardPreview() {
    AppTheme {
        ProductCard(product = sampleProduct, onClick = {})
    }
}

```
### String trong strings.xml
```xml
<!-- res/values/strings.xml -->
<resources>
    <string name="app_name">My App</string>
    <string name="home_featured_products">Featured Products</string>
    <string name="error_generic">Something went wrong</string>
    <string name="retry">Retry</string>
</resources>

```
```kotlin
// ❌ BAD
Text("Featured Products")

// ✅ GOOD
Text(stringResource(R.string.home_featured_products))
```
### Tách UI Logic
```kotlin
// ❌ BAD - Business logic in Composable
@Composable
fun ProductPrice(product: Product) {
    val price = if (product.discount > 0) {
        product.price * (1 - product.discount / 100.0)
    } else {
        product.price
    }
    Text("$$price")
}

// ✅ GOOD - Business logic in Domain/ViewModel
@Composable
fun ProductPrice(priceText: String) {
    Text(priceText)
}

```

# Domain Layer
## Domain Models (Entities)
```kotlin
// domain/model/User.kt
data class User(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String?,
    val createdAt: Date,
    val isActive: Boolean,
    val role: UserRole
) {
    // Business logic methods
    fun isAdmin(): Boolean = role == UserRole.ADMIN
    
    fun canEditProfile(): Boolean = isActive
    
    fun getDisplayName(): String = name.ifEmpty { email.substringBefore("@") }
    
    fun isEmailVerified(): Boolean {
        // Business rule: email verification logic
        return email.isNotEmpty() && email.contains("@")
    }
    
    companion object {
        fun createGuest(): User {
            return User(
                id = "guest_${System.currentTimeMillis()}",
                name = "Guest",
                email = "",
                avatarUrl = null,
                createdAt = Date(),
                isActive = true,
                role = UserRole.GUEST
            )
        }
    }
}

enum class UserRole {
    GUEST,
    USER,
    PREMIUM,
    ADMIN
}

// domain/model/Product.kt
data class Product(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val discountPercent: Int = 0,
    val stock: Int,
    val category: ProductCategory,
    val imageUrls: List<String>,
    val rating: Float,
    val reviewCount: Int
) {
    // Business logic
    fun getFinalPrice(): Double {
        return if (discountPercent > 0) {
            price * (1 - discountPercent / 100.0)
        } else {
            price
        }
    }
    
    fun isInStock(): Boolean = stock > 0
    
    fun isOnSale(): Boolean = discountPercent > 0
    
    fun getSavings(): Double = price - getFinalPrice()
    
    fun canAddToCart(quantity: Int): Boolean {
        return isInStock() && quantity <= stock
    }
    
    fun getDiscountLabel(): String? {
        return if (discountPercent > 0) {
            "-$discountPercent%"
        } else null
    }
}

data class ProductCategory(
    val id: String,
    val name: String,
    val iconUrl: String?
)

// domain/model/Order.kt
data class Order(
    val id: String,
    val userId: String,
    val items: List<OrderItem>,
    val status: OrderStatus,
    val totalAmount: Double,
    val shippingAddress: Address,
    val createdAt: Date,
    val updatedAt: Date
) {
    fun getTotalItems(): Int = items.sumOf { it.quantity }
    
    fun canBeCancelled(): Boolean {
        return status == OrderStatus.PENDING || status == OrderStatus.PROCESSING
    }
    
    fun isCompleted(): Boolean = status == OrderStatus.DELIVERED
    
    fun getEstimatedDelivery(): Date {
        // Business rule: 3-5 days from order date
        val calendar = Calendar.getInstance()
        calendar.time = createdAt
        calendar.add(Calendar.DAY_OF_MONTH, 5)
        return calendar.time
    }
}

data class OrderItem(
    val productId: String,
    val productName: String,
    val quantity: Int,
    val pricePerUnit: Double
) {
    fun getSubtotal(): Double = quantity * pricePerUnit
}

enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

data class Address(
    val street: String,
    val city: String,
    val state: String,
    val zipCode: String,
    val country: String
) {
    fun getFullAddress(): String {
        return "$street, $city, $state $zipCode, $country"
    }
}

// domain/model/Result.kt
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
    object Loading : Result<Nothing>()
    
    fun isSuccess(): Boolean = this is Success
    fun isError(): Boolean = this is Error
    fun isLoading(): Boolean = this is Loading
    
    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }
    
    fun getOrDefault(default: T): T = when (this) {
        is Success -> data
        else -> default
    }
    
    fun exceptionOrNull(): Exception? = when (this) {
        is Error -> exception
        else -> null
    }
}

// domain/model/UiState.kt
sealed class UiState<out T> {
    object Idle : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : UiState<Nothing>()
}

```
## Repository Interfaces
```kotlin
// domain/repository/UserRepository.kt
interface UserRepository {
    // Queries
    suspend fun getUser(userId: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun searchUsers(query: String): Result<List<User>>
    
    // Commands
    suspend fun createUser(user: User): Result<User>
    suspend fun updateUser(user: User): Result<Unit>
    suspend fun deleteUser(userId: String): Result<Unit>
    
    // Reactive
    fun observeUser(userId: String): Flow<Result<User>>
    fun observeUsers(): Flow<Result<List<User>>>
    
    // Authentication
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User?>
}

// domain/repository/ProductRepository.kt
interface ProductRepository {
    suspend fun getProduct(productId: String): Result<Product>
    suspend fun getProducts(
        category: ProductCategory? = null,
        sortBy: ProductSortBy = ProductSortBy.POPULAR
    ): Result<List<Product>>
    suspend fun searchProducts(query: String): Result<List<Product>>
    suspend fun getFeaturedProducts(): Result<List<Product>>
    
    fun observeProduct(productId: String): Flow<Result<Product>>
    fun observeProducts(): Flow<Result<List<Product>>>
}

enum class ProductSortBy {
    POPULAR,
    PRICE_LOW_TO_HIGH,
    PRICE_HIGH_TO_LOW,
    NEWEST,
    RATING
}

// domain/repository/OrderRepository.kt
interface OrderRepository {
    suspend fun createOrder(order: Order): Result<Order>
    suspend fun getOrder(orderId: String): Result<Order>
    suspend fun getUserOrders(userId: String): Result<List<Order>>
    suspend fun cancelOrder(orderId: String): Result<Unit>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Result<Unit>
    
    fun observeUserOrders(userId: String): Flow<Result<List<Order>>>
}

// domain/repository/CartRepository.kt
interface CartRepository {
    suspend fun addToCart(productId: String, quantity: Int): Result<Unit>
    suspend fun removeFromCart(productId: String): Result<Unit>
    suspend fun updateQuantity(productId: String, quantity: Int): Result<Unit>
    suspend fun clearCart(): Result<Unit>
    suspend fun getCartItems(): Result<List<CartItem>>
    
    fun observeCart(): Flow<Result<List<CartItem>>>
}

data class CartItem(
    val product: Product,
    val quantity: Int
) {
    fun getSubtotal(): Double = product.getFinalPrice() * quantity
}

```
### Naming Convention
```
GetXxxUseCase      - Lấy dữ liệu
CreateXxxUseCase   - Tạo mới
UpdateXxxUseCase   - Cập nhật
DeleteXxxUseCase   - Xóa
ValidateXxxUseCase - Validation
```
### Base UseCase
```kotlin
// domain/usecase/base/UseCase.kt
abstract class UseCase<in Params, out T> {
    abstract suspend fun execute(params: Params): Result<T>
    
    suspend operator fun invoke(params: Params): Result<T> {
        return try {
            execute(params)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Cho use case không cần params
abstract class NoParamsUseCase<out T> {
    abstract suspend fun execute(): Result<T>
    
    suspend operator fun invoke(): Result<T> {
        return try {
            execute()
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Cho use case return Flow
abstract class FlowUseCase<in Params, out T> {
    abstract fun execute(params: Params): Flow<Result<T>>
    
    operator fun invoke(params: Params): Flow<Result<T>> {
        return execute(params).catch { e ->
            emit(Result.Error(e as Exception))
        }
    }
}

```
### User Use Cases
```kotlin
// domain/usecase/user/GetUserUseCase.kt
class GetUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : UseCase<GetUserUseCase.Params, User>() {
    
    override suspend fun execute(params: Params): Result<User> {
        return userRepository.getUser(params.userId)
    }
    
    data class Params(val userId: String)
}

// domain/usecase/user/LoginUseCase.kt
class LoginUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val validateEmailUseCase: ValidateEmailUseCase,
    private val validatePasswordUseCase: ValidatePasswordUseCase
) : UseCase<LoginUseCase.Params, User>() {
    
    override suspend fun execute(params: Params): Result<User> {
        // Validate email
        val emailValidation = validateEmailUseCase(
            ValidateEmailUseCase.Params(params.email)
        )
        if (emailValidation is Result.Error) {
            return Result.Error(ValidationException("Invalid email"))
        }
        
        // Validate password
        val passwordValidation = validatePasswordUseCase(
            ValidatePasswordUseCase.Params(params.password)
        )
        if (passwordValidation is Result.Error) {
            return Result.Error(ValidationException("Invalid password"))
        }
        
        // Proceed with login
        return userRepository.login(params.email, params.password)
    }
    
    data class Params(
        val email: String,
        val password: String
    )
}

// domain/usecase/user/UpdateUserProfileUseCase.kt
class UpdateUserProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : UseCase<UpdateUserProfileUseCase.Params, Unit>() {
    
    override suspend fun execute(params: Params): Result<Unit> {
        // Get current user
        val currentUserResult = getCurrentUserUseCase()
        if (currentUserResult is Result.Error) {
            return Result.Error(UnauthorizedException())
        }
        
        val currentUser = (currentUserResult as Result.Success).data
        
        // Business rule: Can't update if not active
        if (!currentUser.canEditProfile()) {
            return Result.Error(
                BusinessRuleException("User is not active, cannot edit profile")
            )
        }
        
        // Create updated user
        val updatedUser = currentUser.copy(
            name = params.name ?: currentUser.name,
            avatarUrl = params.avatarUrl ?: currentUser.avatarUrl
        )
        
        return userRepository.updateUser(updatedUser)
    }
    
    data class Params(
        val name: String? = null,
        val avatarUrl: String? = null
    )
}

// domain/usecase/user/ObserveUserUseCase.kt
class ObserveUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : FlowUseCase<ObserveUserUseCase.Params, User>() {
    
    override fun execute(params: Params): Flow<Result<User>> {
        return userRepository.observeUser(params.userId)
    }
    
    data class Params(val userId: String)
}

// domain/usecase/user/GetCurrentUserUseCase.kt
class GetCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) : NoParamsUseCase<User>() {
    
    override suspend fun execute(): Result<User> {
        val result = userRepository.getCurrentUser()
        return when (result) {
            is Result.Success -> {
                result.data?.let { Result.Success(it) }
                    ?: Result.Error(UnauthorizedException("No user logged in"))
            }
            is Result.Error -> result
            else -> Result.Error(UnknownException())
        }
    }
}
```
### Product Use Cases
```kotlin
// domain/usecase/product/GetProductsUseCase.kt
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : UseCase<GetProductsUseCase.Params, List<Product>>() {
    
    override suspend fun execute(params: Params): Result<List<Product>> {
        val result = productRepository.getProducts(
            category = params.category,
            sortBy = params.sortBy
        )
        
        return when (result) {
            is Result.Success -> {
                var products = result.data
                
                // Apply filters
                if (params.onlyInStock) {
                    products = products.filter { it.isInStock() }
                }
                
                if (params.onlyOnSale) {
                    products = products.filter { it.isOnSale() }
                }
                
                if (params.maxPrice != null) {
                    products = products.filter { it.getFinalPrice() <= params.maxPrice }
                }
                
                Result.Success(products)
            }
            is Result.Error -> result
            else -> Result.Error(UnknownException())
        }
    }
    
    data class Params(
        val category: ProductCategory? = null,
        val sortBy: ProductSortBy = ProductSortBy.POPULAR,
        val onlyInStock: Boolean = false,
        val onlyOnSale: Boolean = false,
        val maxPrice: Double? = null
    )
}

// domain/usecase/product/SearchProductsUseCase.kt
class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) : UseCase<SearchProductsUseCase.Params, List<Product>>() {
    
    override suspend fun execute(params: Params): Result<List<Product>> {
        // Validation
        if (params.query.length < 2) {
            return Result.Error(
                ValidationException("Search query must be at least 2 characters")
            )
        }
        
        return productRepository.searchProducts(params.query)
    }
    
    data class Params(val query: String)
}
```
### Cart Use Cases
```kotlin
// domain/usecase/cart/AddToCartUseCase.kt
class AddToCartUseCase @Inject constructor(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) : UseCase<AddToCartUseCase.Params, Unit>() {
    
    override suspend fun execute(params: Params): Result<Unit> {
        // Get product details
        val productResult = productRepository.getProduct(params.productId)
        if (productResult is Result.Error) {
            return Result.Error(NotFoundException("Product not found"))
        }
        
        val product = (productResult as Result.Success).data
        
        // Business rule: Check stock
        if (!product.canAddToCart(params.quantity)) {
            return Result.Error(
                BusinessRuleException("Not enough stock. Available: ${product.stock}")
            )
        }
        
        // Add to cart
        return cartRepository.addToCart(params.productId, params.quantity)
    }
    
    data class Params(
        val productId: String,
        val quantity: Int = 1
    )
}

// domain/usecase/cart/GetCartTotalUseCase.kt
class GetCartTotalUseCase @Inject constructor(
    private val cartRepository: CartRepository
) : NoParamsUseCase<CartTotal>() {
    
    override suspend fun execute(): Result<CartTotal> {
        val cartResult = cartRepository.getCartItems()
        
        return when (cartResult) {
            is Result.Success -> {
                val items = cartResult.data
                
                val subtotal = items.sumOf { it.getSubtotal() }
                val tax = subtotal * 0.1  // 10% tax
                val shipping = if (subtotal > 50.0) 0.0 else 5.0
                val total = subtotal + tax + shipping
                
                Result.Success(
                    CartTotal(
                        subtotal = subtotal,
                        tax = tax,
                        shipping = shipping,
                        total = total,
                        itemCount = items.sumOf { it.quantity }
                    )
                )
            }
            is Result.Error -> cartResult
            else -> Result.Error(UnknownException())
        }
    }
}

data class CartTotal(
    val subtotal: Double,
    val tax: Double,
    val shipping: Double,
    val total: Double,
    val itemCount: Int
)
```
### Order Use Cases
```kotlin
// domain/usecase/order/CreateOrderUseCase.kt
class CreateOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository,
    private val cartRepository: CartRepository,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getCartTotalUseCase: GetCartTotalUseCase
) : UseCase<CreateOrderUseCase.Params, Order>() {
    
    override suspend fun execute(params: Params): Result<Order> {
        // Get current user
        val userResult = getCurrentUserUseCase()
        if (userResult is Result.Error) {
            return Result.Error(UnauthorizedException())
        }
        val user = (userResult as Result.Success).data
        
        // Get cart items
        val cartResult = cartRepository.getCartItems()
        if (cartResult is Result.Error) {
            return Result.Error(cartResult.exception)
        }
        val cartItems = (cartResult as Result.Success).data
        
        // Business rule: Cart must not be empty
        if (cartItems.isEmpty()) {
            return Result.Error(BusinessRuleException("Cart is empty"))
        }
        
        // Get cart total
        val totalResult = getCartTotalUseCase()
        if (totalResult is Result.Error) {
            return Result.Error(totalResult.exception)
        }
        val cartTotal = (totalResult as Result.Success).data
        
        // Create order
        val order = Order(
            id = UUID.randomUUID().toString(),
            userId = user.id,
            items = cartItems.map {
                OrderItem(
                    productId = it.product.id,
                    productName = it.product.name,
                    quantity = it.quantity,
                    pricePerUnit = it.product.getFinalPrice()
                )
            },
            status = OrderStatus.PENDING,
            totalAmount = cartTotal.total,
            shippingAddress = params.shippingAddress,
            createdAt = Date(),
            updatedAt = Date()
        )
        
        val createResult = orderRepository.createOrder(order)
        
        // Clear cart if order created successfully
        if (createResult is Result.Success) {
            cartRepository.clearCart()
        }
        
        return createResult
    }
    
    data class Params(val shippingAddress: Address)
}

// domain/usecase/order/CancelOrderUseCase.kt
class CancelOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) : UseCase<CancelOrderUseCase.Params, Unit>() {
    
    override suspend fun execute(params: Params): Result<Unit> {
        // Get order
        val orderResult = orderRepository.getOrder(params.orderId)
        if (orderResult is Result.Error) {
            return Result.Error(NotFoundException("Order not found"))
        }
        
        val order = (orderResult as Result.Success).data
        
        // Business rule: Can only cancel pending/processing orders
        if (!order.canBeCancelled()) {
            return Result.Error(
                BusinessRuleException("Cannot cancel order with status: ${order.status}")
            )
        }
        
        return orderRepository.cancelOrder(params.orderId)
    }
    
    data class Params(val orderId: String)
}
```
### Validation Use Cases
```kotlin
// domain/usecase/validation/ValidateEmailUseCase.kt
class ValidateEmailUseCase @Inject constructor() 
    : UseCase<ValidateEmailUseCase.Params, Boolean>() {
    
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    
    override suspend fun execute(params: Params): Result<Boolean> {
        val isValid = params.email.matches(emailRegex)
        return if (isValid) {
            Result.Success(true)
        } else {
            Result.Error(ValidationException("Invalid email format"))
        }
    }
    
    data class Params(val email: String)
}

// domain/usecase/validation/ValidatePasswordUseCase.kt
class ValidatePasswordUseCase @Inject constructor() 
    : UseCase<ValidatePasswordUseCase.Params, Boolean>() {
    
    override suspend fun execute(params: Params): Result<Boolean> {
        val password = params.password
        
        return when {
            password.length < 8 -> 
                Result.Error(ValidationException("Password must be at least 8 characters"))
            !password.any { it.isUpperCase() } -> 
                Result.Error(ValidationException("Password must contain uppercase letter"))
            !password.any { it.isLowerCase() } -> 
                Result.Error(ValidationException("Password must contain lowercase letter"))
            !password.any { it.isDigit() } -> 
                Result.Error(ValidationException("Password must contain a number"))
            else -> Result.Success(true)
        }
    }
    
    data class Params(val password: String)
}
```
## Custom Exceptions
```kotlin
// domain/exception/DomainException.kt
sealed class DomainException(message: String) : Exception(message)

class NotFoundException(message: String = "Resource not found") : DomainException(message)

class UnauthorizedException(message: String = "Unauthorized access") : DomainException(message)

class ValidationException(message: String) : DomainException(message)

class BusinessRuleException(message: String) : DomainException(message)

class NetworkException(message: String = "Network error") : DomainException(message)

class UnknownException(message: String = "Unknown error occurred") : DomainException(message)
```
## Domain Module (Hilt)
```kotlin
// di/DomainModule.kt
@Module
@InstallIn(ViewModelComponent::class)
object DomainModule {
    // Use cases are auto-injected by Hilt
    // No need to provide them manually if using @Inject constructor
}
```
## Domain Layer Best Practices
### Pure Kotlin Only
```kotlin
// ❌ BAD - Android dependency
import android.content.Context

class GetUserUseCase(private val context: Context)

// ✅ GOOD - Pure Kotlin
class GetUserUseCase(private val userRepository: UserRepository)
```
### Single Responsibility
```kotlin
// ❌ BAD - Doing too much
class UserUseCase {
    fun login() { }
    fun register() { }
    fun updateProfile() { }
    fun deleteAccount() { }
}

// ✅ GOOD - One responsibility each
class LoginUseCase { }
class RegisterUseCase { }
class UpdateProfileUseCase { }
class DeleteAccountUseCase { }
```
### Testable
```kotlin
class GetUserUseCaseTest {
    private lateinit var useCase: GetUserUseCase
    private lateinit var repository: UserRepository
    
    @Before
    fun setup() {
        repository = mockk()  // Easy to mock
        useCase = GetUserUseCase(repository)
    }
    
    @Test
    fun `getUser returns success when repository succeeds`() = runTest {
        // Given
        val userId = "123"
        val user = User(/*...*/)
        coEvery { repository.getUser(userId) } returns Result.Success(user)
        
        // When
        val result = useCase(GetUserUseCase.Params(userId))
        
        // Then
        assertTrue(result is Result.Success)
        assertEquals(user, (result as Result.Success).data)
    }
}
```
## 📊 Domain Layer Flow
```
ViewModel
    ↓
UseCase (Business Logic)
    ↓
Repository Interface
    ↓
[Data Layer implements this]
Domain Layer không biết gì về:
- Android (Context, Activity, Fragment)
- UI (Compose, XML)
- Database (Room)
- Network (Retrofit)
```

# Data Layer
## Repository Implementation
```kotlin
// domain/repository/UserRepository.kt (Interface trong Domain)
interface UserRepository {
    suspend fun getUser(userId: String): Result<User>
    suspend fun getAllUsers(): Result<List<User>>
    suspend fun updateUser(user: User): Result<Unit>
    fun observeUser(userId: String): Flow<User>
}

// data/repository/UserRepositoryImpl.kt (Implementation trong Data)
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val networkChecker: NetworkChecker,
    private val userMapper: UserMapper
) : UserRepository {

    override suspend fun getUser(userId: String): Result<User> {
        return try {
            // Strategy: Network First, then Cache
            if (networkChecker.isConnected()) {
                // Lấy từ API
                val userDto = remoteDataSource.getUser(userId)
                
                // Convert DTO -> Domain Model
                val user = userMapper.toDomain(userDto)
                
                // Lưu vào local database
                localDataSource.saveUser(userMapper.toEntity(user))
                
                Result.Success(user)
            } else {
                // Không có mạng -> lấy từ cache
                val userEntity = localDataSource.getUser(userId)
                Result.Success(userMapper.toDomain(userEntity))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val usersDto = remoteDataSource.getAllUsers()
            val users = usersDto.map { userMapper.toDomain(it) }
            
            // Cache vào local
            localDataSource.saveUsers(users.map { userMapper.toEntity(it) })
            
            Result.Success(users)
        } catch (e: Exception) {
            // Fallback to local data
            try {
                val localUsers = localDataSource.getAllUsers()
                Result.Success(localUsers.map { userMapper.toDomain(it) })
            } catch (localError: Exception) {
                Result.Error(e)
            }
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val userDto = userMapper.toDto(user)
            remoteDataSource.updateUser(userDto)
            
            // Update local
            localDataSource.updateUser(userMapper.toEntity(user))
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun observeUser(userId: String): Flow<User> {
        return localDataSource.observeUser(userId)
            .map { userMapper.toDomain(it) }
    }
}
```
## Remote Data Source (API)
### Retrofit API Service
```kotlin
// data/remote/api/UserApiService.kt
interface UserApiService {
    @GET("users/{id}")
    suspend fun getUser(@Path("id") userId: String): UserDto
    
    @GET("users")
    suspend fun getAllUsers(): List<UserDto>
    
    @POST("users")
    suspend fun createUser(@Body user: UserDto): UserDto
    
    @PUT("users/{id}")
    suspend fun updateUser(
        @Path("id") userId: String,
        @Body user: UserDto
    ): UserDto
    
    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: String): Response<Unit>
    
    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): List<UserDto>
}
```
### DTO (Data Transfer Object)
```kotlin
// data/remote/dto/UserDto.kt
@JsonClass(generateAdapter = true)  // Moshi
// hoặc dùng @Serializable với Kotlinx Serialization
data class UserDto(
    @Json(name = "user_id")
    val userId: String,
    
    @Json(name = "full_name")
    val fullName: String,
    
    @Json(name = "email_address")
    val email: String,
    
    @Json(name = "avatar_url")
    val avatarUrl: String?,
    
    @Json(name = "created_at")
    val createdAt: String,  // ISO 8601 format
    
    @Json(name = "is_active")
    val isActive: Boolean,
    
    @Json(name = "metadata")
    val metadata: Map<String, Any>? = null
)

// Response wrapper
data class ApiResponse<T>(
    @Json(name = "success")
    val success: Boolean,
    
    @Json(name = "data")
    val data: T?,
    
    @Json(name = "error")
    val error: ErrorDto?
)

data class ErrorDto(
    @Json(name = "code")
    val code: String,
    
    @Json(name = "message")
    val message: String
)
```
### Remote DataSource
```kotlin
// data/remote/datasource/UserRemoteDataSource.kt
class UserRemoteDataSource @Inject constructor(
    private val apiService: UserApiService,
    private val errorHandler: ApiErrorHandler
) {
    suspend fun getUser(userId: String): UserDto {
        return try {
            apiService.getUser(userId)
        } catch (e: Exception) {
            throw errorHandler.handle(e)
        }
    }
    
    suspend fun getAllUsers(): List<UserDto> {
        return try {
            apiService.getAllUsers()
        } catch (e: Exception) {
            throw errorHandler.handle(e)
        }
    }
    
    suspend fun updateUser(userDto: UserDto): UserDto {
        return try {
            apiService.updateUser(userDto.userId, userDto)
        } catch (e: Exception) {
            throw errorHandler.handle(e)
        }
    }
}

// Error Handler
class ApiErrorHandler @Inject constructor() {
    fun handle(exception: Exception): Exception {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    401 -> UnauthorizedException()
                    404 -> NotFoundException()
                    500 -> ServerException()
                    else -> NetworkException(exception.message())
                }
            }
            is IOException -> NoInternetException()
            else -> UnknownException(exception)
        }
    }
}
```
## Local Data Source (Database)
### Room Entity
```kotlin
// data/local/entity/UserEntity.kt
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    
    @ColumnInfo(name = "full_name")
    val fullName: String,
    
    @ColumnInfo(name = "email")
    val email: String,
    
    @ColumnInfo(name = "avatar_url")
    val avatarUrl: String?,
    
    @ColumnInfo(name = "created_at")
    val createdAt: Long,  // Timestamp
    
    @ColumnInfo(name = "is_active")
    val isActive: Boolean,
    
    @ColumnInfo(name = "last_updated")
    val lastUpdated: Long = System.currentTimeMillis()
)

// Relation Example
@Entity(
    tableName = "user_posts",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["user_id"],
            childColumns = ["author_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PostEntity(
    @PrimaryKey
    val postId: String,
    
    @ColumnInfo(name = "author_id")
    val authorId: String,
    
    val title: String,
    val content: String
)

// Embedded object
data class UserWithPosts(
    @Embedded val user: UserEntity,
    
    @Relation(
        parentColumn = "user_id",
        entityColumn = "author_id"
    )
    val posts: List<PostEntity>
)
```
### DAO (Data Access Object)
```kotlin
// data/local/dao/UserDao.kt
@Dao
interface UserDao {
    // Query
    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUser(userId: String): UserEntity?
    
    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE is_active = 1")
    suspend fun getActiveUsers(): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE user_id = :userId")
    fun observeUser(userId: String): Flow<UserEntity>
    
    @Query("SELECT * FROM users WHERE full_name LIKE '%' || :query || '%'")
    suspend fun searchUsers(query: String): List<UserEntity>
    
    // Insert
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    // Update
    @Update
    suspend fun updateUser(user: UserEntity)
    
    // Delete
    @Delete
    suspend fun deleteUser(user: UserEntity)
    
    @Query("DELETE FROM users WHERE user_id = :userId")
    suspend fun deleteUserById(userId: String)
    
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
    
    // Transaction example
    @Transaction
    @Query("SELECT * FROM users WHERE user_id = :userId")
    suspend fun getUserWithPosts(userId: String): UserWithPosts?
}
```
### Room Database
```kotlin
// data/local/database/AppDatabase.kt
@Database(
    entities = [
        UserEntity::class,
        PostEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun postDao(): PostDao
}

// Type Converters cho complex types
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.joinToString(",")
    }
    
    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.split(",")
    }
}
```
### Local DataSource
```kotlin
// data/local/datasource/UserLocalDataSource.kt
class UserLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val cacheManager: CacheManager
) {
    suspend fun getUser(userId: String): UserEntity {
        // Check MMKV cache first (fast)
        cacheManager.getUser(userId)?.let { return it }
        
        // Then check Room database
        return userDao.getUser(userId)
            ?: throw NotFoundException("User not found in local database")
    }
    
    suspend fun getAllUsers(): List<UserEntity> {
        return userDao.getAllUsers()
    }
    
    suspend fun saveUser(user: UserEntity) {
        userDao.insertUser(user)
        cacheManager.saveUser(user)  // Also save to MMKV
    }
    
    suspend fun saveUsers(users: List<UserEntity>) {
        userDao.insertUsers(users)
    }
    
    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
        cacheManager.saveUser(user)
    }
    
    fun observeUser(userId: String): Flow<UserEntity> {
        return userDao.observeUser(userId)
    }
    
    suspend fun deleteUser(userId: String) {
        userDao.deleteUserById(userId)
        cacheManager.removeUser(userId)
    }
}
```
## Cache Manager (MMKV)
```kotlin
// data/cache/CacheManager.kt
class CacheManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val mmkv: MMKV by lazy {
        MMKV.initialize(context)
        MMKV.defaultMMKV()
    }
    
    private val gson = Gson()
    
    // User cache
    fun saveUser(user: UserEntity) {
        val json = gson.toJson(user)
        mmkv.encode("user_${user.userId}", json)
    }
    
    fun getUser(userId: String): UserEntity? {
        val json = mmkv.decodeString("user_$userId") ?: return null
        return try {
            gson.fromJson(json, UserEntity::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun removeUser(userId: String) {
        mmkv.remove("user_$userId")
    }
    
    // Generic cache
    fun <T> saveObject(key: String, obj: T) {
        val json = gson.toJson(obj)
        mmkv.encode(key, json)
    }
    
    inline fun <reified T> getObject(key: String): T? {
        val json = mmkv.decodeString(key) ?: return null
        return try {
            gson.fromJson(json, T::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    // Cache with expiration
    fun saveWithExpiry(key: String, value: String, expiryMinutes: Long) {
        mmkv.encode(key, value)
        mmkv.encode("${key}_expiry", System.currentTimeMillis() + (expiryMinutes * 60 * 1000))
    }
    
    fun getWithExpiry(key: String): String? {
        val expiry = mmkv.decodeLong("${key}_expiry", 0)
        return if (System.currentTimeMillis() < expiry) {
            mmkv.decodeString(key)
        } else {
            mmkv.remove(key)
            mmkv.remove("${key}_expiry")
            null
        }
    }
    
    fun clearAll() {
        mmkv.clearAll()
    }
}
```
## Mapper (DTO ↔ Domain ↔ Entity)
```kotlin
// data/mapper/UserMapper.kt
class UserMapper @Inject constructor() {
    
    // DTO -> Domain Model
    fun toDomain(dto: UserDto): User {
        return User(
            id = dto.userId,
            name = dto.fullName,
            email = dto.email,
            avatarUrl = dto.avatarUrl,
            createdAt = parseIsoDate(dto.createdAt),
            isActive = dto.isActive
        )
    }
    
    // Domain Model -> DTO
    fun toDto(domain: User): UserDto {
        return UserDto(
            userId = domain.id,
            fullName = domain.name,
            email = domain.email,
            avatarUrl = domain.avatarUrl,
            createdAt = formatIsoDate(domain.createdAt),
            isActive = domain.isActive
        )
    }
    
    // Entity -> Domain Model
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.userId,
            name = entity.fullName,
            email = entity.email,
            avatarUrl = entity.avatarUrl,
            createdAt = Date(entity.createdAt),
            isActive = entity.isActive
        )
    }
    
    // Domain Model -> Entity
    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            userId = domain.id,
            fullName = domain.name,
            email = domain.email,
            avatarUrl = domain.avatarUrl,
            createdAt = domain.createdAt.time,
            isActive = domain.isActive
        )
    }
    
    private fun parseIsoDate(iso: String): Date {
        // Parse ISO 8601 format
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(iso) ?: Date()
    }
    
    private fun formatIsoDate(date: Date): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(date)
    }
}
```
## Hilt Modules cho Data Layer
```kotlin
// di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer YOUR_TOKEN")
                    .build()
                chain.proceed(request)
            }
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }
    
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.example.com/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }
}

// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
}

// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
}
```
## 📊 Data Flow Diagram
```
API (JSON)
    ↓
UserDto (DTO)
    ↓
[Mapper]
    ↓
User (Domain Model) → UseCase → ViewModel → UI
    ↓
[Mapper]
    ↓
UserEntity (Room)
    ↓
Local Database
```
## Best Practices
```
Single Source of Truth: Room Database là nguồn duy nhất
Offline First: Cache local trước, sync sau
Error Handling: Xử lý lỗi ở Repository layer
Mapping: Luôn tách biệt DTO, Domain, Entity
Cache Strategy: MMKV cho cache nhanh, Room cho persistent storage
```

# DI Layer
## Setup Hilt
## Application Class
```kotlin
// MyApplication.kt
@HiltAndroidApp
class MyApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize libraries
        Timber.plant(Timber.DebugTree())
        
        // Initialize MMKV
        MMKV.initialize(this)
        
        // StrictMode for development
        if (BuildConfig.DEBUG) {
            enableStrictMode()
        }
    }
    
    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
        
        StrictMode.setVmPolicy(
            StrictMode.VmPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )
    }
}
```
AndroidManifest.xml:
```xml
<application
    android:name=".MyApplication"
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    ...
    />
```
## MainActivity
```kotlin
// MainActivity.kt
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            AppTheme {
                AppNavGraph()
            }
        }
    }
}
```
## Hilt Modules Chi Tiết
### Network Module
```kotlin
// di/NetworkModule.kt
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    
    private const val BASE_URL = "https://api.example.com/"
    private const val TIMEOUT = 30L
    
    // Provide HttpLoggingInterceptor
    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }
    
    // Provide AuthInterceptor
    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager
    ): Interceptor {
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
            
            // Add auth token if available
            tokenManager.getToken()?.let { token ->
                request.addHeader("Authorization", "Bearer $token")
            }
            
            // Add common headers
            request.addHeader("Accept", "application/json")
            request.addHeader("Content-Type", "application/json")
            request.addHeader("User-Agent", "Android/${BuildConfig.VERSION_NAME}")
            
            chain.proceed(request.build())
        }
    }
    
    // Provide OkHttpClient
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            // Certificate pinning (optional)
            .certificatePinner(
                CertificatePinner.Builder()
                    .add("api.example.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
                    .build()
            )
            .build()
    }
    
    // Provide Moshi
    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .add(DateAdapter())  // Custom date adapter
            .build()
    }
    
    // Provide Retrofit
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
    
    // Provide API Services
    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideProductApiService(retrofit: Retrofit): ProductApiService {
        return retrofit.create(ProductApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideOrderApiService(retrofit: Retrofit): OrderApiService {
        return retrofit.create(OrderApiService::class.java)
    }
    
    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }
}

// Custom Date Adapter for Moshi
class DateAdapter {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
    
    @FromJson
    fun fromJson(json: String): Date {
        return dateFormat.parse(json) ?: Date()
    }
    
    @ToJson
    fun toJson(date: Date): String {
        return dateFormat.format(date)
    }
}
```
### Database Module
```kotlin
// di/DatabaseModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    // Provide Room Database
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()  // Only in development
            .build()
    }
    
    // Provide DAOs
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    
    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }
    
    @Provides
    fun provideOrderDao(database: AppDatabase): OrderDao {
        return database.orderDao()
    }
    
    @Provides
    fun provideCartDao(database: AppDatabase): CartDao {
        return database.cartDao()
    }
    
    // Database Migrations
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "ALTER TABLE users ADD COLUMN phone_number TEXT"
            )
        }
    }
    
    private val MIGRATION_2_3 = object : Migration(2, 3) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL(
                "CREATE TABLE IF NOT EXISTS wishlist (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                "user_id TEXT NOT NULL, " +
                "product_id TEXT NOT NULL)"
            )
        }
    }
}
```
### Repository Module
```kotlin
// di/RepositoryModule.kt
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    // Bind Repository Implementations to Interfaces
    // Use @Binds for interface binding (more efficient than @Provides)
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository
    
    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepositoryImpl
    ): OrderRepository
    
    @Binds
    @Singleton
    abstract fun bindCartRepository(
        impl: CartRepositoryImpl
    ): CartRepository
    
    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository
}

// Note: Nếu cần thêm dependencies khi bind, dùng @Provides
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModuleProvides {
    
    @Provides
    @Singleton
    fun provideSpecialRepository(
        apiService: ApiService,
        database: AppDatabase,
        mapper: Mapper
    ): SpecialRepository {
        return SpecialRepositoryImpl(apiService, database, mapper)
    }
}
```
### DataSource Module
```kotlin
// di/DataSourceModule.kt
@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {
    
    // Remote DataSources
    @Provides
    @Singleton
    fun provideUserRemoteDataSource(
        apiService: UserApiService,
        errorHandler: ApiErrorHandler
    ): UserRemoteDataSource {
        return UserRemoteDataSource(apiService, errorHandler)
    }
    
    @Provides
    @Singleton
    fun provideProductRemoteDataSource(
        apiService: ProductApiService,
        errorHandler: ApiErrorHandler
    ): ProductRemoteDataSource {
        return ProductRemoteDataSource(apiService, errorHandler)
    }
    
    // Local DataSources
    @Provides
    @Singleton
    fun provideUserLocalDataSource(
        userDao: UserDao,
        cacheManager: CacheManager
    ): UserLocalDataSource {
        return UserLocalDataSource(userDao, cacheManager)
    }
    
    @Provides
    @Singleton
    fun provideProductLocalDataSource(
        productDao: ProductDao,
        cacheManager: CacheManager
    ): ProductLocalDataSource {
        return ProductLocalDataSource(productDao, cacheManager)
    }
}
```
### Util Module
```kotlin
// di/UtilModule.kt
@Module
@InstallIn(SingletonComponent::class)
object UtilModule {
    
    // Provide Gson (if using Gson instead of Moshi)
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .setPrettyPrinting()
            .create()
    }
    
    // Provide CacheManager (MMKV)
    @Provides
    @Singleton
    fun provideCacheManager(
        @ApplicationContext context: Context
    ): CacheManager {
        return CacheManager(context)
    }
    
    // Provide TokenManager
    @Provides
    @Singleton
    fun provideTokenManager(
        cacheManager: CacheManager
    ): TokenManager {
        return TokenManager(cacheManager)
    }
    
    // Provide NetworkChecker
    @Provides
    @Singleton
    fun provideNetworkChecker(
        @ApplicationContext context: Context
    ): NetworkChecker {
        return NetworkChecker(context)
    }
    
    // Provide DateFormatter
    @Provides
    @Singleton
    fun provideDateFormatter(): DateFormatter {
        return DateFormatter()
    }
    
    // Provide ApiErrorHandler
    @Provides
    @Singleton
    fun provideApiErrorHandler(): ApiErrorHandler {
        return ApiErrorHandler()
    }
    
    // Provide Dispatchers
    @Provides
    @IoDispatcher
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
    
    @Provides
    @MainDispatcher
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main
    
    @Provides
    @DefaultDispatcher
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

// Qualifiers for Dispatchers
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DefaultDispatcher
```
### App Module (Other Dependencies)
```kotlin
// di/AppModule.kt
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Provide Application Context
    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }
    
    // Provide SharedPreferences
    @Provides
    @Singleton
    fun provideSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        return context.getSharedPreferences(
            "app_preferences",
            Context.MODE_PRIVATE
        )
    }
    
    // Provide Resources
    @Provides
    fun provideResources(
        @ApplicationContext context: Context
    ): Resources {
        return context.resources
    }
    
    // Provide WorkManager
    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager {
        return WorkManager.getInstance(context)
    }
}
```
## Qualifiers (Phân biệt Dependencies)
```kotlin
// di/Qualifiers.kt
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RemoteDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class LocalDataSource

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CacheDataSource

// Usage in Module
@Module
@InstallIn(SingletonComponent::class)
object DataSourceQualifierModule {
    
    @Provides
    @Singleton
    @RemoteDataSource
    fun provideRemoteUserDataSource(
        apiService: UserApiService
    ): UserDataSource {
        return UserRemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    @LocalDataSource
    fun provideLocalUserDataSource(
        dao: UserDao
    ): UserDataSource {
        return UserLocalDataSource(dao)
    }
    
    @Provides
    @Singleton
    @CacheDataSource
    fun provideCacheUserDataSource(
        mmkv: MMKV
    ): UserDataSource {
        return UserCacheDataSource(mmkv)
    }
}

// Usage in Repository
class UserRepositoryImpl @Inject constructor(
    @RemoteDataSource private val remoteDataSource: UserDataSource,
    @LocalDataSource private val localDataSource: UserDataSource,
    @CacheDataSource private val cacheDataSource: UserDataSource
) : UserRepository {
    // ...
}
```
## Scopes (Lifecycle Management)
### Available Scopes
```kotlin
// Singleton - Lives throughout app lifecycle
@InstallIn(SingletonComponent::class)

// ViewModel - Lives as long as ViewModel
@InstallIn(ViewModelComponent::class)

// Activity - Lives as long as Activity
@InstallIn(ActivityComponent::class)

// Fragment - Lives as long as Fragment
@InstallIn(FragmentComponent::class)

// Service - Lives as long as Service
@InstallIn(ServiceComponent::class)
```
### Scope Examples
```kotlin
// Singleton scope - Shared across entire app
@Module
@InstallIn(SingletonComponent::class)
object SingletonScopeModule {
    
    @Provides
    @Singleton
    fun provideDatabase(): AppDatabase {
        // Created once, lives forever
    }
}

// ViewModel scope - New instance per ViewModel
@Module
@InstallIn(ViewModelComponent::class)
object ViewModelScopeModule {
    
    @Provides
    fun provideUseCase(
        repository: Repository
    ): UseCase {
        // New instance for each ViewModel
        return UseCase(repository)
    }
}

// Activity scope - New instance per Activity
@Module
@InstallIn(ActivityComponent::class)
object ActivityScopeModule {
    
    @Provides
    fun provideAnalytics(
        @ActivityContext context: Context
    ): Analytics {
        // New instance for each Activity
        return Analytics(context)
    }
}
```
## ViewModel Injection
```kotlin
// presentation/screen/home/HomeViewModel.kt
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserUseCase: GetCurrentUserUseCase,
    private val getProductsUseCase: GetFeaturedProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val productMapper: ProductUiMapper
) : ViewModel() {
    
    // Hilt automatically injects all dependencies
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch(ioDispatcher) {
            // Use injected dependencies
        }
    }
}

// In Composable - No manual creation needed
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()  // Hilt provides ViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // UI code
}
```
## Repository Injection
```kotlin
// data/repository/UserRepositoryImpl.kt
class UserRepositoryImpl @Inject constructor(
    private val remoteDataSource: UserRemoteDataSource,
    private val localDataSource: UserLocalDataSource,
    private val networkChecker: NetworkChecker,
    private val userMapper: UserMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserRepository {
    
    // All dependencies auto-injected by Hilt
    
    override suspend fun getUser(userId: String): Result<User> = 
        withContext(ioDispatcher) {
            // Implementation
        }
}
```
## Testing with Hilt
### Test Module
```kotlin
// di/TestAppModule.kt
@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]  // Replace production module
)
object TestAppModule {

    @Provides
    @Singleton
    fun provideTestDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).build()
    }

    @Provides
    @Singleton
    fun provideFakeApiService(): UserApiService {
        return FakeUserApiService()
    }
}
```
### Test Example
```kotlin
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class UserRepositoryTest {
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    
    @Inject
    lateinit var repository: UserRepository
    
    @Inject
    lateinit var database: AppDatabase
    
    @Before
    fun setup() {
        hiltRule.inject()
    }
    
    @Test
    fun getUser_returnsSuccess() = runTest {
        // Given
        val userId = "123"
        
        // When
        val result = repository.getUser(userId)
        
        // Then
        assertTrue(result is Result.Success)
    }
    
    @After
    fun tearDown() {
        database.close()
    }
}
```
## Advanced: Named Provides
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NamedModule {
    
    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String = "https://api.example.com/"
    
    @Provides
    @Singleton
    @Named("ApiKey")
    fun provideApiKey(): String = BuildConfig.API_KEY
    
    @Provides
    @Singleton
    @Named("Timeout")
    fun provideTimeout(): Long = 30L
}

// Usage
class ApiClient @Inject constructor(
    @Named("BaseUrl") private val baseUrl: String,
    @Named("ApiKey") private val apiKey: String,
    @Named("Timeout") private val timeout: Long
) {
    // Use named dependencies
}
```
## Hilt Entry Points (For non-Hilt classes)
```kotlin
// For classes that Hilt can't inject into
@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepositoryEntryPoint {
    fun userRepository(): UserRepository
}

// Usage in non-Hilt class
class CustomClass(context: Context) {
    
    private val appContext = context.applicationContext
    
    private val repository: UserRepository by lazy {
        val hiltEntryPoint = EntryPointAccessors.fromApplication(
            appContext,
            RepositoryEntryPoint::class.java
        )
        hiltEntryPoint.userRepository()
    }
}
```

---

## Complete DI Flow Diagram
```
@HiltAndroidApp (Application)
        ↓
@InstallIn(SingletonComponent::class)
        ↓
    Modules provide dependencies
        ↓
@AndroidEntryPoint (Activity)
        ↓
@HiltViewModel injects into ViewModel
        ↓
ViewModel constructor receives dependencies
        ↓
hiltViewModel() in Composable gets ViewModel
```
## DI Layer Best Practices
### Module Organization
```kotlin
// ❌ BAD - One giant module
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    // 100+ provides methods
}

// ✅ GOOD - Separate by concern
NetworkModule.kt
DatabaseModule.kt
RepositoryModule.kt
UtilModule.kt
```
### Use @Binds for Interfaces
```kotlin
// ❌ BAD - Less efficient
@Provides
@Singleton
fun provideUserRepository(impl: UserRepositoryImpl): UserRepository {
    return impl
}

// ✅ GOOD - More efficient
@Binds
@Singleton
abstract fun bindUserRepository(
    impl: UserRepositoryImpl
): UserRepository
```
### Proper Scoping
```kotlin
// ❌ BAD - Everything Singleton
@Provides
@Singleton
fun provideViewModel(): ViewModel

// ✅ GOOD - Correct scope
@Provides  // No @Singleton
fun provideViewModel(): ViewModel {
    return ViewModel()
}
```
### Use Qualifiers for Multiple Implementations
```kotlin
// ❌ BAD - Can't distinguish
@Provides
fun provideDataSource1(): DataSource
@Provides
fun provideDataSource2(): DataSource

// ✅ GOOD - Use qualifiers
@Provides
@RemoteDataSource
fun provideRemoteDataSource(): DataSource

@Provides
@LocalDataSource
fun provideLocalDataSource(): DataSource
```

---

## 📊 Dependency Graph Example
```
Application
    ↓
NetworkModule → Retrofit → ApiService
    ↓
DataSourceModule → RemoteDataSource
    ↓                      ↓
DatabaseModule → LocalDataSource
    ↓                      ↓
RepositoryModule → Repository
    ↓
DomainModule → UseCase
    ↓
ViewModel
    ↓
UI (Composable)
```


### DI Module Pattern

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://phimapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
```

### Color Guidelines

- **Primary Green**: `Color(0xFF111d32)` - Main brand color
- **Accent Green**: `Color(0xFF8b5cf6)` - Secondary brand color
- **Text Colors**: Black for primary, Gray for secondary
- **Use MaterialTheme colors** when possible, fallback to hardcoded colors

### Modifier Chaining

```kotlin
// Preferred: Each modifier on new line for readability
Card(
    modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .clickable { onClick() },
    colors = CardDefaults.cardColors(containerColor = Color.White)
)

// Avoid chaining on same line when complex
```

### Documentation

- Add KDoc comments for public API (ViewModels, Screens, UseCases)
- No comments for implementation details unless complex
- Use `//` for inline comments, not block comments

## Testing Guidelines

- Place unit tests in `app/src/test/java/`
- Place instrumented tests in `app/src/androidTest/java/`
- Use JUnit 4 for unit tests
- Mock dependencies with mocking frameworks compatible with Kotlin
- Test naming: `methodName_condition_expectedResult`

## Common Tasks

### Adding a New Screen

1. Create screen composable in `presentation/screenname/`
2. Create ViewModel with `@HiltViewModel` annotation
3. Create/extend UseCases in `domain/usecase/`
4. Add navigation route in `MainActivity.kt`
5. Add bottom nav item if needed in `AppDestinations`

### Adding a New UseCase

1. Create class in `domain/usecase/`
2. Inject repository in constructor
3. Use `@Inject constructor` annotation
4. Implement `operator fun invoke()` for callable syntax
