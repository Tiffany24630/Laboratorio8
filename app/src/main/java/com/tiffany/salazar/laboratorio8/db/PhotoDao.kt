package com.tiffany.salazar.laboratorio8.db

import androidx.paging.PagingSource
import androidx.room.*
import com.tiffany.salazar.laboratorio8.model.PhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("""
        SELECT * FROM photos
        WHERE queryKey = :queryKey
        ORDER BY pageIndex ASC, id ASC
    """)
    fun pagingSourceForQuery(queryKey: String): PagingSource<Int, PhotoEntity>

    @Query("SELECT * FROM photos WHERE id = :id LIMIT 1")
    suspend fun getPhotoById(id: String): PhotoEntity?

    @Query("SELECT * FROM photos WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavorites(): Flow<List<PhotoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Update
    suspend fun update(photo: PhotoEntity)

    @Query("DELETE FROM photos WHERE queryKey = :queryKey")
    suspend fun clearByQuery(queryKey: String)

    @Query("SELECT COUNT(*) FROM photos WHERE queryKey = :queryKey")
    suspend fun getPhotoCountForQuery(queryKey: String): Int
}