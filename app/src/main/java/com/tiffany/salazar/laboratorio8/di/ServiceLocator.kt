package com.tiffany.salazar.laboratorio8.di

import android.content.Context
import com.tiffany.salazar.laboratorio8.repository.PhotoRepository
import com.tiffany.salazar.laboratorio8.repository.SimplePhotoRepository

object ServiceLocator {
    private var repository: PhotoRepository? = null

    fun provideRepository(context: Context): PhotoRepository {
        return repository ?: synchronized(this) {
            val repo = SimplePhotoRepository()
            repository = repo
            repo
        }
    }
}