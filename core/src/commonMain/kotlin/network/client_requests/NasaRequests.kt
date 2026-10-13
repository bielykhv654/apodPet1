package network.client_requests

import com.example.apodpet.core.BuildKonfig
import database.NasaPhotoDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import network.AppClient
import network.util.Result
import network.util.safeRequest

suspend fun AppClient.getNasaRandomPhotos(
    count: Int = 15,
    apiKey: String = BuildKonfig.NASA_API_KEY,
): Result<List<NasaPhotoDto>, Int> = safeRequest {
    httpClient.get("/planetary/apod") {
        parameter("api_key", apiKey)
        parameter("count", count)
    }
}

suspend fun AppClient.getNasaPhotosByRange(
    startDate: String,
    endDate: String,
    apiKey: String = BuildKonfig.NASA_API_KEY,
): Result<List<NasaPhotoDto>, Int> = safeRequest {
    httpClient.get("/planetary/apod") {
        parameter("api_key", apiKey)
        parameter("start_date", startDate)
        parameter("end_date", endDate)
    }
}
