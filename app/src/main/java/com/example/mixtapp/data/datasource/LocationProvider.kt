package com.example.mixtapp.data.datasource

interface LocationProvider {
    suspend fun getCurrentLocation(): Pair<Double, Double>?
}