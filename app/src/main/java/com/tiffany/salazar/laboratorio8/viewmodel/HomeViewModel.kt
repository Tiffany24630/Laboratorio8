package com.tiffany.salazar.laboratorio8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.data.PhotoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PhotoRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    @OptIn(FlowPreview::class)
    val photos: Flow<PagingData<Photo>> = _query
        .debounce(500)
        .flatMapLatest { query ->
            repository.getPhotos(query)
        }
        .cachedIn(viewModelScope) // Cachea los resultados en el ViewModelScope

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    suspend fun toggleFavorite(photo: Photo) {
        val updatedPhoto = photo.copy(isFavorite = !photo.isFavorite)
        repository.updatePhoto(updatedPhoto)
    }
}
