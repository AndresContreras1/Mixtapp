package com.example.mixtapp.data.injection

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.ArtistaRemoteDataSource
import com.example.mixtapp.data.datasource.CancionRemoteDataSource
import com.example.mixtapp.data.datasource.HistoriaRemoteDataSource
import com.example.mixtapp.data.datasource.NotificacionRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.ArtistaFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.CancionFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.HistoriaFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.NotificacionFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// Los repositorios reciben el contrato y aqui se decide que implementacion entrega Hilt.
// Para volver al backend de Express basta con cambiar las Impl de Firestore por las de Retrofit
@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindUsuarioRemoteDataSource(
        impl: UsuarioFirestoreDataSourceImpl
    ): UsuarioRemoteDataSource

    @Binds
    abstract fun bindAlbumRemoteDataSource(
        impl: AlbumFirestoreDataSourceImpl
    ): AlbumRemoteDataSource

    @Binds
    abstract fun bindCancionRemoteDataSource(
        impl: CancionFirestoreDataSourceImpl
    ): CancionRemoteDataSource

    @Binds
    abstract fun bindArtistaRemoteDataSource(
        impl: ArtistaFirestoreDataSourceImpl
    ): ArtistaRemoteDataSource

    @Binds
    abstract fun bindReviewRemoteDataSource(
        impl: ReviewFirestoreDataSourceImpl
    ): ReviewRemoteDataSource

    @Binds
    abstract fun bindNotificacionRemoteDataSource(
        impl: NotificacionFirestoreDataSourceImpl
    ): NotificacionRemoteDataSource

    @Binds
    abstract fun bindHistoriaRemoteDataSource(
        impl: HistoriaFirestoreDataSourceImpl
    ): HistoriaRemoteDataSource
}
