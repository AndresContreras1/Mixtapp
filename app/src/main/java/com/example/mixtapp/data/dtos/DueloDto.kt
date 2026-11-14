package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.DueloEnVivoUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.VotoUi
import java.time.Instant

// El documento duelos/{id}. El id es el dia y el tipo (dia_20397_album): todos los celulares
// llegan al mismo duelo sin preguntarle a nadie
data class DueloDto(
    val id: String,
    val dia: Int,
    val tipo: String,
    val genero: String,
    // Copia de lo que se muestra de los dos items, como en la resena: no hay joins
    val itemA: ItemDelDueloDto?,
    val itemB: ItemDelDueloDto?,
    // Quien voto esta en la subcoleccion votos; aqui solo cuantos van con cada uno
    val votosA: Int,
    val votosB: Int,
    val createdAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", 0, "", "", null, null, 0, 0, null)
}

// Lo que se copia de cada item del duelo, con su id para abrir el detalle
data class ItemDelDueloDto(
    val id: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "")
}

// Lo que se manda para crear el duelo con el primer voto: ese voto ya va contado
data class CreateDueloDto(
    val dia: Int,
    val tipo: String,
    val genero: String,
    val itemA: ItemDelDueloDto,
    val itemB: ItemDelDueloDto,
    val votosA: Int,
    val votosB: Int,
    val createdAt: String,
)

// El documento duelos/{id}/votos/{uid}: con el uid como id, cada usuario tiene un solo voto
data class VotoDto(
    val id: String,
    val opcion: String,
    val opinion: String,
    // Copia de lo que se muestra de quien voto
    val usuario: UsuarioDto?,
    // En formato ISO, asi que ordenar el texto ordena las fechas
    val createdAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", null, null)
}

// Lo que se manda al votar. Si ya habia votado se reemplaza
data class CreateVotoDto(
    val opcion: String,
    val opinion: String,
    val createdAt: String,
    val usuario: CreateReviewUsuarioDto,
)

fun ItemDelDueloDto.toItemResumenUi(tipo: String): ItemResumenUi {
    return ItemResumenUi(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
    )
}

fun ItemResumenUi.toItemDelDueloDto(): ItemDelDueloDto {
    return ItemDelDueloDto(
        id = id,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
    )
}

// El duelo con sus votos para la pantalla. El documento es null mientras nadie haya votado (o si
// los votos llegan antes que el): no es un error, los contadores van en 0 y los votos se muestran.
// Mi opcion es la de mi voto, que tiene mi uid como id
fun DueloDto?.toDueloEnVivoUi(votos: List<VotoDto>, tipo: String, usuarioActualId: String): DueloEnVivoUi {
    return DueloEnVivoUi(
        genero = this?.genero,
        votosA = this?.votosA ?: 0,
        votosB = this?.votosB ?: 0,
        itemA = this?.itemA?.toItemResumenUi(tipo = tipo),
        itemB = this?.itemB?.toItemResumenUi(tipo = tipo),
        opiniones = votos.map { it.toVotoUi(usuarioActualId = usuarioActualId) },
        miOpcion = votos.find { it.id == usuarioActualId }?.opcion,
    )
}

fun VotoDto.toVotoUi(usuarioActualId: String, ahora: Instant = Instant.now()): VotoUi {
    return VotoUi(
        usuarioId = id,
        autor = usuario?.nombre ?: "",
        autorFoto = usuario?.fotoUrl ?: "",
        opcion = opcion,
        opinion = opinion,
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
        esMio = id == usuarioActualId,
    )
}
