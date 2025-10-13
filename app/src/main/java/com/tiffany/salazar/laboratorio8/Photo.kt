package com.tiffany.salazar.laboratorio8.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "photos")
data class Photo(
    @PrimaryKey val id: String,
    val author: String? = "Desconocido",
    val width: Int? = 0,
    val height: Int? = 0,
    val url: String,
    val likes: Int? = 0,
    var isFavorite: Boolean = false
)
