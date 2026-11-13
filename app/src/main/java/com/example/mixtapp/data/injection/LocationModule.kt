package com.example.mixtapp.data.injection

import com.example.mixtapp.data.datasource.LocationProvider
import com.example.mixtapp.data.datasource.impl.AndroidLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

// La ubicacion del celular: los ViewModels reciben el contrato y Hilt entrega la implementacion de
// Android. En las pruebas se reemplaza por un mock
@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    abstract fun bindLocationProvider(
        impl: AndroidLocationProvider
    ): LocationProvider
}
