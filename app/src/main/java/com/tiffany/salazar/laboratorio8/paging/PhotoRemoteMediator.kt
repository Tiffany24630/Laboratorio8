package com.tiffany.salazar.laboratorio8.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.tiffany.salazar.laboratorio8.db.AppDatabase
import com.tiffany.salazar.laboratorio8.model.PhotoEntity
import com.tiffany.salazar.laboratorio8.model.RemoteKeys
import com.tiffany.salazar.laboratorio8.network.PhotoApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator(
    private val query: String,
    private val photoApi: PhotoApi,
    private val database: AppDatabase,
    private val clientId: String? = null,
    private val perPage: Int = 30
) : RemoteMediator<Int, PhotoEntity>() {

    private val photoDao = database.photoDao()
    private val remoteKeysDao = database.remoteKeysDao()

    override suspend fun load(loadType: LoadType, state: PagingState<Int, PhotoEntity>): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val keys = remoteKeysDao.remoteKeysByQuery(query)
                keys?.nextKey ?: 1
            }
        }

        try {
            val response = photoApi.searchPhotos(query, page, perPage, clientId)
            val photos = response.results.mapIndexed { index, p ->
                PhotoEntity(
                    id = p.id,
                    author = p.user.name,
                    width = p.width,
                    height = p.height,
                    thumbUrl = p.urls.thumb,
                    fullUrl = p.urls.full,
                    likes = p.likes,
                    isFavorite = false,
                    queryKey = query,
                    pageIndex = page,
                    updatedAt = System.currentTimeMillis()
                )
            }

            val endOfPagination = response.results.isEmpty() || page >= response.total_pages

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDao.clearByQuery(query)
                    remoteKeysDao.clearKeysForQuery(query)
                }
                photoDao.insertAll(photos)
                val nextKey = if (endOfPagination) null else page + 1
                remoteKeysDao.insertKeys(RemoteKeys(query = query, prevKey = if (page==1) null else page-1, nextKey = nextKey))
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPagination)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}
