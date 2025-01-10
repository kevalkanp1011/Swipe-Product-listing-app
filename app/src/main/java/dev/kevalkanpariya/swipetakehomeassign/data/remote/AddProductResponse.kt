package dev.kevalkanpariya.swipetakehomeassign.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AddProductResponse(
    val message: String,
    @SerialName("product_details") val productDetails: RemoteProduct,
    @SerialName("product_id") val productId: Int,
    val success: Boolean
)



