package com.example.mixtapp.data.injection

import com.example.mixtapp.data.datasource.LocationProvider
import com.example.mixtapp.data.datasource.impl.AndroidLocationProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationModule {

    @Binds
    abstract fun bindLocationProvider(
        impl: AndroidLocationProvider
    ): LocationProvider
}