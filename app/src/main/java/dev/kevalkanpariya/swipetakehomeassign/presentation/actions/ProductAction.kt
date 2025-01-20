package dev.kevalkanpariya.swipetakehomeassign.presentation.actions

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.CoroutineScope

sealed interface ProductUiAction {

    data class OnManageBottomSheet(val scope: CoroutineScope, val bottomSheetId: BottomSheetId, val bottomSheetActionState: BottomSheetActionState):
        ProductUiAction
    data class OnResetStates(val bottomSheetId: BottomSheetId): ProductUiAction
    data class OnSearchTextChange(val text: String): ProductUiAction
    data class OnActiveSearchChange(val isActiveSearch: Boolean): ProductUiAction

    data object OnSearch: ProductUiAction

    data class OnProductTypeSelected(val id:String, val isSelected: Boolean): ProductUiAction

    data class OnProductTitleChanged(val title: String): ProductUiAction

    data class OnProductPriceChanged(val price: String): ProductUiAction

    data class OnProductTaxRateChanged(val taxRate: String): ProductUiAction

    data class OnPhotoUriChanged(val uri: Uri): ProductUiAction

    data class OnBottomSheetThreeDone(val context: Context, val scope: CoroutineScope): ProductUiAction

    data class OnBottomSheetTwoNext(val scope: CoroutineScope): ProductUiAction

    data class OnBottomSheetOneNext(val scope: CoroutineScope): ProductUiAction
}

enum class BottomSheetActionState{
    CLOSE,
    OPEN
}

enum class BottomSheetId { SHEET_ONE, SHEET_TWO, SHEET_THREE }