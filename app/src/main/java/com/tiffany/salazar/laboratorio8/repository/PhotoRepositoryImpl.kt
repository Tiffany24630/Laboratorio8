package com.tiffany.salazar.laboratorio8.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.network.PhotoApi
import kotlinx.coroutines.flow.Flow

class PhotoRepositoryImpl(
    private val photoApi: PhotoApi,
    private val apiKey: String
) : PhotoRepository {

    private val recentQueries = mutableListOf<String>()
    private val favoritePhotos = mutableMapOf<String, Photo>()
    private val memoryCache = mutableMapOf<String, List<Photo>>()

    override fun getPhotos(query: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PhotoPagingSource(photoApi, query, memoryCache, apiKey)
            }
        ).flow
    }

    override suspend fun getPhotoById(photoId: String): Photo? {
        // Buscar en favoritos primero
        favoritePhotos[photoId]?.let { return it }

        // Buscar en cache
        memoryCache.values.flatten().find { it.id == photoId }?.let { photo ->
            return photo.copy(isFavorite = favoritePhotos.containsKey(photoId))
        }

        return null
    }

    override suspend fun updatePhoto(photo: Photo) {
        if (photo.isFavorite) {
            favoritePhotos[photo.id] = photo
        } else {
            favoritePhotos.remove(photo.id)
        }
    }

    override suspend fun addRecentQuery(query: String) {
        if (query.isNotBlank()) {
            recentQueries.remove(query)
            recentQueries.add(0, query)
            if (recentQueries.size > 10) {
                recentQueries.removeLast()
            }
        }
    }

    override suspend fun getRecentQueries(limit: Int): List<String> {
        return recentQueries.take(limit)
    }
}