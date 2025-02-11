package dev.kevalkanpariya.swipetakehomeassign.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetActionState
import dev.kevalkanpariya.swipetakehomeassign.presentation.actions.BottomSheetId
import dev.kevalkanpariya.swipetakehomeassign.presentation.states.ProductTypeState
import dev.kevalkanpariya.swipetakehomeassign.ui.theme.mierFontFamily

@Composable
fun SheetOneContent(
    productTypeChooseError: String,
    productTypeStateList: List<ProductTypeState>,
    onProductTypeSelected: (String, Boolean) -> Unit,
    onManageBottomSheet:(BottomSheetId, BottomSheetActionState) -> Unit,
) {

    Column(
        modifier = Modifier.padding(14.dp)
    ) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f),
            contentPadding = PaddingValues(0.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {

            items(
                items = productTypeStateList,
                key = {it.id}
            ) { productTypeState ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable() {
                            onProductTypeSelected(productTypeState.id, !productTypeState.isSelected)
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Checkbox(
                        modifier = Modifier.size(15.dp),
                        checked = productTypeState.isSelected,
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xff5f00d3),
                            uncheckedColor = Color.Black.copy(0.6f),
                            checkmarkColor = Color.White
                        ),
                        onCheckedChange = {
                            onProductTypeSelected(productTypeState.id, it)
                        }
                    )

                    Text(
                        text = productTypeState.type,
                        style = TextStyle(
                            fontFamily = mierFontFamily,
                            fontWeight = if(productTypeState.isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    )
                }
            }


        }

        HorizontalDivider()
        if (productTypeChooseError.isNotBlank()) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = productTypeChooseError,
                style = TextStyle(
                    fontFamily = mierFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(Modifier.height(5.dp))
        } else {
            Spacer(Modifier.height(8.dp))
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onManageBottomSheet(BottomSheetId.SHEET_TWO, BottomSheetActionState.OPEN)
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