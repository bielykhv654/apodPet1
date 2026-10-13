package network.util

import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException

fun Exception.exceptionHandle(): Result.Error<Int> =
    when (this) {
        is UnresolvedAddressException -> Result.Error(NETWORK_ERROR)
        is SerializationException -> Result.Error(SERIALIZATION_ERROR)
        else -> Result.Error(NETWORK_ERROR)
    }

private const val NETWORK_ERROR = 1
private const val SERIALIZATION_ERROR = 2
