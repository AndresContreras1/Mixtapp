package com.example.mixtapp.data.injection

import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.datasource.services.ArtistaRetrofitService
import com.example.mixtapp.data.datasource.services.CancionRetrofitService
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.datasource.services.UsuarioRetrofitService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

// Todo lo de Retrofit: la instancia con la URL del backend y un servicio por recurso. Lo de
// Firebase va en FirebaseHiltModule
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providesRetrofit(): Retrofit {
        return Retrofit.Builder()
            // Desde el emulador de Android, 10.0.2.2 es el computador donde corre el backend
            .baseUrl("http://10.0.2.2:3000/")
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesAlbumRetrofitService(retrofit: Retrofit): AlbumRetrofitService {
        return retrofit.create(AlbumRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun providesCancionRetrofitService(retrofit: Retrofit): CancionRetrofitService {
        return retrofit.create(CancionRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun providesArtistaRetrofitService(retrofit: Retrofit): ArtistaRetrofitService {
        return retrofit.create(ArtistaRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun providesUsuarioRetrofitService(retrofit: Retrofit): UsuarioRetrofitService {
        return retrofit.create(UsuarioRetrofitService::class.java)
    }

    @Singleton
    @Provides
    fun providesReviewRetrofitService(retrofit: Retrofit): ReviewRetrofitService {
        return retrofit.create(ReviewRetrofitService::class.java)
    }
}
