package dev.kevalkanpariya.swipetakehomeassign.presentation.components

data class SearchProductsState(
    val searchQuery: String = "",
    val isActiveSearch: Boolean = false,
    val searchProductHistories: List<String> = emptyList()
)
