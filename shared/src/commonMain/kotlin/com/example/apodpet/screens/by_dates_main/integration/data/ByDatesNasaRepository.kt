package com.example.apodpet.screens.by_dates_main.integration.data

import androidx.paging.PagingSource
import com.example.apodpet.StringMessage
import com.example.apodpet.getErrorType
import database.NasaPhotoDao
import database.NasaPhotoDto
import database.NasaPhotoEntity
import database.currentTimeMillis
import database.getTodayDateString
import database.minusDays
import kotlinx.coroutines.flow.Flow
import network.AppClient
import network.client_requests.getNasaPhotosByRange
import network.util.Result

const val APOD_START_DATE = "1995-06-16"

sealed interface NextPagePhotosResult {
    data class Success(val photos: List<NasaPhotoDto>, val nextMinDate: String?) : NextPagePhotosResult
    data class Error(val error: StringMessage) : NextPagePhotosResult
}

interface ByDatesNasaRepository {
    fun getPhotosPagingSource(onError: (StringMessage) -> Unit): PagingSource<String, NasaPhotoDto>
    suspend fun getNextPhotoPage(currentMinDate: String?, pageSizeDays: Int = 20): NextPagePhotosResult
    fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>>
    suspend fun savePhoto(dto: NasaPhotoDto)
    suspend fun deletePhoto(id: String)
}

class ByDatesNasaRepositoryImpl(
    private val appClient: AppClient,
    private val photoDao: NasaPhotoDao
) : ByDatesNasaRepository {

    override fun getPhotosPagingSource(onError: (StringMessage) -> Unit): PagingSource<String, NasaPhotoDto> {
        val loader: NasaPageLoader = { cursorDate, pageSizeDays ->
            getNextPhotoPage(cursorDate, pageSizeDays)
        }
        return NasaPagingSource(loader, onError)
    }

    override suspend fun getNextPhotoPage(
        currentMinDate: String?,
        pageSizeDays: Int
    ): NextPagePhotosResult {
        val today = getTodayDateString()
        var endDate = currentMinDate ?: today

        if (endDate > today) {
            endDate = today
        }

        if (endDate < APOD_START_DATE) {
            return NextPagePhotosResult.Success(emptyList(), null)
        }

        var startDate = minusDays(endDate, pageSizeDays)
        if (startDate < APOD_START_DATE) {
            startDate = APOD_START_DATE
        }
        if (startDate > endDate) {
            startDate = endDate
        }

        return when (val res = appClient.getNasaPhotosByRange(startDate, endDate)) {
            is Result.Success -> {
                val filtered = res.data.reversed().filter { it.url != null }
                val nextMinDate = if (startDate > APOD_START_DATE) {
                    minusDays(startDate, 1)
                } else {
                    null
                }
                NextPagePhotosResult.Success(filtered, nextMinDate)
            }
            is Result.Error -> NextPagePhotosResult.Error(getErrorType(res.error))
        }
    }

    override fun observeSavedPhotos(): Flow<List<NasaPhotoEntity>> {
        return photoDao.observeSavedPhotos()
    }

    override suspend fun savePhoto(dto: NasaPhotoDto) {
        val entity = dto.toEntity() ?: return
        photoDao.insertPhoto(entity)
    }

    override suspend fun deletePhoto(id: String) {
        photoDao.deletePhoto(id)
    }
}

private fun NasaPhotoDto.toEntity(): NasaPhotoEntity? {
    val id = date ?: url ?: return null
    return NasaPhotoEntity(
        id = id,
        title = title ?: "NASA APOD",
        explanation = explanation ?: "",
        url = hdurl ?: url ?: "",
        hdurl = hdurl,
        date = date ?: "",
        mediaType = mediaType,
        copyright = copyright,
        savedAt = currentTimeMillis()
    )
}
