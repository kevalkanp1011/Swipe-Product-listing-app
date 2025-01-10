package dev.kevalkanpariya.swipetakehomeassign.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapNotNull
import com.dokar.sheets.BottomSheet
import com.dokar.sheets.BottomSheetDefaults
import com.dokar.sheets.PeekHeight
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SheetOneContent
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SheetThreeContent
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.SheetTwoContent
import dev.kevalkanpariya.swipetakehomeassign.presentation.components.StepIcon
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.AddProductState
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.ProductTypeState
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily
import dev.kevalkanpariya.swipetakehomeassign.utils.DecimalFormatter


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddProductScreen(
    addProductState: AddProductState,
    onManageBottomSheet:(BottomSheetId, BottomSheetActionState) -> Unit,
    productTypeStateList: List<ProductTypeState>,
    onProductTypeSelected: (String, Boolean) -> Unit,
    onProductTitleChanged: (String) -> Unit,
    onProductPriceChanged: (String) -> Unit,
    onProductTaxRateChanged: (String) -> Unit,
    onPhotoUriSelected: (Uri) -> Unit,
    onDone: () -> Unit

) {


    BottomSheet(
        state = addProductState.bottomSheetOneState,
        modifier = Modifier.fillMaxHeight((1 - 0.3).toFloat()),
        shape = RoundedCornerShape(15.dp).copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp)),
        skipPeeked = true,
        peekHeight = PeekHeight.fraction(0.15f),
        backgroundColor = Color(0XFFEBEDF3),
        behaviors = BottomSheetDefaults.dialogSheetBehaviors(
            collapseOnClickOutside = false,
            collapseOnBackPress = false,
        ),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()

            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        StepIcon(1, 30.dp)

                        val mProductTypeStateList = productTypeStateList.fastMapNotNull {
                            if (it.isSelected) {
                                it
                            } else {
                                null
                            }
                        }

                        if (mProductTypeStateList.isEmpty()) {
                            Log.d("AddProductScreen", "productlist inside")
                            Text(
                                modifier = Modifier.fillMaxWidth(0.8f),
                                text = "Choose Product Type",
                                style = TextStyle(
                                    fontFamily = mierFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.Black
                                )
                            )

                        } else {
                            Log.d("AddProductScreen", "productlist outside: ${productTypeStateList}")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(0.8f),
                                horizontalArrangement = Arrangement.spacedBy(5.dp),
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ){
                                mProductTypeStateList.fastForEach {
                                    Text(
                                        modifier = Modifier.background(Color.Black.copy(0.2f), RoundedCornerShape(5.dp)).padding(5.dp),
                                        text = it.type,
                                        style = TextStyle(
                                            fontFamily = mierFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.Black
                                        )
                                    )

                                }
                            }
                        }


                    }

                    IconButton(onClick = {
                        onManageBottomSheet(BottomSheetId.SHEET_ONE, BottomSheetActionState.CLOSE)
                    }) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "close"
                        )
                    }

                }

                HorizontalDivider()
            }
        }
    ) {
        SheetOneContent(
            productTypeStateList = productTypeStateList,
            onProductTypeSelected = onProductTypeSelected,
            onManageBottomSheet = {bottomSheetId, bottomSheetActionState ->
                onManageBottomSheet(bottomSheetId, bottomSheetActionState)
            }
        )
    }

    BottomSheet(
        state = addProductState.bottomSheetTwoState,
        modifier = Modifier.fillMaxHeight((1 - 0.37).toFloat()),
        shape = RoundedCornerShape(15.dp).copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp)),
        skipPeeked = true,
        peekHeight = PeekHeight.fraction(0.15f),
        backgroundColor = Color(0XFFEBEDF3),
        behaviors = BottomSheetDefaults.dialogSheetBehaviors(
            collapseOnClickOutside = false,
            collapseOnBackPress = false
        ),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {



                    StepIcon(2, 30.dp)

                    if (addProductState.productName.text.isNotBlank() && addProductState.price.text.isNotBlank() && addProductState.taxRate.text.isNotBlank()) {

                        Text(
                            modifier = Modifier.fillMaxWidth(0.5f),
                            text = addProductState.productName.text,
                            style = TextStyle(
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.Black,
                            ),
                            overflow = TextOverflow.Ellipsis
                        )

                        Column(

                        ) {
                            Text(
                                text = "₹ ${addProductState.price.text}",
                                style = TextStyle(
                                    fontFamily = mierFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            )
                            Text(
                                text = "  (${addProductState.taxRate.text} %)",
                                style = TextStyle(
                                    fontFamily = mierFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                )
                            )
                        }
                    } else {
                        Text(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = "Enter Product Details",
                            style = TextStyle(
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }


                    IconButton(onClick = {
                        onManageBottomSheet(BottomSheetId.SHEET_TWO, BottomSheetActionState.CLOSE)
                    }) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "close"
                        )
                    }

                }
                HorizontalDivider()
            }
        }
    ) {

        SheetTwoContent(
            onManageBottomSheet = {bottomSheetId, bottomSheetActionState ->
                onManageBottomSheet(bottomSheetId, bottomSheetActionState)
            },
            productTitle = addProductState.productName,
            productPrice = addProductState.price,
            productTaxRate = addProductState.taxRate,
            onProductTitleChanged =onProductTitleChanged,
            onProductPriceChanged = onProductPriceChanged,
            onProductTaxRateChanged = onProductTaxRateChanged,
            decimalFormatter = DecimalFormatter(),
        )

    }


    BottomSheet(
        state = addProductState.bottomSheetThreeState,
        modifier = Modifier.fillMaxHeight((1 - 0.44).toFloat()),
        shape = RoundedCornerShape(15.dp).copy(bottomEnd = CornerSize(0.dp), bottomStart = CornerSize(0.dp)),
        skipPeeked = true,
        peekHeight = PeekHeight.fraction(0.15f),
        backgroundColor = Color(0XFFEBEDF3),
        behaviors = BottomSheetDefaults.dialogSheetBehaviors(
            collapseOnClickOutside = false,
            collapseOnBackPress = false
        ),
        dragHandle = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {

                        StepIcon(3, 30.dp)

                        Text(
                            modifier = Modifier.fillMaxWidth(0.8f),
                            text = "Upload Product Photo",
                            style = TextStyle(
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = Color.Black
                            )
                        )
                    }

                    IconButton(onClick = {
                        onManageBottomSheet(BottomSheetId.SHEET_THREE, BottomSheetActionState.CLOSE)
                    }) {
                        Icon(
                            modifier = Modifier.size(30.dp),
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "close"
                        )
                    }

                }
                HorizontalDivider()
            }
        }
    ) {

        SheetThreeContent(
            isProductCreating = addProductState.isProductCreating,
            photoUri = addProductState.photoUri,
            onImageUriChanged = onPhotoUriSelected,
            onDone = onDone
        )


    }




}





