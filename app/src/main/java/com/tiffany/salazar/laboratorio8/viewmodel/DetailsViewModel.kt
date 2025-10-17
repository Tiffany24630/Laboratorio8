package com.tiffany.salazar.laboratorio8.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.repository.PhotoRepository

class DetailsViewModel(
    private val repository: PhotoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val photoId: String = savedStateHandle["photoId"] ?: ""

    private val _photoDetails = MutableStateFlow<Photo?>(null)
    val photoDetails: StateFlow<Photo?> = _photoDetails.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPhotoDetails()
    }

    fun loadById(id: String) {
        if (id != photoId) {
            // Recargar si el ID cambió
            loadPhotoDetails()
        }
    }

    private fun loadPhotoDetails() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 1. Intentar cargar desde la base de datos local
                var photo = repository.getPhotoById(photoId)

                // 2. Si no existe localmente, aquí podrías intentar cargar desde la API
                // y luego guardar en la base de datos
                // Por simplicidad, en esta implementación solo cargamos desde la DB

                _photoDetails.value = photo
            } catch (e: Exception) {
                // Manejar error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        _photoDetails.value?.let { currentPhoto ->
            val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
            _photoDetails.value = updatedPhoto
            viewModelScope.launch {
                repository.updatePhoto(updatedPhoto)
            }
        }
    }
}