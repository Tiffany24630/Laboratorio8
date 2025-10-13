package com.tiffany.salazar.laboratorio8

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Asumiendo que tienes un PhotoRepository. Esta es una interfaz de ejemplo.
interface PhotoRepository {
    suspend fun getPhotoByIdFromDb(photoId: String): Flow<Photo?>
    suspend fun getPhotoByIdFromApi(photoId: String): Photo?
    suspend fun insertPhoto(photo: Photo)
    suspend fun updatePhoto(photo: Photo)
}


class DetailsViewModel(
    private val photoRepository: PhotoRepository,
    private val photoId: String
) : ViewModel() {

    private val _photoDetails = MutableStateFlow<Photo?>(null)
    val photoDetails: StateFlow<Photo?> = _photoDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPhotoDetails()
    }

    private fun loadPhotoDetails() {
        viewModelScope.launch {
            _isLoading.value = true

            // 1. Carga preferentemente desde Room
            var photo = photoRepository.getPhotoByIdFromDb(photoId).firstOrNull()

            // 2. Si no existe, intenta desde la red
            if (photo == null) {
                photo = photoRepository.getPhotoByIdFromApi(photoId)
                // 3. Si se obtiene de la red, persiste localmente
                photo?.let {
                    photoRepository.insertPhoto(it)
                }
            }

            _photoDetails.value = photo
            _isLoading.value = false
        }
    }

    fun toggleFavorite() {
        _photoDetails.value?.let { currentPhoto ->
            val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
            _photoDetails.value = updatedPhoto // Actualiza la UI inmediatamente
            viewModelScope.launch {
                photoRepository.updatePhoto(updatedPhoto) // Persiste el cambio en la BD
            }
        }
    }
}
