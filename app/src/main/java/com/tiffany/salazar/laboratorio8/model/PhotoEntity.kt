package com.tiffany.salazar.laboratorio8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad Photo extendida para soportar cache por query y paginado
@Entity(tableName = "photos")
data class PhotoEntity(
    @PrimaryKey val id: String,
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
