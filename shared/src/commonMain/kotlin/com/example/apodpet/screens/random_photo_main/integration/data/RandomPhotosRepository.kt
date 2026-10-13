package com.example.apodpet.screens.random_photo_main.integration.data

import com.example.apodpet.StringMessage
import com.example.apodpet.getErrorType
import database.NasaPhotoDao
import database.NasaPhotoDto
import database.NasaPhotoEntity
import database.currentTimeMillis
import kotlinx.coroutines.flow.Flow
import network.AppClient
import network.client_requests.getNasaRandomPhotos
import network.util.Result

sealed interface RandomPhotosResult {
    data class Success(val photoList: List<NasaPhotoDto>) : RandomPhotosResult
    data class Error(val error: StringMessage) : RandomPhotosResult
}

interface RandomPhotosRepository {
    suspend fun getRandomPhotos(count: Int = 15): RandomPhotosResult
    fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>>
    suspend fun savePhoto(dto: NasaPhotoDto)
    suspend fun deletePhoto(id: String)
}

class RandomPhotosRepositoryImpl(
    private val appClient: AppClient,
    private val photoDao: NasaPhotoDao
) : RandomPhotosRepository {

    override suspend fun getRandomPhotos(count: Int): RandomPhotosResult {
        return when (val res = appClient.getNasaRandomPhotos(count)) {
            is Result.Success -> RandomPhotosResult.Success(res.data)
            is Result.Error -> RandomPhotosResult.Error(getErrorType(res.error))
        }
    }

    override fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>> {
        return photoDao.observeSavedPhotos()
    }

    override suspend fun savePhoto(dto: NasaPhotoDto) {
        val id = dto.date ?: dto.url ?: return
        val entity = NasaPhotoEntity(
            id = id,
            title = dto.title ?: "NASA APOD",
            explanation = dto.explanation ?: "",
            url = dto.url ?: "",
            hdurl = dto.hdurl,
            date = dto.date ?: "",
            mediaType = dto.mediaType,
            copyright = dto.copyright,
            savedAt = currentTimeMillis()
        )
        photoDao.insertPhoto(entity)
    }

    override suspend fun deletePhoto(id: String) {
        photoDao.deletePhoto(id)
    }
}
