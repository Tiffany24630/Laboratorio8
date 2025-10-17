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

    @OptIn(FlowPreview::class)
    val photos: Flow<PagingData<Photo>> = _query
        .debounce(500)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (query.isNotBlank()) {
                viewModelScope.launch {
                    repository.addRecentQuery(query)
                    loadRecentQueries()
                }
            }
            repository.getPhotos(query)
        }
        .cachedIn(viewModelScope)

    init {
        loadRecentQueries()
    }

    fun setQuery(newQuery: String) {
        _query.value = newQuery
    }

    fun selectRecentQuery(query: String) {
        _query.value = query
    }

    fun toggleFavorite(photo: Photo) {
        viewModelScope.launch {
            val updatedPhoto = photo.copy(isFavorite = !photo.isFavorite)
            repository.updatePhoto(updatedPhoto)
        }
    }

    private fun loadRecentQueries() {
        viewModelScope.launch {
            _recentQueries.value = repository.getRecentQueries(10)
        }
    }
}