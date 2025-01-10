package dev.kevalkanpariya.swipetakehomeassign.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface ProductDAO {


    @Query("SELECT * FROM product WHERE productName LIKE '%' || :query || '%'")
    fun getProducts(query: String): PagingSource<Int, Product>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(remoteProduct: Product)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(remoteProducts: List<Product>)

}