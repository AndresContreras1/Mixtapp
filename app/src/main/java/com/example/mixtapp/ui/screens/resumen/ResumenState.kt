package com.example.mixtapp.ui.screens.resumen

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
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
    // Con esto el ViewModel calcula el resumen: mis reseñas y mis me gusta en vivo, mis contadores
    // en vivo y el catalogo leido una vez (para el genero y el artista de cada item)
    val misResenas: List<ReviewUi> = emptyList(),
    val items: List<ItemUi> = emptyList(),
    val favoritos: List<ItemResumenUi> = emptyList(),
    val seguidores: Int = 0,
    val seguidos: Int = 0,
    // El resumen ya calculado. El ano va en el titulo ("Tu 2026 en Mixtapp")
    val anio: Int = 0,
    val totalResenas: Int = 0,
    val promedio: Double = 0.0,
    val totalMeGusta: Int = 0,
    val generoFavorito: String? = null,
    val artistaFavorito: String? = null,
    val mejorCalificado: ItemResumenUi? = null,
    val mejorCalificacion: Int = 0,
    val masQuerida: ReviewUi? = null,
    // Con muy pocas reseñas y me gusta no se afirma cual es mi genero ni mi artista (data/model)
    val pocosDatos: Boolean = false,
    // Las tarjetas que hay y cuantas ya se descubrieron
    val tarjetas: List<String> = emptyList(),
    val reveladas: Int = 1,
    // Se pone en true al pedir los datos y en false cuando llegan mis reseñas o fallan
    val isLoading: Boolean = false,
    // No se pudieron cargar mis reseñas o el catalogo: ocupa la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // No llegaron mis me gusta o mis seguidores: el resumen se ve, pero avisa que esos numeros
    // pueden no estar completos
    @StringRes val errorDatosRes: Int? = null,
) {
    // Mientras queden tarjetas por descubrir el boton dice Siguiente; al final, Ver de nuevo
    val quedanPorVer: Boolean get() = reveladas < tarjetas.size
}
