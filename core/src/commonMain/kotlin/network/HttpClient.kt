package network

import com.example.apodpet.core.BuildKonfig
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json



class AppClient (val httpClient: HttpClient)
@OptIn(InternalAPI::class)
internal fun createHttpClient() = HttpClient {

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }

    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
    install(HttpTimeout) {
        connectTimeoutMillis = 30000
        requestTimeoutMillis = 30000
        socketTimeoutMillis = 30000
    }

    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
                host = BuildKonfig.BASE_URL
        }
        header("Content-Type", "application/json")
    }
}
