package com.tiffany.salazar.laboratorio8.data

data class Photo(
    val id: String,
    val author: String? = "Desconocido",
    val width: Int? = 0,
    val height: Int? = 0,
    val thumbUrl: String,
    val fullUrl: String,
    val likes: Int? = 0,
    var isFavorite: Boolean = false,
    val queryKey: String? = null,
    val pageIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis()
)