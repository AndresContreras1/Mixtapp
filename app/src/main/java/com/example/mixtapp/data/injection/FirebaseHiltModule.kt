package com.example.mixtapp.data.injection

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Solo se declara lo que Hilt no puede construir solo. El data source y el repositorio
// se resuelven en cadena porque llevan @Inject constructor
@Module
@InstallIn(SingletonComponent::class)
class FirebaseHiltModule {

    @Provides
    @Singleton
    fun auth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun storage(): FirebaseStorage = FirebaseStorage.getInstance()
}
