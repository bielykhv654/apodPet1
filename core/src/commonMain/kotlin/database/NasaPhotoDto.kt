package database

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NasaPhotoDto(
    @SerialName("date") val date: String? = null,
    @SerialName("explanation") val explanation: String? = null,
    @SerialName("hdurl") val hdurl: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("copyright") val copyright: String? = null
)

fun String.cleanHtmlText(): String {
    return this
        .replace(Regex("<[^>]*>"), "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .replace("&#8217;", "'")
        .replace("&#8216;", "'")
        .replace("&#8220;", "\"")
        .replace("&#8221;", "\"")
        .replace("&#8211;", "–")
        .replace("&#8212;", "—")
        .replace(Regex("^Explanation:\\s*", RegexOption.IGNORE_CASE), "")
        .replace(Regex("^©\\s*"), "")
        .replace("\n", " ")
        .replace("\r", " ")
        .replace(Regex("\\s+"), " ")
        .trim()
}

fun NasaPhotoDto.cleaned(): NasaPhotoDto = copy(
    title = title?.cleanHtmlText(),
    explanation = explanation?.cleanHtmlText(),
    copyright = copyright?.cleanHtmlText()
)
