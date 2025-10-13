package com.tiffany.salazar.laboratorio8.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tiffany.salazar.laboratorio8.model.RemoteKeys

@Dao
interface RemoteKeysDao {
    @Query("SELECT * FROM remote_keys WHERE query = :query LIMIT 1")
    suspend fun remoteKeysByQuery(query: String): RemoteKeys?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeys(keys: RemoteKeys)

    @Query("DELETE FROM remote_keys WHERE query = :query")
    suspend fun clearKeysForQuery(query: String)
}
