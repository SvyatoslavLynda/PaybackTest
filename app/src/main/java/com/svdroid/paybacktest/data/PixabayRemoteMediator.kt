package com.svdroid.paybacktest.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import retrofit2.HttpException
import java.io.IOException
import androidx.room.withTransaction
import com.svdroid.paybacktest.data.api.Hit
import com.svdroid.paybacktest.data.api.PixabayService
import com.svdroid.paybacktest.data.db.PixabayDatabase
import com.svdroid.paybacktest.data.db.RemoteKeys
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class PixabayRemoteMediator(
    private val pixabayApiService: PixabayService,
    private val pixabayDatabase: PixabayDatabase,
    private val searchQuery: String,
) : RemoteMediator<Int, Hit>() {

    override suspend fun initialize(): InitializeAction {
        val cacheTimeout = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

        return if (System.currentTimeMillis() - (pixabayDatabase.getRemoteKeysDao().getCreationTime(searchQuery)
                ?: 0) < cacheTimeout
        ) {
            // Cached data is up-to-date, so there is no need to re-fetch
            // from the network.
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            // Need to refresh cached data from network; returning
            // LAUNCH_INITIAL_REFRESH here will also block RemoteMediator's
            // APPEND and PREPEND from running until REFRESH succeeds.
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Hit>): RemoteKeys? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let {
            pixabayDatabase.getRemoteKeysDao().getRemoteKeyByHitID(searchQuery)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Hit>): RemoteKeys? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let {
            pixabayDatabase.getRemoteKeysDao().getRemoteKeyByHitID(searchQuery)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Hit>): RemoteKeys? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let {
                pixabayDatabase.getRemoteKeysDao().getRemoteKeyByHitID(searchQuery)
            }
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Hit>
    ): MediatorResult {
        val page: Int = when (loadType) {
            LoadType.REFRESH -> {
                //New Query so clear the DB
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                // If remoteKeys is null, that means the refresh result is not in the database yet.
                val prevKey = remoteKeys?.prevKey
                prevKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)

                // If remoteKeys is null, that means the refresh result is not in the database yet.
                // We can return Success with endOfPaginationReached = false because Paging
                // will call this method again if RemoteKeys becomes non-null.
                // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                // the end of pagination for append.
                val nextKey = remoteKeys?.nextKey
                nextKey ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
            }
        }

        try {
            val apiResponse = pixabayApiService.getAllHits(page = page, query = searchQuery)
            val hits = apiResponse.hits
            val endOfPaginationReached = hits.isEmpty()

            pixabayDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    pixabayDatabase.getRemoteKeysDao().clearRemoteKeys()
                    pixabayDatabase.hitDao().clearAll(searchQuery)
                }
                val prevKey = if (page > 1) page - 1 else null
                val nextKey = if (endOfPaginationReached) null else page + 1
                val remoteKeys = listOf(
                    RemoteKeys(
                        prevKey = prevKey,
                        currentPage = page,
                        nextKey = nextKey,
                        searchQuery = searchQuery,
                    )
                )

                pixabayDatabase.getRemoteKeysDao().insertAll(remoteKeys)
                pixabayDatabase.hitDao().insertAll(hits.map { it.copy(searchQuery = searchQuery) })
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (error: IOException) {
            return MediatorResult.Error(error)
        } catch (error: HttpException) {
            return MediatorResult.Error(error)
        }
    }
}