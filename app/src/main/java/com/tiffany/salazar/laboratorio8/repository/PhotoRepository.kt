package com.tiffany.salazar.laboratorio8.data

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

// --- INTERFAZ ---
interface PhotoRepository {
    fun getPhotos(query: String): Flow<PagingData<Photo>>
    suspend fun getPhotoById(photoId: String): Photo?
    suspend fun updatePhoto(photo: Photo)
}

// --- IMPLEMENTACIÓN (con datos de ejemplo) ---
class PhotoRepositoryImpl : PhotoRepository {

    // Simula una base de datos en memoria para el ejemplo
    private val photoCache = mutableListOf(
        Photo("1", "Autor A", 1080, 1920, "https://picsum.photos/id/1/400/600", 100, false),
        Photo("2", "Autor B", 1920, 1080, "https://picsum.photos/id/2/400/600", 250, true),
        Photo("3", "Autor C", 800, 600, "https://picsum.photos/id/3/400/600", 50, false)
    )

    override fun getPhotos(query: String): Flow<PagingData<Photo>> {
        // En una implementación real, aquí usarías Pager con Room y RemoteMediator.
        // Para este ejemplo, simplemente devolvemos los datos cacheados.
        val filteredList = if (query.isBlank()) {
            photoCache
        } else {
            photoCache.filter { it.author?.contains(query, ignoreCase = true) == true }
        }
        return flowOf(PagingData.from(filteredList))
    }

    override suspend fun getPhotoById(photoId: String): Photo? {
        // Busca en la caché. En una app real, buscarías en Room y luego en la API.
        return photoCache.find { it.id == photoId }
    }

    override suspend fun updatePhoto(photo: Photo) {
        // Actualiza la foto en la caché. En una app real, actualizarías la base de datos de Room.
        val index = photoCache.indexOfFirst { it.id == photo.id }
        if (index != -1) {
            photoCache[index] = photo
        }
    }
}
