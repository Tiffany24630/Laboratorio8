package com.tiffany.salazar.laboratorio8.repository

import androidx.paging.PagingData
import com.tiffany.salazar.laboratorio8.data.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    fun getPhotos(query: String): Flow<PagingData<Photo>>
    suspend fun getPhotoById(photoId: String): Photo?
    suspend fun updatePhoto(photo: Photo)
    suspend fun addRecentQuery(query: String)
    suspend fun getRecentQueries(limit: Int): List<String>
}