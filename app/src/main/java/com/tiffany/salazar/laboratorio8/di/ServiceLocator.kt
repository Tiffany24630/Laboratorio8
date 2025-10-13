package com.tiffany.salazar.laboratorio8.di

import android.content.Context
import com.tiffany.salazar.laboratorio8.data.PhotoRepository
import com.tiffany.salazar.laboratorio8.data.PhotoRepositoryImpl

// Objeto Singleton para la inyección manual de dependencias
object ServiceLocator {
    private var repository: PhotoRepository? = null

    fun provideRepository(context: Context): PhotoRepository {
        return repository ?: synchronized(this) {
            // Crea una única instancia del repositorio.
            val repo = PhotoRepositoryImpl()
            repository = repo
            repo
        }
    }
}
