package dev.kevalkanpariya.swipetakehomeassign.data.remote

import android.annotation.SuppressLint
import dev.kevalkanpariya.swipetakehomeassign.data.local.Product
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class RemoteProduct(
    val image: String,
    val price: Double,
    @SerialName("product_name") val productName: String,
    @SerialName("product_type") val productType: String,
    val tax: Double,
)

@SuppressLint("NewApi")
fun RemoteProduct.toProduct(): Product { return Product(
    image = this.image,
    price = this.price,
    productName = this.productName,
    productType = this.productType,
    tax = this.tax,
    lastUpdated = LocalDateTime.now(),
) }
