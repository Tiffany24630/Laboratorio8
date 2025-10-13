package com.tiffany.salazar.laboratorio8.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tiffany.salazar.laboratorio8.model.PhotoEntity
import com.tiffany.salazar.laboratorio8.model.RecentQuery
import com.tiffany.salazar.laboratorio8.model.RemoteKeys

@Database(entities = [PhotoEntity::class, RecentQuery::class, RemoteKeys::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun photoDao(): PhotoDao
    abstract fun recentQueryDao(): RecentQueryDao
    abstract fun remoteKeysDao(): RemoteKeysDao
}
