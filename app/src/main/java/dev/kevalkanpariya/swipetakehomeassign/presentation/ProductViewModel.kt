package dev.kevalkanpariya.swipetakehomeassign.presentation

import android.content.Context
import android.net.Uri
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dokar.sheets.BottomSheetState
import com.dokar.sheets.BottomSheetValue
import dev.kevalkanpariya.swipetakehomeassign.data.appsearch.SearchProductHistory
import dev.kevalkanpariya.swipetakehomeassign.domain.repository.ProductRepository
import dev.kevalkanpariya.swipetakehomeassign.domain.utils.handleResult
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetActionState
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetId
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.ProductUiAction
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.SearchProductsState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.AddProductState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.ProductTypeState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.TextFieldState
import dev.kevalkanpariya.swipetakehomeassign.utils.ConnectivityObserver
import dev.kevalkanpariya.swipetakehomeassign.utils.SearchProductHistoryManager
import dev.kevalkanpariya.swipetakehomeassign.utils.createFileFromInputStream
import kotlinx.coroutines.CoroutineScope
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
    private val productRepository: ProductRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {


    private val _addProductState = MutableStateFlow(AddProductState())
    val addProductState = _addProductState.asStateFlow()

    private val _searchProductsState = MutableStateFlow(SearchProductsState())
    val searchProductsState = _searchProductsState.asStateFlow()

    private val _productTypeStateList = MutableStateFlow(listOf(ProductTypeState()))
    val productTypeStateList = _productTypeStateList.asStateFlow()

    val isConnectedToInternet = connectivityObserver
        .isConnected
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            false
        )

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

            _addProductState.update { state ->
                state.copy(
                    bottomSheetOneState = BottomSheetState(
                        confirmValueChange = {
                            if (it == BottomSheetValue.Collapsed) {
                                resetStates(BottomSheetId.SHEET_ONE)
                                true
                            } else {
                                true
                            }
                        }
                    )
                )
            }

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
                        state.copy(productType = if (action.isSelected) productType else "")
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

            is ProductUiAction.OnResetStates -> {
                resetStates(action.bottomSheetId)
            }

            is ProductUiAction.OnBottomSheetThreeDone -> {
                viewModelScope.launch {
                    _addProductState.update { state ->
                        state.copy(isProductCreating = true)
                    }

                    if (addProductState.value.photoUri == Uri.EMPTY) {
                        _addProductState.update { state ->
                            state.copy(productCreateError = "please upload photo")
                        }
                    }

                    // Check if product already exists
                    productRepository.isProductExist(addProductState.value.productName.text).handleResult(
                        onSuccess = { productExists ->
                            if (productExists == false) {
                                addNewProduct(action.context, action.scope)
                            } else {
                                _addProductState.update { state ->
                                    state.copy(isProductCreating = false, productCreateError = "Product with provided name and type already exists!")
                                }
                            }
                        },
                        onError = { error ->
                            _addProductState.update { state ->
                                state.copy(isProductCreating = false, productCreateError = "Product add failed!: ${error.toString()}")
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
                    onManageBottomSheet(action.scope, BottomSheetId.SHEET_THREE, BottomSheetActionState.OPEN)
                }
            }
            is ProductUiAction.OnBottomSheetOneNext -> {
                var isValid = true
                _addProductState.update { addProductState ->
                    addProductState.copy(
                        productTypeChooseError = if (addProductState.productType.isBlank()) {
                            isValid = false
                            "please choose product type to proceed"
                        } else {
                            ""
                        }
                    )
                }
                if (isValid) {
                    onManageBottomSheet(action.scope, BottomSheetId.SHEET_TWO, BottomSheetActionState.OPEN)
                }
            }

        }
    }

    private fun refreshProducts() {
        viewModelScope.launch {
            productRepository.refreshProducts()
        }
    }


    private fun onManageBottomSheet(
        scope: CoroutineScope,
        bottomSheetId: BottomSheetId,
        bottomSheetActionState: BottomSheetActionState
    ) {
        scope.launch {
            when (bottomSheetActionState) {
                BottomSheetActionState.CLOSE -> {
                    collapseBottomSheet(bottomSheetId)
                }
                BottomSheetActionState.OPEN -> {
                    expandBottomSheet(bottomSheetId)
                }
            }
        }
    }


    // Function to add a new product
    private suspend fun addNewProduct(context: Context, scope: CoroutineScope) {

        productRepository.addProduct(
            productType = addProductState.value.productType,
            productName = addProductState.value.productName.text,
            productPrice = addProductState.value.price.text.toDouble(),
            productTaxRate = addProductState.value.taxRate.text.toDouble(),
            file = createFileFromInputStream(context, addProductState.value.photoUri)
        ).handleResult(
            onSuccess = {
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
                        productName = TextFieldState(),
                        productType = "",
                        price = TextFieldState(),
                        taxRate = TextFieldState(),
                        photoUri = Uri.EMPTY,
                        productCreateError = "",
                        isProductCreating = false
                    )
                }

                BottomSheetId.entries.reversed().fastForEach {
                    onAction(ProductUiAction.OnManageBottomSheet(scope, it, BottomSheetActionState.CLOSE))
                }

                _snackbarEventFlow.emit("Product added Successfully!")
            },
            onError = { error ->
                _snackbarEventFlow.emit("Product add failed!: ${error.toString()}")
                _addProductState.update { state ->
                    state.copy(isProductCreating = false)
                }
            }
        )
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

    private fun resetStates(sheetId: BottomSheetId) {
        when(sheetId) {
            BottomSheetId.SHEET_ONE -> {_addProductState.update { state ->
                _productTypeStateList.update { stateList ->
                    stateList.map {
                        it.copy(isSelected = false)
                    }
                }
                state.copy(
                    productType = "",
                    productName = TextFieldState(),
                    price = TextFieldState(),
                    taxRate = TextFieldState(),
                    photoUri = Uri.EMPTY,
                    productCreateError = ""
                )
            }
            }
            BottomSheetId.SHEET_TWO -> _addProductState.update { it.copy(
                productName = TextFieldState(),
                price = TextFieldState(),
                taxRate = TextFieldState()
            ) }
            BottomSheetId.SHEET_THREE -> _addProductState.update {
                it.copy(photoUri = Uri.EMPTY, productCreateError = "")
            }
        }
    }

    override fun onCleared() {
        searchProductHistoryManager.closeSession()
        super.onCleared()
    }
}
