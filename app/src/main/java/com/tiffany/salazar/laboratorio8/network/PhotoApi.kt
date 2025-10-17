package com.tiffany.salazar.laboratorio8.network

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// Interfaz Retrofit para Pexels API
interface PhotoApi {
    @GET("v1/search")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PexelsSearchResponse

    @GET("v1/curated")
    suspend fun getPopularPhotos(
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30
    ): PexelsSearchResponse
}

// Modelos para la respuesta de Pexels
data class PexelsSearchResponse(
    val page: Int,
    val per_page: Int,
    val photos: List<PexelsPhoto>,
    val total_results: Int,
    val next_page: String?
)

data class PexelsPhoto(
    val id: Int,
    val width: Int,
    val height: Int,
    val url: String,
    val photographer: String,
    val photographer_url: String,
    val photographer_id: Int,
    val avg_color: String,
    val src: PhotoSources,
    val liked: Boolean
)

data class PhotoSources(
    val original: String,
    val large2x: String,
    val large: String,
    val medium: String,
    val small: String,
    val portrait: String,
    val landscape: String,
    val tiny: String
)