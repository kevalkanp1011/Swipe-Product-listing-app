package dev.kevalkanpariya.swipetakehomeassign.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime


@Entity
data class Product(
    val image: String,
    val price: Double,
    val productName: String,
    val productType: String,
    val tax: Double,
    val lastUpdated: LocalDateTime,
    @PrimaryKey(autoGenerate = true) val id: Int?= null
)