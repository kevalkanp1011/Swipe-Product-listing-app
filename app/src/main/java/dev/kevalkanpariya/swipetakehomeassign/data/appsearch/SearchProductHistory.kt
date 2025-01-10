package dev.kevalkanpariya.swipetakehomeassign.data.appsearch

import androidx.appsearch.annotation.Document
import androidx.appsearch.annotation.Document.Id
import androidx.appsearch.annotation.Document.Namespace
import androidx.appsearch.annotation.Document.StringProperty
import androidx.appsearch.app.AppSearchSchema.StringPropertyConfig.INDEXING_TYPE_PREFIXES


@Document
data class SearchProductHistory(
    @Namespace
    val nameSpace: String,
    @StringProperty(indexingType = INDEXING_TYPE_PREFIXES)
    val history: String,
    @Id
    val historyId: String,
    @Document.LongProperty
    val timestamp: Long,

)
