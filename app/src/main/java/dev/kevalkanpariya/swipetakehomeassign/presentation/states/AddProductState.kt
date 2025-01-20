package dev.kevalkanpariya.swipetakehomeassign.presentation.states

import android.net.Uri
import com.dokar.sheets.BottomSheetState

data class AddProductState(
    val bottomSheetOneState: BottomSheetState = BottomSheetState(),
    val bottomSheetTwoState: BottomSheetState = BottomSheetState(),
    val bottomSheetThreeState: BottomSheetState = BottomSheetState(),
    val productName: TextFieldState = TextFieldState(),
    val productType: String = "",
    val price: TextFieldState = TextFieldState(),
    val taxRate: TextFieldState = TextFieldState(),
    val photoUri: Uri = Uri.EMPTY,
    val isProductCreating: Boolean =false,
    val productCreateError:String = "",
    val productTypeChooseError: String = ""
)


data class TextFieldState(
    val text: String = "",
    val error: String? = null
)