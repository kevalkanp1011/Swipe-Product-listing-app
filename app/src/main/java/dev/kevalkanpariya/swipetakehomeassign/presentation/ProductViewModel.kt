package dev.kevalkanpariya.swipetakehomeassign.presentation

import android.net.Uri
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dev.kevalkanpariya.swipetakehomeassign.data.appsearch.SearchProductHistory
import dev.kevalkanpariya.swipetakehomeassign.domain.repository.ProductRepository
import dev.kevalkanpariya.swipetakehomeassign.domain.utils.handleResult
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchProductsState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.AddProductState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.ProductTypeState
import dev.kevalkanpariya.swipetakehomeassign.utils.SearchProductHistoryManager
import dev.kevalkanpariya.swipetakehomeassign.utils.createFileFromInputStream
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class ProductViewModel(
    private val searchProductHistoryManager: SearchProductHistoryManager,
    private val productRepository: ProductRepository
) : ViewModel() {


    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _searchProductsState = MutableStateFlow(SearchProductsState())
    val searchProductsState = _searchProductsState.asStateFlow()

    private val _productTypeStateList = MutableStateFlow(listOf(ProductTypeState()))
    val productTypeStateList = _productTypeStateList.asStateFlow()

    private val _snackbarEventFlow = MutableSharedFlow<String>()
    val snackbarEventFlow = _snackbarEventFlow.asSharedFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val getProducts = searchProductsState
        .map { it.searchQuery to it.isActiveSearch }
        .distinctUntilChanged()
        .flatMapLatest { (searchQuery, isActiveSearch) ->
            if (!isActiveSearch) {
                Pager(
                    PagingConfig(
                        pageSize = 20,
                        enablePlaceholders = true,
                        maxSize = 200
                    )
                ) { productRepository.getProducts(searchQuery) }
                    .flow
                    .cachedIn(viewModelScope)
            } else {
                flowOf(PagingData.empty())
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    private var searchJob: Job? = null

    private val productTypeList = listOf("Entertainment", "Healthcare", "Education", "Mobile", "Electronics", "Watches", "Men's Clothes", "Groceries")

    init {
        viewModelScope.launch {
            refreshProducts()
            searchProductHistoryManager.init()

            _productTypeStateList.update {
                productTypeList.map {
                    ProductTypeState(type = it, id = UUID.randomUUID().toString())
                }
            }
        }
    }

    fun onAction(action: ProductUiAction) {
        when (action) {
            is ProductUiAction.OnActiveSearchChange -> {
                if (action.isActiveSearch) {
                    searchJob = viewModelScope.launch {
                        val searchProductHistories = searchProductHistoryManager.searchProductHistories("")
                        _searchProductsState.update { state ->
                            state.copy(searchProductHistories = searchProductHistories.map { it.history })
                        }
                    }
                }
                _searchProductsState.update { state ->
                    state.copy(isActiveSearch = action.isActiveSearch)
                }
            }
            is ProductUiAction.OnSearchTextChange -> {
                searchJob?.cancel()
                _searchProductsState.update { state ->
                    state.copy(searchQuery = action.text)
                }
                searchJob = viewModelScope.launch {
                    delay(500L)
                    val searchProductHistories = searchProductHistoryManager.searchProductHistories(action.text)
                    _searchProductsState.update { state ->
                        state.copy(searchProductHistories = searchProductHistories.map { it.history })
                    }
                }
            }
            is ProductUiAction.OnSearch -> {
                viewModelScope.launch {
                    val query = searchProductsState.value.searchQuery.trim()
                    if (query.isNotBlank()) {
                        searchProductHistoryManager.putSearchProductHistory(
                            SearchProductHistory(
                                nameSpace = "NameSpace",
                                historyId = query.hashCode().toString(),
                                history = query,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }
                    _searchProductsState.update { state ->
                        state.copy(searchQuery = query, isActiveSearch = false)
                    }

                }
            }
            is ProductUiAction.OnProductTypeSelected -> {
                _productTypeStateList.update { stateList ->
                    stateList.map {
                        if (it.id == action.id) {
                            it.copy(isSelected = action.isSelected)
                        } else {
                            it.copy(isSelected = false)
                        }
                    }
                }

                val productType = productTypeStateList.value.fastFirstOrNull { it.id == action.id }?.type
                productType?.let {
                    _addProductState.update { state ->
                        state.copy(productType = productType)
                    }
                }

            }
            is ProductUiAction.OnProductPriceChanged -> _addProductState.update { state ->
                state.copy(price = state.price.copy(text = action.price, error = null))
            }
            is ProductUiAction.OnProductTaxRateChanged -> _addProductState.update { state ->
                state.copy(taxRate = state.taxRate.copy(text = action.taxRate, error = null))
            }
            is ProductUiAction.OnProductTitleChanged -> _addProductState.update { state ->
                state.copy(productName = state.productName.copy(text = action.title, error = null))
            }
            is ProductUiAction.OnPhotoUriChanged -> _addProductState.update { state ->
                state.copy(photoUri = action.uri)
            }
            is ProductUiAction.OnDone -> {
                viewModelScope.launch {
                    _addProductState.update { state ->
                        state.copy(isProductCreating = true)
                    }

                    productRepository.addProduct(
                        productType = addProductState.value.productType,
                        productName = addProductState.value.productName.text,
                        productPrice = addProductState.value.price.text.toDouble(),
                        productTaxRate = addProductState.value.taxRate.text.toDouble(),
                        file = createFileFromInputStream(action.context, addProductState.value.photoUri)
                    ).handleResult(
                        onSuccess = {
                            _snackbarEventFlow.emit("Product added Successfully!")
                            _addProductState.update { state ->
                                state.copy(isProductCreating = false)
                            }

                            _productTypeStateList.update { state ->
                                state.map {
                                    if (it.isSelected) {
                                        it.copy(isSelected = false)
                                    } else {
                                        it
                                    }
                                }
                            }
                            _addProductState.update { state ->
                                state.copy(
                                    productName = state.productName.copy(text = ""),
                                    productType = "",
                                    price = state.price.copy(text = ""),
                                    taxRate = state.taxRate.copy(text = ""),
                                    photoUri = Uri.EMPTY
                                )
                            }

                            BottomSheetId.entries.reversed().fastForEach {
                                onAction(ProductUiAction.OnManageBottomSheet(action.scope, it, BottomSheetActionState.CLOSE))
                            }
                        },
                        onError = { error ->
                            _snackbarEventFlow.emit("Product add failed!: ${error.toString()}")
                            _addProductState.update { state ->
                                state.copy(isProductCreating = false)
                            }
                        }
                    )
                }

            }
            is ProductUiAction.OnBottomSheetTwoNext -> {
                var isValid = true
                _addProductState.update { addProductState ->
                    addProductState.copy(
                        productName = addProductState.productName.copy(
                            error = if (addProductState.productName.text.isEmpty()) {
                                isValid = false
                                "Product name is required"
                            } else null
                        ),
                        price = addProductState.price.copy(
                            error = if (addProductState.price.text.isEmpty()) {
                                isValid = false
                                "Product price is required"
                            } else null),
                        taxRate = addProductState.taxRate.copy(
                            error = if (addProductState.taxRate.text.isEmpty()) {
                                isValid = false
                                "Product taxRate is required"
                            } else null)
                    )
                }
                if (isValid) {
                    onAction(ProductUiAction.OnManageBottomSheet(action.scope, BottomSheetId.SHEET_THREE, BottomSheetActionState.OPEN))
                }
            }
            is ProductUiAction.OnManageBottomSheet -> {
                action.scope.launch {
                    when (action.bottomSheetActionState) {
                        BottomSheetActionState.CLOSE -> {
                            collapseBottomSheet(action.bottomSheetId)
                        }
                        BottomSheetActionState.OPEN -> {
                            expandBottomSheet(action.bottomSheetId)
                        }
                    }
                }
            }
        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            productRepository.refreshProducts()
        }
    }

    private suspend fun collapseBottomSheet(sheetId: BottomSheetId) {
        when (sheetId) {
            BottomSheetId.SHEET_ONE -> {
                _addProductState.update { state ->
                    state.copy(
                        productType = "",
                    )
                }
                _addProductState.value.bottomSheetOneState.collapse()
            }
            BottomSheetId.SHEET_TWO -> {
                _addProductState.update { state ->
                    state.copy(
                        productName = state.productName.copy(text = ""),
                        price = state.price.copy(text = ""),
                        taxRate = state.taxRate.copy(text = "")
                    )
                }
                _addProductState.value.bottomSheetTwoState.collapse()
            }
            BottomSheetId.SHEET_THREE -> {
                _addProductState.update { state ->
                    state.copy(
                        photoUri = Uri.EMPTY
                    )
                }
                _addProductState.value.bottomSheetThreeState.collapse()
            }
        }
    }

    private suspend fun expandBottomSheet(sheetId: BottomSheetId) {
        when (sheetId) {
            BottomSheetId.SHEET_ONE -> {
                _addProductState.value.bottomSheetOneState.expand()
            }
            BottomSheetId.SHEET_TWO -> {
                _addProductState.value.bottomSheetTwoState.expand()
            }
            BottomSheetId.SHEET_THREE -> _addProductState.value.bottomSheetThreeState.expand()
        }
    }

    override fun onCleared() {
        searchProductHistoryManager.closeSession()
        super.onCleared()
    }
}
