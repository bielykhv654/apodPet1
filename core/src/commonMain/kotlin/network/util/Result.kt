package network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlin.coroutines.cancellation.CancellationException

sealed interface Result<out D, out E> {
    data class Success<out D>(val data: D) : Result<D, Nothing>
    data class Error<out E>(val error: E) : Result<Nothing, E>
}

inline fun <T, E> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> this
        is Result.Success -> {
            action(data)
            this
        }
    }
}

inline fun <T, E> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> {
    return when (this) {
        is Result.Error -> {
            action(error)
            this
        }

        is Result.Success -> this
    }
}

internal suspend inline fun <reified T> safeRequest(
    request: () -> HttpResponse
): Result<T, Int> {
    val response = try {
        request()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        return e.exceptionHandle()
    }

    if (response.status.value >= 500) return Result.Error(SERVER_ERROR)
    val responseErrorStatus = response.status.value.errorStatusHandle()
    return if (responseErrorStatus == NetworkError.EMPTY) {
        if (T::class == Unit::class) {
            Result.Success(Unit as T)
        } else {
            Result.Success(response.body<T>())
        }
    } else {
        Result.Error(response.status.value)
    }
}

const val SERVER_ERROR = 22
