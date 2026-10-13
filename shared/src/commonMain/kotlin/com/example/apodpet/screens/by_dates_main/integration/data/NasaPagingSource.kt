package com.example.apodpet.screens.by_dates_main.integration.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.apodpet.StringMessage
import database.NasaPhotoDto

typealias NasaPageLoader = suspend (cursorDate: String?, pageSizeDays: Int) -> NextPagePhotosResult

class NasaPagingSource(
    private val loader: NasaPageLoader,
    private val onError: (StringMessage) -> Unit
) : PagingSource<String, NasaPhotoDto>() {

    override fun getRefreshKey(state: PagingState<String, NasaPhotoDto>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(anchorPosition) ?: return null
        return page.nextKey ?: page.prevKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, NasaPhotoDto> {
        val currentMinDate = params.key
        val pageSizeDays = params.loadSize.coerceAtMost(20)

        return when (val res = loader.invoke(currentMinDate, pageSizeDays)) {
            is NextPagePhotosResult.Success -> {
                LoadResult.Page(
                    data = res.photos,
                    prevKey = null,
                    nextKey = res.nextMinDate
                )
            }
            is NextPagePhotosResult.Error -> {
                onError.invoke(res.error)
                LoadResult.Error(Exception(res.error.toString()))
            }
        }
    }
}
