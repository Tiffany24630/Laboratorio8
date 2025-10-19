package com.tiffany.salazar.laboratorio8.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.tiffany.salazar.laboratorio8.data.Photo
import com.tiffany.salazar.laboratorio8.network.PhotoApi

class PhotoPagingSource(
    private val photoApi: PhotoApi,
    private val query: String,
    private val memoryCache: MutableMap<String, List<Photo>>,
    private val apiKey: String
) : PagingSource<Int, Photo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        return try {
            val page = params.key ?: 1

            val response = if (query.isBlank()) {
                photoApi.getPopularPhotos(apiKey, page, params.loadSize)
            } else {
                photoApi.searchPhotos(apiKey, query, page, params.loadSize)
            }

            val photos = response.photos.map { pexelsPhoto ->
                Photo(
                    id = pexelsPhoto.id.toString(),
                    author = pexelsPhoto.photographer,
                    width = pexelsPhoto.width,
                    height = pexelsPhoto.height,
                    thumbUrl = pexelsPhoto.src.medium,
                    fullUrl = pexelsPhoto.src.large,
                    likes = 0,
                    isFavorite = false,
                    queryKey = query
                )
            }

            // Guardar en cache por query
            val cacheKey = if (query.isBlank()) "popular" else query
            memoryCache[cacheKey] = (memoryCache[cacheKey] ?: emptyList()) + photos

            LoadResult.Page(
                data = photos,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (response.next_page == null) null else page + 1
            )
        } catch (e: Exception) {
            // Fallback a cache si hay error de red
            val cacheKey = if (query.isBlank()) "popular" else query
            val cachedPhotos = memoryCache[cacheKey] ?: emptyList()

            LoadResult.Page(
                data = cachedPhotos,
                prevKey = null,
                nextKey = null
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}