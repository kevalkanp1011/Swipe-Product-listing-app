package dev.kevalkanpariya.swipetakehomeassign.di

import androidx.room.Room
import dev.kevalkanpariya.swipetakehomeassign.data.local.ProductDAO
import dev.kevalkanpariya.swipetakehomeassign.data.local.ProductDatabase
import dev.kevalkanpariya.swipetakehomeassign.data.repoImpl.ProductRepoImpl
import dev.kevalkanpariya.swipetakehomeassign.domain.repository.ProductRepository
import dev.kevalkanpariya.swipetakehomeassign.presentation.ProductViewModel
import dev.kevalkanpariya.swipetakehomeassign.utils.SearchProductHistoryManager
import dev.kevalkanpariya.swipetakehomeassign.utils.ktorOKHTTPClient
import io.ktor.client.HttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {

    single<HttpClient> { ktorOKHTTPClient }

    single<ProductDAO> {
        Room.databaseBuilder(
            androidContext(),
            ProductDatabase::class.java,
            ProductDatabase.DATABASE_NAME
        )
            .build()
            .productDao
    }

    single<SearchProductHistoryManager> { SearchProductHistoryManager(get()) }

    single<ProductRepository> { ProductRepoImpl(get(), get())}


    viewModelOf(::ProductViewModel)

}