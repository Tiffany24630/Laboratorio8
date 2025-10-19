package com.tiffany.salazar.laboratorio8.di

import android.content.Context
import com.tiffany.salazar.laboratorio8.repository.PhotoRepository
import com.tiffany.salazar.laboratorio8.repository.PhotoRepositoryImpl
import com.tiffany.salazar.laboratorio8.network.PhotoApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ServiceLocator {
    private const val BASE_URL = "https://api.pexels.com/"
    private const val API_KEY = "1JggYpijM42g4lIlc38xam2PqbYinwDqMy6BbO5IDGKRH3YXrvGxIEuI"

    private var photoApi: PhotoApi? = null
    private var repository: PhotoRepository? = null

    private fun providePhotoApi(): PhotoApi {
        return photoApi ?: synchronized(this) {
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val original = chain.request()
                    val requestBuilder = original.newBuilder()
                        .header("Authorization", API_KEY)
                    val request = requestBuilder.build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val api = retrofit.create(PhotoApi::class.java)
            photoApi = api
            api
        }
    }

    fun provideRepository(context: Context): PhotoRepository {
        return repository ?: synchronized(this) {
            val repo = PhotoRepositoryImpl(providePhotoApi(), API_KEY)
            repository = repo
            repo
        }
    }
}