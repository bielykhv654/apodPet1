package database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "nasa_photos")
data class NasaPhotoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val explanation: String,
    val url: String,
    val hdurl: String? = null,
    val date: String,
    val mediaType: String? = null,
    val copyright: String? = null,
    val savedAt: Long = 0L
)
