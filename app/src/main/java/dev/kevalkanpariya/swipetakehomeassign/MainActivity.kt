package dev.kevalkanpariya.swipetakehomeassign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import dev.kevalkanpariya.swipetakehomeassign.presentation.BottomSheetId
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductUiAction
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductViewModel
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductsScreen
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.SwipeTakeHomeAssignTheme
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SwipeTakeHomeAssignTheme {

                val snackbarHostState  = remember { SnackbarHostState() }
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        androidx.compose.material3.SnackbarHost(snackbarHostState)
                    }
                ) { innerPadding ->
                    val viewModel = koinViewModel<ProductViewModel>()

                    val addProductState by viewModel.addProductState.collectAsStateWithLifecycle()
                    val searchProductsState by viewModel.searchProductsState.collectAsStateWithLifecycle()
                    val productTypeStateList by viewModel.productTypeStateList.collectAsStateWithLifecycle()
                    val coroutineScope = rememberCoroutineScope()
                    val context = LocalContext.current
                    val productPagingItems = viewModel.getProducts.collectAsLazyPagingItems()

                    LaunchedEffect(Unit) {
                        viewModel.snackbarEventFlow.collectLatest { msg ->
                            snackbarHostState.showSnackbar(msg)
                        }



                    }

                    ProductsScreen(
                        modifier = Modifier.padding(innerPadding),
                        productPagingItems = productPagingItems,
                        addProductState = addProductState,
                        searchProductsState = searchProductsState,

                        onSearchQueryChange = {viewModel.onAction(ProductUiAction.OnSearchTextChange(it))},
                        onActiveSearchChange = {viewModel.onAction(ProductUiAction.OnActiveSearchChange(it))},
                        onSearch = {viewModel.onAction(ProductUiAction.OnSearch)},
                        onManageBottomSheet = { bottomSheetId, bottomSheetActionState ->
                            when(bottomSheetId) {
                                BottomSheetId.SHEET_THREE -> {
                                    viewModel.onAction(ProductUiAction.OnBottomSheetTwoNext(coroutineScope))
                                }
                                else -> {
                                    viewModel.onAction(ProductUiAction.OnManageBottomSheet(coroutineScope, bottomSheetId, bottomSheetActionState))
                                }
                            }

                        },
                        productTypeStateList = productTypeStateList,
                        onProductTypeSelected = {id, isSelected ->
                            viewModel.onAction(ProductUiAction.OnProductTypeSelected(id, isSelected))
                        },
                        onProductTitleChanged = {viewModel.onAction(ProductUiAction.OnProductTitleChanged(it))},
                        onProductPriceChanged = {viewModel.onAction(ProductUiAction.OnProductPriceChanged(it))},
                        onProductTaxRateChanged = {viewModel.onAction(ProductUiAction.OnProductTaxRateChanged(it))},
                        onPhotoUriSelected = {viewModel.onAction(ProductUiAction.OnPhotoUriChanged(it))},

                        onDone = {
                            viewModel.onAction(ProductUiAction.OnDone(context, coroutineScope))

                        }

                    )
                }
            }
        }
    }
}
