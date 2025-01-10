package dev.kevalkanpariya.swipetakehomeassign.domain.utils

import dev.kevalkanpariya.swipetakehomeassign.domain.models.RootError
import dev.kevalkanpariya.swipetakehomeassign.domain.models.Result
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import dev.kevalkanpariya.swipetakehomeassign.domain.models.DataError

suspend fun <T, E : RootError> Result<T, E>.handleResult(
    onSuccess: suspend (T?) -> Unit,
    onError: suspend (E?) -> Unit = {}
) {

    when (this) {
        is Result.Success -> onSuccess(
            this.data
        )

        is Result.Error -> onError(this.error)
    }
}

fun <T> handleHttpResponseException(e: ResponseException): Result<T, DataError> {
    return when (e.response.status) {

        HttpStatusCode.BadRequest -> {
            Result.Error(DataError.Network.BAD_REQUEST)
        }
        HttpStatusCode.Unauthorized -> {
            Result.Error(DataError.Network.UNAUTHORIZED)
        }
        HttpStatusCode.Forbidden -> {
            Result.Error(DataError.Network.FORBIDDEN)
        }
        HttpStatusCode.NotFound -> {
            Result.Error(DataError.Network.NOT_FOUND)
        }
        HttpStatusCode.PayloadTooLarge -> {
            Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
        }
        HttpStatusCode.RequestTimeout -> {
            Result.Error(DataError.Network.REQUEST_TIMEOUT)
        }
        HttpStatusCode.UnsupportedMediaType -> {
            Result.Error(DataError.Network.UNSUPPORTED_MEDIA_TYPE)
        }
        HttpStatusCode.Conflict -> {
            Result.Error(DataError.Network.CONFLICT)
        }
        HttpStatusCode.InternalServerError -> {
            Result.Error(DataError.Network.INTERNAL_SERVER_ERROR)
        }
        HttpStatusCode.ServiceUnavailable -> {
            Result.Error(DataError.Network.SERVICE_UNAVAILABLE)
        }
        HttpStatusCode.GatewayTimeout -> {
            Result.Error(DataError.Network.GATEWAY_TIMEOUT)
        }
        else -> {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}
