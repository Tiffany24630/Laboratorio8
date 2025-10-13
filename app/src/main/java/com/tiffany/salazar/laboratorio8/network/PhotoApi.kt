package com.tiffany.salazar.laboratorio8.network

import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

// Interfaz Retrofit para Unsplash (ejemplo). Necesitas añadir tu API key en header o parámetro.
interface PhotoApi {
    @Headers("Accept: application/json")
    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int = 30,
        @Query("client_id") clientId: String? = null // o usar header con Authorization
    ): UnsplashSearchResponse
}

// Modelos mínimos para la respuesta
data class UnsplashSearchResponse(
    val total: Int,
    val total_pages: Int,
    val results: List<UnsplashPhoto>
)

data class UnsplashPhoto(
    val id: String,
    val width: Int?,
    val height: Int?,
    val urls: Urls,
    val user: User,
    val likes: Int?
)

data class Urls(val thumb: String, val small: String, val regular: String, val full: String)
data class User(val name: String)
