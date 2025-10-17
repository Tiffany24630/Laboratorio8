package com.tiffany.salazar.laboratorio8.repository

import androidx.paging.PagingData
import com.tiffany.salazar.laboratorio8.data.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

interface PhotoRepository {
    fun getPhotos(query: String): Flow<PagingData<Photo>>
    suspend fun getPhotoById(photoId: String): Photo?
    suspend fun updatePhoto(photo: Photo)
    suspend fun addRecentQuery(query: String)
    suspend fun getRecentQueries(limit: Int): List<String>
}

class SimplePhotoRepository : PhotoRepository {

    // Datos de ejemplo para demostración
    private val samplePhotos = listOf(
        Photo(
            id = "1",
            author = "Photographer 1",
            width = 1920,
            height = 1080,
            thumbUrl = "https://picsum.photos/200/300?random=1",
            fullUrl = "https://picsum.photos/800/600?random=1",
            likes = 150,
            isFavorite = false
        ),
        Photo(
            id = "2",
            author = "Photographer 2",
            width = 1920,
            height = 1080,
            thumbUrl = "https://picsum.photos/200/300?random=2",
            fullUrl = "https://picsum.photos/800/600?random=2",
            likes = 230,
            isFavorite = true
        ),
        Photo(
            id = "3",
            author = "Photographer 3",
            width = 1920,
            height = 1080,
            thumbUrl = "https://picsum.photos/200/300?random=3",
            fullUrl = "https://picsum.photos/800/600?random=3",
            likes = 89,
            isFavorite = false
        ),
        Photo(
            id = "4",
            author = "Nature Photographer",
            width = 1920,
            height = 1080,
            thumbUrl = "https://picsum.photos/200/300?random=4",
            fullUrl = "https://picsum.photos/800/600?random=4",
            likes = 320,
            isFavorite = false
        ),
        Photo(
            id = "5",
            author = "Urban Photographer",
            width = 1920,
            height = 1080,
            thumbUrl = "https://picsum.photos/200/300?random=5",
            fullUrl = "https://picsum.photos/800/600?random=5",
            likes = 175,
            isFavorite = true
        )
    )

    private val recentQueries = mutableListOf<String>()
    private val favoritePhotos = mutableMapOf<String, Photo>()

    override fun getPhotos(query: String): Flow<PagingData<Photo>> {
        val filteredPhotos = if (query.isBlank()) {
            samplePhotos
        } else {
            samplePhotos.filter {
                it.author?.contains(query, ignoreCase = true) == true
            }
        }
        return flowOf(PagingData.from(filteredPhotos))
    }

    override suspend fun getPhotoById(photoId: String): Photo? {
        return samplePhotos.find { it.id == photoId }?.copy(
            isFavorite = favoritePhotos.containsKey(photoId)
        )
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
            // Mantener solo las últimas 10 consultas
            if (recentQueries.size > 10) {
                recentQueries.removeLast()
            }
        }
    }

    override suspend fun getRecentQueries(limit: Int): List<String> {
        return recentQueries.take(limit)
    }
}