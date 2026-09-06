package com.example.mixtapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// Punto de entrada de Hilt: aqui se crean los objetos del modulo al arrancar la app
@HiltAndroidApp
class BaseApplication : Application()
