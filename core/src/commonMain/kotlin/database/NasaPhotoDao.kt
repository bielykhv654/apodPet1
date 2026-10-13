package database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NasaPhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhoto(photo: NasaPhotoEntity)

    @Query("DELETE FROM nasa_photos WHERE id = :id")
    suspend fun deletePhoto(id: String)

    @Query("SELECT * FROM nasa_photos ORDER BY savedAt DESC")
    fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM nasa_photos WHERE id = :id)")
    suspend fun isPhotoSaved(id: String): Boolean
}
