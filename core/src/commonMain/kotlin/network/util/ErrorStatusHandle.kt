package network.util

enum class NetworkError {
    EMPTY,
    REQUEST_TIMEOUT,
    UNAUTHORIZED,
    CONFLICT,
    SERVER_ERROR,
    UNKNOWN,
    BAD_REQUEST,
    TOO_MANY_REQUESTS,
}
fun Int.errorStatusHandle(): NetworkError {
    return when (this) {
        in 200..299 -> {
            NetworkError.EMPTY
        }
        400-> NetworkError.BAD_REQUEST
        401 -> NetworkError.UNAUTHORIZED
        409 -> NetworkError.CONFLICT
        429 -> NetworkError.TOO_MANY_REQUESTS
        408 -> NetworkError.REQUEST_TIMEOUT
        in 500..599 -> NetworkError.SERVER_ERROR
        else -> NetworkError.UNKNOWN
    }
}