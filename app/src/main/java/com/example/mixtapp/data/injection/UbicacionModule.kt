package com.example.mixtapp.data.injection

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// El LocationManager de Android lo construye el sistema, no Hilt: aqui se declara como lo entrega,
// igual que FirebaseHiltModule con Firestore. El contrato de la ubicacion va en DataSourceModule
@Module
@InstallIn(SingletonComponent::class)
object UbicacionModule {

    @Singleton
    @Provides
    fun locationManager(@ApplicationContext context: Context): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}
