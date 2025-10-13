package com.tiffany.salazar.laboratorio8 // Asegúrate que el package sea correcto

// --- IMPORTACIONES CORREGIDAS ---
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow // <-- SOLUCIÓN 1: Importar Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

// Esta interfaz debería estar en su propio archivo (ej. data/PhotoRepository.kt)
// para evitar redefiniciones.
interface PhotoRepository {
    fun getPhotos(query: String): Flow<PagingData<Photo>>
    suspend fun getPhotoByIdFromDb(photoId: String): Flow<Photo?>
    suspend fun getPhotoByIdFromApi(photoId: String): Photo?
    suspend fun insertPhoto(photo: Photo)
    suspend fun updatePhoto(photo: Photo)
}

// Esta clase de datos debería estar en su propio archivo (ej. data/Photo.kt)
// La he añadido aquí temporalmente para que el código compile.
data class Photo(
    val id: String,
    val author: String,
    // ... otros campos
    val isFavorite: Boolean // <-- SOLUCIÓN 2: Asegúrate que el campo exista
)

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

            // Aquí el compilador ya puede inferir el tipo
            var photo: Photo? = photoRepository.getPhotoByIdFromDb(photoId).firstOrNull()

            if (photo == null) {
                photo = photoRepository.getPhotoByIdFromApi(photoId)
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
            // La copia ahora funcionará porque el campo isFavorite existe
            val updatedPhoto = currentPhoto.copy(isFavorite = !currentPhoto.isFavorite)
            _photoDetails.value = updatedPhoto
            viewModelScope.launch {
                photoRepository.updatePhoto(updatedPhoto)
            }
        }
    }
}
