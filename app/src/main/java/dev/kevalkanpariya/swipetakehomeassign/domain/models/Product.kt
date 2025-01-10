package dev.kevalkanpariya.swipetakehomeassign.domain.models

data class Product(
    val id: Int,
    val productName: String,
    val productType: String,
    val taxRate: Float,
    val imageUrl: String
)