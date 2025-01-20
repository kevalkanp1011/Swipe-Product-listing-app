package dev.kevalkanpariya.swipetakehomeassign.utils

import android.content.Context
import androidx.appsearch.app.AppSearchSession
import androidx.appsearch.app.PutDocumentsRequest
import androidx.appsearch.app.SearchSpec
import androidx.appsearch.app.SetSchemaRequest
import androidx.appsearch.localstorage.LocalStorage
import dev.kevalkanpariya.swipetakehomeassign.data.appsearch.SearchProductHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ExecutionException

class SearchProductHistoryManager(
    private val appContext:Context
) {
    private var session:AppSearchSession?=null


    suspend fun init(){
        withContext(Dispatchers.IO){
            val sessionFuture= LocalStorage.createSearchSessionAsync(
                LocalStorage.SearchContext.Builder(
                    appContext,
                    "SEARCH_PRODUCT_DB"
                ).build()
            )
            val setSchemaRequest = SetSchemaRequest.Builder()
                .addDocumentClasses(SearchProductHistory::class.java)
                .build()
            session=sessionFuture.get()
            session?.setSchemaAsync(setSchemaRequest)
        }
    }


    suspend fun putSearchProductHistory(history:SearchProductHistory):Boolean {
        return withContext(Dispatchers.IO){
            val result=  session?.putAsync(
                PutDocumentsRequest.Builder()
                    .addDocuments(history)
                    .build()
            )?.get()
            result?.isSuccess==true
        }


    }

    suspend fun searchProductHistories(query:String):List<SearchProductHistory>{
        return withContext(Dispatchers.IO){
            val searchSpec = SearchSpec.Builder()
                .addFilterNamespaces("NameSpace")
                .setRankingStrategy(SearchSpec.RANKING_STRATEGY_CREATION_TIMESTAMP)
                .setResultCountPerPage(20)
                .build()
            val result = session?.search(
                query,
                searchSpec
            )?:return@withContext emptyList()

            try {
                val page = result.nextPageAsync.get()
                page.mapNotNull {
                    if(it.genericDocument.schemaType==SearchProductHistory::class.java.simpleName) {

                        it.getDocument(SearchProductHistory::class.java)

                    }
                    else{
                        null
                    }
                }.sortedByDescending { it.timestamp }
            } catch (e: ExecutionException) {
                e.printStackTrace()
                emptyList()
            } catch (e: InterruptedException) {
                e.printStackTrace()
                emptyList()
            }


        }
    }

    fun  closeSession(){
        session?.close()
        session=null
    }
}
