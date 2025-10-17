package com.tiffany.salazar.laboratorio8.di

import android.content.Context
import com.tiffany.salazar.laboratorio8.db.AppDatabase
import com.tiffany.salazar.laboratorio8.network.PhotoApi
import com.tiffany.salazar.laboratorio8.repository.PhotoRepository
import com.tiffany.salazar.laboratorio8.repository.PhotoRepositoryImpl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {
    private var database: AppDatabase? = null
    private var photoApi: PhotoApi? = null
    private var repository: PhotoRepository? = null

    // Reemplaza con tu API key de Pexels
    private const val PEXELS_API_KEY = "1JggYpijM42g4lIlc38xam2PqbYinwDqMy6BbO5IDGKRH3YXrvGxIEuI"

    fun provideRepository(context: Context): PhotoRepository {
        return repository ?: synchronized(this) {
            val repo = PhotoRepositoryImpl(
                photoDao = provideDatabase(context).photoDao(),
                recentQueryDao = provideDatabase(context).recentQueryDao(),
                photoApi = providePhotoApi()
            )
            repository = repo
            repo
        }
    }

    private fun provideDatabase(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            val instance = AppDatabase.getInstance(context)
            database = instance
            instance
        }
    }

    private fun providePhotoApi(): PhotoApi {
        return photoApi ?: synchronized(this) {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }

            val authInterceptor = Interceptor { chain ->
                val originalRequest = chain.request()
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", PEXELS_API_KEY)
                    .build()
                chain.proceed(newRequest)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.pexels.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val instance = retrofit.create(PhotoApi::class.java)
            photoApi = instance
            instance
        }
    }

    // Método helper para el repositorio
    suspend fun getPhotoCountForQuery(context: Context, query: String): Int {
        return provideDatabase(context).photoDao().getPhotoCountForQuery(
            if (query.isBlank()) "popular" else query
        )
    }
}