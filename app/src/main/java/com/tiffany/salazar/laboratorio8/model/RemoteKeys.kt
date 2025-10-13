package com.tiffany.salazar.laboratorio8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey val query: String,
    val prevKey: Int?,
    val nextKey: Int?
)
