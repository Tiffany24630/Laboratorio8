package com.tiffany.salazar.laboratorio8.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.repository.PhotoRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: PhotoRepository) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _recentQueries = MutableStateFlow<List<String>>(emptyList())
    val recentQueries: StateFlow<List<String>> = _recentQueries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    @OptIn(FlowPreview::class)
    val photos: Flow<PagingData<Photo>> = _query
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                viewModelScope.launch {
                    repository.addRecentQuery(query)
                    loadRecentQueries()
                    // Cargar datos iniciales desde la red si es necesario
                    loadInitialDataFromNetwork(query)
                }
            }
            repository.getPhotos(query)
        }
        .cachedIn(viewModelScope)

    init {
        loadRecentQueries()
        // Cargar fotos populares al inicio
        _query.value = ""
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun selectRecentQuery(query: String) {
        _query.value = query
    }

    suspend fun toggleFavorite(photo: Photo) {
        val updatedPhoto = photo.copy(isFavorite = !photo.isFavorite)
        repository.updatePhoto(updatedPhoto)
    }

    private fun loadRecentQueries() {
        viewModelScope.launch {
            _recentQueries.value = repository.getRecentQueries(10)
        }
    }

    private suspend fun loadInitialDataFromNetwork(query: String) {
        // Solo cargar desde la red si no hay datos en cache
        val normalizedQuery = if (query.isBlank()) "popular" else query
        val cacheCount = repository.getPhotoCountForQuery(normalizedQuery)

        if (cacheCount == 0) {
            _isLoading.value = true
            try {
                val photos = repository.searchPhotosFromNetwork(normalizedQuery, 1)
                // Insertar en la base de datos (esto se haría en el repositorio normalmente)
                // Por simplicidad, aquí solo marcamos que se cargó
            } catch (e: Exception) {
                // Manejar error - la paginación mostrará lo que haya en cache
            } finally {
                _isLoading.value = false
            }
        }
    }

    interface PhotoRepository {
        fun getPhotos(query: String): Flow<PagingData<Photo>>
        suspend fun getPhotoById(photoId: String): Photo?
        suspend fun updatePhoto(photo: Photo)
        suspend fun addRecentQuery(query: String)
        suspend fun getRecentQueries(limit: Int): List<String>
        suspend fun searchPhotosFromNetwork(query: String, page: Int): List<Photo>
        suspend fun getPhotoCountForQuery(query: String): Int
    }
}