package database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NasaPhotoDto(
    @SerialName("date") val date: String? = null,
    @SerialName("explanation") val explanation: String? = null,
    @SerialName("hdurl") val hdurl: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("service_version") val serviceVersion: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("copyright") val copyright: String? = null
)
