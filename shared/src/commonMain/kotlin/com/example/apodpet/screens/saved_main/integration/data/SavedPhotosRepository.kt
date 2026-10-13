package com.example.apodpet.screens.saved_main.integration.data

import database.NasaPhotoDao
import database.NasaPhotoEntity
import kotlinx.coroutines.flow.Flow

interface SavedPhotosRepository {
    fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>>
    suspend fun deletePhoto(id: String)
}

class SavedPhotosRepositoryImpl(
    private val photoDao: NasaPhotoDao
) : SavedPhotosRepository {

    override fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>> {
        return photoDao.observeSavedPhotos()
    }

    override suspend fun deletePhoto(id: String) {
        photoDao.deletePhoto(id)
    }
}
