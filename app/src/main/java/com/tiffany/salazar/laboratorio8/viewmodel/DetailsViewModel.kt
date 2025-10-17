package com.tiffany.salazar.laboratorio8.viewmodel

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
            try {
                val photo = repository.getPhotoById(photoId)
                _photoDetails.value = photo
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _photoDetails.value?.let { currentPhoto ->
                val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
                _photoDetails.value = updatedPhoto
                repository.updatePhoto(updatedPhoto)
            }
        }
    }
}