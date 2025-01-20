package dev.kevalkanpariya.swipetakehomeassign.data.repoImpl

import androidx.paging.PagingSource
import dev.kevalkanpariya.swipetakehomeassign.BuildConfig
import dev.kevalkanpariya.swipetakehomeassign.data.local.Product
import dev.kevalkanpariya.swipetakehomeassign.data.local.ProductDAO
import dev.kevalkanpariya.swipetakehomeassign.data.remote.AddProductResponse
import dev.kevalkanpariya.swipetakehomeassign.data.remote.RemoteProduct
import dev.kevalkanpariya.swipetakehomeassign.data.remote.toProduct
import dev.kevalkanpariya.swipetakehomeassign.domain.models.Result
import dev.kevalkanpariya.swipetakehomeassign.domain.models.RootError
import dev.kevalkanpariya.swipetakehomeassign.domain.repository.ProductRepository
import dev.kevalkanpariya.swipetakehomeassign.domain.utils.handleHttpResponseException
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.SerializationException
import dev.kevalkanpariya.swipetakehomeassign.domain.models.DataError
import java.io.File
import java.io.IOException

class ProductRepoImpl(
    private val client: HttpClient,
    private val dao: ProductDAO
): ProductRepository {
    override fun getProducts(query: String): PagingSource<Int, Product> {
        return dao.getProducts(query)
    }

    override suspend fun isProductExist(productName: String): Result<Boolean, RootError> {
        return try {

            val isProductExist = dao.isProductExist(productName)

            Result.Success(isProductExist)

        } catch (e: ClientRequestException) {
            e.printStackTrace()
            handleHttpResponseException(e)
        } catch (e: ServerResponseException) {
            e.printStackTrace()
            handleHttpResponseException(e)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(DataError.Network.SERVER_ERROR)
        } catch (e: SerializationException) {
            e.printStackTrace()
            Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.UNKNOWN)
        }
    }

    override suspend fun addProduct(
        productType: String,
        productName: String,
        productPrice: Double,
        productTaxRate: Double,
        file: File
    ): Result<Product, RootError> {
        return try {
            val response = client.post {
                url("${BuildConfig.SERVER_URL}/add")
                header(HttpHeaders.ContentType, ContentType.MultiPart.FormData.toString())
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("product_name", productName)
                            append("product_type", productType)
                            append("price", productPrice)
                            append("tax", productTaxRate)
                            append("files[]", file.readBytes(), Headers.build { append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"") })
                        }
                    )
                )
            }.body<AddProductResponse>()
            file.delete()

            val localProduct = response.productDetails.toProduct()

            dao.insertProduct(localProduct)

            Result.Success(data = localProduct)

        } catch (e: ClientRequestException) {
            e.printStackTrace()
            handleHttpResponseException(e)
        } catch (e: ServerResponseException) {
            e.printStackTrace()
            handleHttpResponseException(e)
        } catch (e: IOException) {
            e.printStackTrace()
            Result.Error(DataError.Network.SERVER_ERROR)
        } catch (e: SerializationException) {
            e.printStackTrace()
            Result.Error(DataError.Network.SERIALIZATION)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Network.UNKNOWN)
        }
    }



    override suspend fun refreshProducts() {
        try {

            val products = client.get {
                url("${BuildConfig.SERVER_URL}/get")
            }.body<List<RemoteProduct>>()

            val localProducts = products.map {
                it.toProduct()
            }

            dao.insertProducts(localProducts)

        } catch (e: ClientRequestException) {
            e.printStackTrace()
        } catch (e: ServerResponseException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: SerializationException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



    override suspend fun deleteProduct(id: Int) {

    }
}