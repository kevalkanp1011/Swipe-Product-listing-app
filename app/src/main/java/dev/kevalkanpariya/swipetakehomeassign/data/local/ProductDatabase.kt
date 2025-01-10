package dev.kevalkanpariya.swipetakehomeassign.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(
    entities = [Product::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(LocalDateTimeConverter::class)
abstract class ProductDatabase: RoomDatabase() {

    abstract val productDao: ProductDAO

    companion object {
        const val DATABASE_NAME = "products_db"
    }
}