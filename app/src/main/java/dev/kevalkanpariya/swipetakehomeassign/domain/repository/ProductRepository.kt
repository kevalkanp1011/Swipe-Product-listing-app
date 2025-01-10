package dev.kevalkanpariya.swipetakehomeassign.domain.repository

import androidx.paging.PagingSource
import dev.kevalkanpariya.swipetakehomeassign.data.local.Product
import dev.kevalkanpariya.swipetakehomeassign.domain.models.Result
import dev.kevalkanpariya.swipetakehomeassign.domain.models.RootError
import java.io.File

interface ProductRepository {

    fun getProducts(query: String): PagingSource<Int, Product>


    suspend fun addProduct(
        productType: String,
        productName: String,
        productPrice: Double,
        productTaxRate: Double,
        file: File
    ): Result<Product, RootError>


    suspend fun refreshProducts()


    suspend fun deleteProduct(id: Int)

}