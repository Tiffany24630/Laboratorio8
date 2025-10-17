package com.tiffany.salazar.laboratorio8.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.db.PhotoDao
import com.tiffany.salazar.laboratorio8.db.RecentQueryDao
import com.tiffany.salazar.laboratorio8.model.RecentQuery
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.tiffany.salazar.laboratorio8.network.PhotoApi

interface PhotoRepository {
    fun getPhotos(query: String): Flow<PagingData<Photo>>
    suspend fun getPhotoById(photoId: String): Photo?
    suspend fun updatePhoto(photo: Photo)
    suspend fun addRecentQuery(query: String)
    suspend fun getRecentQueries(limit: Int): List<String>
    suspend fun searchPhotosFromNetwork(query: String, page: Int): List<Photo>
}

class PhotoRepositoryImpl(
    private val photoDao: PhotoDao,
    private val recentQueryDao: RecentQueryDao,
    private val photoApi: PhotoApi
) : PhotoRepository {

    override fun getPhotos(query: String): Flow<PagingData<Photo>> {
        val normalizedQuery = if (query.isBlank()) "popular" else query.trim().lowercase()

        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { photoDao.pagingSourceForQuery(normalizedQuery) }
        ).flow.map { pagingData ->
            pagingData.map { photoEntity ->
                Photo(
                    id = photoEntity.id,
                    author = photoEntity.author,
                    width = photoEntity.width,
                    height = photoEntity.height,
                    thumbUrl = photoEntity.thumbUrl,
                    fullUrl = photoEntity.fullUrl,
                    likes = photoEntity.likes,
                    isFavorite = photoEntity.isFavorite,
                    queryKey = photoEntity.queryKey,
                    pageIndex = photoEntity.pageIndex,
                    updatedAt = photoEntity.updatedAt
                )
            }
        }
    }

    override suspend fun getPhotoById(photoId: String): Photo? {
        return photoDao.getPhotoById(photoId)?.let { photoEntity ->
            Photo(
                id = photoEntity.id,
                author = photoEntity.author,
                width = photoEntity.width,
                height = photoEntity.height,
                thumbUrl = photoEntity.thumbUrl,
                fullUrl = photoEntity.fullUrl,
                likes = photoEntity.likes,
                isFavorite = photoEntity.isFavorite,
                queryKey = photoEntity.queryKey,
                pageIndex = photoEntity.pageIndex,
                updatedAt = photoEntity.updatedAt
            )
        }
    }

    override suspend fun updatePhoto(photo: Photo) {
        val photoEntity = com.tiffany.salazar.laboratorio8.model.PhotoEntity(
            id = photo.id,
            author = photo.author,
            width = photo.width,
            height = photo.height,
            thumbUrl = photo.thumbUrl,
            fullUrl = photo.fullUrl,
            likes = photo.likes,
            isFavorite = photo.isFavorite,
            queryKey = photo.queryKey,
            pageIndex = photo.pageIndex,
            updatedAt = photo.updatedAt
        )
        photoDao.update(photoEntity)
    }

    override suspend fun addRecentQuery(query: String) {
        if (query.isNotBlank()) {
            recentQueryDao.upsert(RecentQuery(query = query.trim().lowercase()))
        }
    }

    override suspend fun getRecentQueries(limit: Int): List<String> {
        return recentQueryDao.getRecent(limit).map { it.query }
    }

    override suspend fun searchPhotosFromNetwork(query: String, page: Int): List<Photo> {
        val response = if (query == "popular") {
            photoApi.getPopularPhotos(page = page)
        } else {
            photoApi.searchPhotos(query = query, page = page)
        }

        return response.photos.map { pexelsPhoto ->
            Photo(
                id = pexelsPhoto.id.toString(),
                author = pexelsPhoto.photographer,
                width = pexelsPhoto.width,
                height = pexelsPhoto.height,
                thumbUrl = pexelsPhoto.src.medium,
                fullUrl = pexelsPhoto.src.large,
                likes = if (pexelsPhoto.liked) 1 else 0,
                isFavorite = false,
                queryKey = if (query == "popular") "popular" else query,
                pageIndex = page,
                updatedAt = System.currentTimeMillis()
            )
        }
    }

    override suspend fun getPhotoCountForQuery(query: String): Int {
        val normalizedQuery = if (query.isBlank()) "popular" else query.trim().lowercase()
        return photoDao.getPhotoCountForQuery(normalizedQuery)
    }
}