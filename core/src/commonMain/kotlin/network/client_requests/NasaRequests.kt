package network.client_requests

import database.NasaPhotoDto
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import network.AppClient
import network.util.Result
import network.util.safeRequest

/**
 * Converts YYYY-MM-DD or YYYYMMDD date strings to legacy YYMMDD format expected by the APOD Basic API.
 * e.g., "2014-09-18" -> "140918"
 */
fun String.toLegacyApodDate(): String {
    val digitsOnly = this.replace("-", "").replace("/", "")
    return if (digitsOnly.length == 8) {
        digitsOnly.substring(2)
    } else {
        this
    }
}

/**
 * Fetches 15 random APOD entries by selecting a random page from the science.nasa.gov APOD Basic API.
 */
suspend fun AppClient.getNasaRandomPhotos(
    count: Int = 15,
): Result<List<NasaPhotoDto>, Int> = safeRequest {
    httpClient.get("/wp-json/wp/v2/apod-basic") {
        parameter("page", (1..50).random())
        parameter("per_page", count)
    }
}

/**
 * Fetches APOD entries within a specified date range using date_from and date_to (legacy YYMMDD format).
 */
suspend fun AppClient.getNasaPhotosByRange(
    startDate: String,
    endDate: String,
): Result<List<NasaPhotoDto>, Int> = safeRequest {
    httpClient.get("/wp-json/wp/v2/apod-basic") {
        parameter("date_from", startDate.toLegacyApodDate())
        parameter("date_to", endDate.toLegacyApodDate())
        parameter("per_page", 25)
    }
}
