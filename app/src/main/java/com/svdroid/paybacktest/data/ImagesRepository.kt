package com.svdroid.paybacktest.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.svdroid.paybacktest.data.api.PixabayService
import com.svdroid.paybacktest.data.db.PixabayDatabase
import javax.inject.Inject

class ImagesRepository @Inject constructor(
    private val pixabayService: PixabayService,
    private val pixabayDatabase: PixabayDatabase,
) {
    @OptIn(ExperimentalPagingApi::class)
    fun searchImages(query: String) = Pager(
        config = PagingConfig(
            pageSize = 100,
            prefetchDistance = 10,
            initialLoadSize = 1
        ),
        pagingSourceFactory = {
            pixabayDatabase.hitDao().getAll(searchQuery = query)
        },
        remoteMediator = PixabayRemoteMediator(
            pixabayService,
            pixabayDatabase,
            query
        )
    ).flow
}