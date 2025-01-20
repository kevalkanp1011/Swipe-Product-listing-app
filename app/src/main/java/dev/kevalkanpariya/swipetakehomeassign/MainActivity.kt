package dev.kevalkanpariya.swipetakehomeassign

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetId
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.ProductUiAction
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductViewModel
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductsScreen
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetActionState
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchCumTopBar
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchBarDefaults
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.SwipeTakeHomeAssignTheme
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            SwipeTakeHomeAssignTheme {

                val snackbarHostState  = remember { SnackbarHostState() }

                val viewModel = koinViewModel<ProductViewModel>()

                val addProductState by viewModel.addProductState.collectAsStateWithLifecycle()
                val searchProductsState by viewModel.searchProductsState.collectAsStateWithLifecycle()
                val productTypeStateList by viewModel.productTypeStateList.collectAsStateWithLifecycle()
                val isConnectedToInternet by viewModel.isConnectedToInternet.collectAsStateWithLifecycle()

                val coroutineScope = rememberCoroutineScope()
                val context = LocalContext.current
                val productPagingItems = viewModel.getProducts.collectAsLazyPagingItems()

                LaunchedEffect(Unit) {
                    viewModel.snackbarEventFlow.collectLatest { msg ->
                        snackbarHostState.showSnackbar(msg)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = {
                        androidx.compose.material3.SnackbarHost(snackbarHostState)
                    },
                    topBar = {

                        val interactionSource = remember { MutableInteractionSource() }
                        val focusRequester = remember { FocusRequester() }

                        SearchCumTopBar(
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .focusable(interactionSource = interactionSource),
                            shape = RoundedCornerShape(12.dp),
                            colors = SearchBarDefaults.colors(
                                dividerColor = Color.Black,
                                containerColor = Color.White
                            ),
                            query = searchProductsState.searchQuery,
                            onQueryChange = {
                                viewModel.onAction(ProductUiAction.OnSearchTextChange(it))
                            },
                            onSearch = {
                                viewModel.onAction(ProductUiAction.OnSearch)
                            },
                            active = searchProductsState.isActiveSearch,
                            onActiveChange = {viewModel.onAction(ProductUiAction.OnActiveSearchChange(it))},
                            enabled = true,
                            placeholder = {
                                Text(
                                    text = "Search....",
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        color = Color.Black,
                                        fontFamily = mierFontFamily
                                    )
                                )
                            },
                            interactionSource = interactionSource,
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "search",
                                    tint = Color.Black
                                )
                            },
                            trailingIcon = {

                                if (searchProductsState.isActiveSearch) {
                                    IconButton(onClick = {
                                        if (searchProductsState.searchQuery.isNotEmpty()) {
                                            viewModel.onAction(ProductUiAction.OnSearchTextChange(""))
                                        } else {
                                            viewModel.onAction(ProductUiAction.OnSearch)
                                            viewModel.onAction(ProductUiAction.OnActiveSearchChange(false))
                                        }
                                    }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "clear",
                                            tint = Color.Black

                                        )
                                    }

                                }


                            },
                            isConnectedToInternet = isConnectedToInternet,
                            onAddProduct = {
                                viewModel.onAction(ProductUiAction.OnManageBottomSheet(coroutineScope, BottomSheetId.SHEET_ONE, BottomSheetActionState.OPEN))
                            }
                        ) {

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(start = 14.dp, top = 14.dp)
                            ) {

                                Text(
                                    text = "Recent searches",
                                    style = TextStyle(
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black,
                                        fontFamily = mierFontFamily
                                    )
                                )

                                Spacer(Modifier.height(15.dp))
                                if (searchProductsState.searchProductHistories.isEmpty()) {
                                    Text(
                                        text = "no search history found",
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Light,
                                            color = Color.Black.copy(0.7f),
                                            fontFamily = mierFontFamily
                                        )
                                    )
                                }
                                searchProductsState.searchProductHistories.fastForEach {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(
                                                interactionSource = remember { MutableInteractionSource() },
                                                indication = rememberRipple()
                                            ) {
                                                focusRequester.requestFocus()
                                                viewModel.onAction(ProductUiAction.OnSearchTextChange(it))
                                                viewModel.onAction(ProductUiAction.OnSearch)

                                            }
                                            .padding(vertical = 5.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {

                                        Icon(
                                            painter = painterResource(R.drawable.baseline_history_24),
                                            contentDescription = "history_icon",
                                            tint = Color.Black
                                        )
                                        Text(
                                            text = it,
                                            style = TextStyle(
                                                color = Color.Black,
                                                fontFamily = mierFontFamily
                                            )
                                        )

                                    }
                                }

                            }


                        }
                    },
                    bottomBar = {
                        if (!isConnectedToInternet) {
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xff5f00d3).copy(0.08f))
                                    .padding(5.dp)
                                ,
                                text = "no internet available",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontFamily = mierFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xff5f00d3),
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    }
                ) { innerPadding ->

                    ProductsScreen(
                        modifier = Modifier.padding(innerPadding),
                        productPagingItems = productPagingItems,
                        addProductState = addProductState,
                        onManageBottomSheet = { bottomSheetId, bottomSheetActionState ->

                            when(bottomSheetActionState) {
                                BottomSheetActionState.CLOSE -> {
                                    viewModel.onAction(ProductUiAction.OnManageBottomSheet(coroutineScope, bottomSheetId, bottomSheetActionState))
                                }
                                BottomSheetActionState.OPEN -> {
                                    when(bottomSheetId) {
                                        BottomSheetId.SHEET_THREE -> {
                                            viewModel.onAction(ProductUiAction.OnBottomSheetTwoNext(coroutineScope))
                                        }
                                        BottomSheetId.SHEET_TWO -> {
                                            viewModel.onAction(ProductUiAction.OnBottomSheetOneNext(coroutineScope))
                                        }
                                        else -> {
                                            viewModel.onAction(ProductUiAction.OnManageBottomSheet(coroutineScope, bottomSheetId, bottomSheetActionState))
                                        }
                                    }
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
                            viewModel.onAction(ProductUiAction.OnBottomSheetThreeDone(context, coroutineScope))

                        },
                        isConnectedToInternet = isConnectedToInternet,
                        onResetStates = {
                            viewModel.onAction(ProductUiAction.OnResetStates(it))
                        }

                    )
                }
            }
        }
    }
}
