package dev.kevalkanpariya.swipetakehomeassign.presentation.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetActionState
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetId
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.TextFieldState
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily
import dev.kevalkanpariya.swipetakehomeassign.utils.DecimalFormatter
import dev.kevalkanpariya.swipetakehomeassign.utils.DecimalInputVisualTransformation

@Composable
fun SheetTwoContent(
    decimalFormatter: DecimalFormatter,
    onManageBottomSheet:(BottomSheetId, BottomSheetActionState) -> Unit,
    productTitle: TextFieldState,
    productPrice: TextFieldState,
    productTaxRate: TextFieldState,
    onProductTitleChanged: (String) -> Unit,
    onProductPriceChanged: (String) -> Unit,
    onProductTaxRateChanged: (String) -> Unit
) {

    Box(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {

            Column {
                Text(
                    text = "Product name",
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )
                Spacer(Modifier.height(5.dp))
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                    shape = RoundedCornerShape(5.dp),
                    value = productTitle.text,
                    onValueChange = onProductTitleChanged,
                    placeholder = {
                        Text(
                            text = "Product name",
                            style = TextStyle(
                                color = Color.Black.copy(0.4f),
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp,

                            )
                        )
                    },
                    keyboardOptions  = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        focusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black

                    )
                )
                productTitle.error?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = Color.Red,
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Column {
                Text(
                    text = "Product price (in ₹)",
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )

                Spacer(Modifier.height(5.dp))
                TextField(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                    shape = RoundedCornerShape(5.dp),
                    value = productPrice.text,
                    onValueChange = {
                        onProductPriceChanged(decimalFormatter.cleanup(it))
                    },
                    placeholder = {
                        Text(
                            text = "Product Price",
                            style = TextStyle(
                                color = Color.Black.copy(0.4f),
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        )
                    },
                    keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        focusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black

                    ),
                    visualTransformation = DecimalInputVisualTransformation(decimalFormatter)

                )
                productPrice.error?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = Color.Red,
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            Column {
                Text(
                    text = "Tax rate (in %)",
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                )
                Spacer(Modifier.height(5.dp))

                TextField(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(5.dp)),
                    shape = RoundedCornerShape(5.dp),
                    value = productTaxRate.text,
                    onValueChange = {
                        onProductTaxRateChanged(decimalFormatter.cleanup(it))
                    },
                    placeholder = {
                        Text(
                            text = "Tax rate",
                            style = TextStyle(
                                color = Color.Black.copy(0.4f),
                                fontFamily = mierFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 15.sp
                            )
                        )
                    },
                    keyboardActions = KeyboardActions(onNext = {
                        onManageBottomSheet(BottomSheetId.SHEET_THREE, BottomSheetActionState.OPEN)
                    }),
                    keyboardOptions  = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Next),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        focusedContainerColor = Color(0xFF3F0085).copy(0.05f),
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    ),
                    visualTransformation = DecimalInputVisualTransformation(decimalFormatter)

                )
                productTaxRate.error?.let {
                    Text(
                        text = it,
                        style = TextStyle(
                            color = Color.Red,
                            fontFamily = mierFontFamily,
                            fontWeight = FontWeight.Normal,
                            fontSize = 12.sp
                        )
                    )
                }
            }


        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ) {
            HorizontalDivider()
            Spacer(Modifier.height(5.dp))
            Button(
                modifier = Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, bottom = 10.dp),
                onClick = {
                    onManageBottomSheet(BottomSheetId.SHEET_THREE, BottomSheetActionState.OPEN)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff5f00d3)),
                shape = RoundedCornerShape(5.dp)
            ) {
                Text(
                    text = "Next",
                    style = TextStyle(
                        fontFamily = mierFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                )
            }
        }
    }

}