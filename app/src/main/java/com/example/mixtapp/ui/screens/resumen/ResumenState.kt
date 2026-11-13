package com.example.mixtapp.ui.screens.resumen

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi

// Las tarjetas del resumen, en el orden en que se descubren. Las que no tienen datos (por ejemplo,
// sin reseñas no hay mejor calificada) no salen
const val TARJETA_RESENAS = "resenas"
const val TARJETA_GENERO = "genero"
const val TARJETA_ARTISTA = "artista"
const val TARJETA_MEJOR = "mejor"
const val TARJETA_QUERIDA = "querida"
const val TARJETA_SOCIAL = "social"

data class ResumenState(
    // Lo que llega en vivo: con esto el ViewModel calcula el resumen
    val catalogo: List<ItemConResenasUi> = emptyList(),
    val favoritos: List<ItemResumenUi> = emptyList(),
    val seguidores: Int = 0,
    val seguidos: Int = 0,
    // El resumen ya calculado
    val totalResenas: Int = 0,
    val promedio: Double = 0.0,
    val totalMeGusta: Int = 0,
    val generoFavorito: String? = null,
    val artistaFavorito: String? = null,
    val mejorCalificado: ItemResumenUi? = null,
    val mejorCalificacion: Int = 0,
    val masQuerida: ReviewUi? = null,
    // Las tarjetas que hay y cuantas ya se descubrieron
    val tarjetas: List<String> = emptyList(),
    val reveladas: Int = 1,
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar el catalogo
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
