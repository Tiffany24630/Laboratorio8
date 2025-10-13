package com.tiffany.salazar.laboratorio8.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiffany.salazar.laboratorio8.model.RecentQuery

@Dao
interface RecentQueryDao {
    @Query("SELECT * FROM recent_queries ORDER BY lastUsedAt DESC LIMIT :limit")
    suspend fun getRecent(limit: Int): List<RecentQuery>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(query: RecentQuery)
}
