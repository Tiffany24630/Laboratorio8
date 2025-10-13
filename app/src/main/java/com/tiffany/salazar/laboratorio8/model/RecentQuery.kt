package com.tiffany.salazar.laboratorio8.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_queries")
data class RecentQuery(
    @PrimaryKey val query: String,
    val lastUsedAt: Long = System.currentTimeMillis()
)
