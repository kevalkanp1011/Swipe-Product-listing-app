package dev.kevalkanpariya.swipetakehomeassign.presentation

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import dev.kevalkanpariya.swipetakehomeassign.R
import dev.kevalkanpariya.swipetakehomeassign.data.local.Product
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchBar
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchBarDefaults
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SearchProductsState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.AddProductState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.ProductTypeState
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    productPagingItems: LazyPagingItems<Product>,
    addProductState: AddProductState,
    searchProductsState: SearchProductsState,
    productTypeStateList: List<ProductTypeState>,
    onProductTypeSelected: (String, Boolean) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onActiveSearchChange: (Boolean) -> Unit,
    onSearch: () -> Unit,
    onManageBottomSheet: (BottomSheetId, BottomSheetActionState) -> Unit,
    onProductPriceChanged: (String) -> Unit,
    onProductTitleChanged: (String) -> Unit,
    onProductTaxRateChanged: (String) -> Unit,
    onPhotoUriSelected: (Uri) -> Unit,
    onDone: () -> Unit,

) {


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        when(productPagingItems.loadState.refresh) {
            is LoadState.Error -> {
                Text(
                    modifier = Modifier
                        .align(Alignment.Center),
                    text = "Failed to Load Products..",
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 25.sp
                    )
                )
            }
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                )
            }
            else -> Unit
        }


        LazyVerticalGrid(
            modifier = modifier.then(
                Modifier.padding(top = 60.dp, start = 10.dp, end = 10.dp, bottom = 10.dp)
            ),
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)

        ) {

            itemsIndexed(
                items = productPagingItems.itemSnapshotList.items,
                key = { index, _ -> index }) { _, product ->

                ProductItem(
                    product = product
                )
            }

            item {
                when(productPagingItems.loadState.append) {
                    is LoadState.Error -> {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = "Failed to Load Products..",
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 25.sp
                            )
                        )
                    }
                    is LoadState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(100.dp)
                                .align(Alignment.Center)
                        )
                    }
                    else -> Unit
                }
            }


        }




        val interactionSource = remember { MutableInteractionSource() }
        val focusRequester = remember { FocusRequester() }

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
                .align(Alignment.TopCenter)
                .focusRequester(focusRequester)
                .focusable(interactionSource = interactionSource)
            ,
            shape = RoundedCornerShape(12.dp),
            colors = SearchBarDefaults.colors(
                dividerColor = Color.Black,
                containerColor = Color.White
            ),
            query = searchProductsState.searchQuery,
            onQueryChange = {
                onSearchQueryChange(it)
            },
            onSearch = {
                onSearch()
            },
            active = searchProductsState.isActiveSearch,
            onActiveChange = onActiveSearchChange,
            enabled = true,
            placeholder = {
                Text(
                    text = "Search....",
                    fontSize = 12.sp
                )
            },
            interactionSource = interactionSource,
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "search")
            },
            trailingIcon = {

                if (searchProductsState.isActiveSearch) {
                    IconButton(onClick = {
                        if (searchProductsState.searchQuery.isNotEmpty()) {
                            onSearchQueryChange("")
                        } else {
                            onSearch()
                            onActiveSearchChange(false)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "clear")
                    }
                }


            },
            onAddProduct = {
                onManageBottomSheet(BottomSheetId.SHEET_ONE, BottomSheetActionState.OPEN)
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
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(Modifier.height(15.dp))



                searchProductsState.searchProductHistories.fastForEach {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                focusRequester.requestFocus()
                                onSearchQueryChange(it)
                                onSearch()

                            }
                            .padding(vertical = 5.dp)
                           ,
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        Icon(
                            painter = painterResource(R.drawable.baseline_history_24),
                            contentDescription = "history_icon"
                        )
                        Text(
                            text = it,
                            style = TextStyle()
                        )

                    }
                }

            }


        }




        AddProductScreen(
            addProductState = addProductState,
            onManageBottomSheet = {bottomSheetId, bottomSheetActionState ->
                onManageBottomSheet(bottomSheetId, bottomSheetActionState)

            },
            productTypeStateList = productTypeStateList,
            onProductTypeSelected = onProductTypeSelected,
            onProductPriceChanged = onProductPriceChanged,
            onProductTitleChanged = onProductTitleChanged,
            onProductTaxRateChanged = onProductTaxRateChanged,
            onPhotoUriSelected = onPhotoUriSelected,
            onDone = onDone

        )


    }





}


@Composable
fun ProductItem(
    product: Product
) {

    val imageRequest = ImageRequest.Builder(LocalContext.current)
        .data(product.image.takeIf { it.isNotEmpty() } ?: R.drawable.placeholder)
        .crossfade(true)
        .build()

    val constraints = decoupledConstraints(margin = 10.dp)



    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(5.dp),
        border = BorderStroke(1.dp, Color.Black.copy(0.4f)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        ConstraintLayout(constraints) {

            AsyncImage(
                model = imageRequest,
                contentDescription = "Product Image",
                contentScale = ContentScale.Crop,
                alpha = if (product.image.isEmpty()) 0.4f else 1f,
                modifier = Modifier
                    .aspectRatio(1f)
                    .background(Color.Black.copy(0.4f))
                    .layoutId("image"),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    //.height(90.dp)
                    .padding(top = 20.dp, start = 10.dp)
                    .layoutId("desc_col"),
                verticalArrangement = Arrangement.spacedBy(5.dp)

            ) {


                Text(
                    modifier = Modifier, // Add clip for better visual appearance
                    text = if (product.productName.length > 75) {
                        product.productName.substring(0, 75 - 3) + "..."
                    } else {
                        product.productName
                    },
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.SemiBold,
                    ),
                    maxLines = 1, // Limit to one line
                    overflow = TextOverflow.Ellipsis, // Show ellipsis if text overflows
                )
                Text(
                    modifier = Modifier,
                    text = "₹ ${formatPrice(product.price)}", // Call formatPrice function
                    style = TextStyle(
                        fontSize = 15.sp,
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                )

                Text(
                    modifier = Modifier,
                    text = "${formatTax(product.tax)}% tax", // Call formatTax function
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontSize = 12.sp
                    ),
                )
            }

            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xff5f00d3))
                    .border(2.dp, color = Color.White, RoundedCornerShape(20.dp))
                    .padding(6.dp)
                    .layoutId("product_type_txt"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    modifier = Modifier.size(15.dp),
                    painter = rememberAsyncImagePainter(R.drawable.category_24px),
                    contentDescription = "category_icon",
                    tint = Color.White
                )
                Text(
                    text = product.productType,
                    style = TextStyle(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = mierFontFamily,
                        color = Color.White
                    )
                )
            }


        }

    }
}


private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val productImage = createRefFor("image")
        val productDescCol = createRefFor("desc_col")
        val productTypeText = createRefFor("product_type_txt")

        constrain(productImage) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
        }
        constrain(productDescCol) {
            top.linkTo(productImage.bottom)
            start.linkTo(parent.start, margin)
            end.linkTo(parent.end, margin)
            bottom.linkTo(parent.bottom, margin)
        }
        constrain(productTypeText) {
            top.linkTo(productDescCol.top, -15.dp)
            bottom.linkTo(productDescCol.top, 15.dp)
            start.linkTo(parent.start, 8.dp)

        }
    }
}

// Helper functions to format price and tax values
fun formatPrice(price: Double): String {
    val formattedPrice =
        if (price.rem(1.0) == 0.0) { // Check if price is a whole number (no decimals)
            price.toInt().toString()
        } else {
            "%.2f".format(price) // Format with two decimal places
        }
    return formattedPrice
}

fun formatTax(tax: Double): String {
    return "%.2f".format(tax) // Always format tax with two decimal places
}