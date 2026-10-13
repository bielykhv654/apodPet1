package com.example.apodpet

import apodpet.shared.generated.resources.Res
import apodpet.shared.generated.resources.error_bad_request
import apodpet.shared.generated.resources.error_default
import apodpet.shared.generated.resources.error_forbidden
import apodpet.shared.generated.resources.error_no_internet
import apodpet.shared.generated.resources.error_not_found
import apodpet.shared.generated.resources.error_serialization
import apodpet.shared.generated.resources.error_server
import apodpet.shared.generated.resources.error_too_many_requests
import apodpet.shared.generated.resources.error_unknown
import org.jetbrains.compose.resources.StringResource

interface StringMessage {
    val message: StringResource
}

fun getErrorType(errorCode: Int): ErrorType {
    return when (errorCode) {
        -1 -> ErrorType.UNKNOWN_ERROR
        1 -> ErrorType.NO_INTERNET
        2 -> ErrorType.SERIALIZATION_ERROR
        400 -> ErrorType.BAD_REQUEST
        429-> ErrorType.TOO_MANY_REQUESTS
        403 -> ErrorType.FORBIDDEN
        404 -> ErrorType.NOT_FOUND
        22 -> ErrorType.SERVER_ERROR
        else -> ErrorType.DEFAULT_ERROR
    }
}

enum class ErrorType : StringMessage {
    SERIALIZATION_ERROR {
        override val message: StringResource = Res.string.error_serialization
    },
    SERVER_ERROR {
        override val message: StringResource = Res.string.error_server
    },
    UNKNOWN_ERROR {
        override val message: StringResource = Res.string.error_unknown
    },
    NO_INTERNET {
        override val message: StringResource = Res.string.error_no_internet
    },
    BAD_REQUEST {
        override val message: StringResource = Res.string.error_bad_request
    },
    FORBIDDEN {
        override val message: StringResource = Res.string.error_forbidden
    },
    NOT_FOUND {
        override val message: StringResource = Res.string.error_not_found
    },
    TOO_MANY_REQUESTS {
        override val message: StringResource = Res.string.error_too_many_requests
    },
    DEFAULT_ERROR {
        override val message: StringResource = Res.string.error_default
    }
}
