package dev.kevalkanpariya.swipetakehomeassign.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ProductDAO {

    @Query("SELECT * FROM product WHERE productName LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    fun getProducts(query: String): PagingSource<Int, Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(remoteProduct: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(remoteProducts: List<Product>)

    @Query("SELECT EXISTS(SELECT 1 FROM product WHERE productName = :productName)")
    suspend fun isProductExist(productName: String): Boolean
}

