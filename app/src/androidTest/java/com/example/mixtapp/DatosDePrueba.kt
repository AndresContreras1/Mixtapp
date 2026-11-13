package com.example.mixtapp

import android.util.Log
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateDueloDto
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewItemDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.CreateVotoDto
import com.example.mixtapp.data.dtos.HistoriaDto
import com.example.mixtapp.data.dtos.ItemDelDueloDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.SeccionDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// Los datos los crea la propia prueba, no la carga con Faker: asi cada prueba es replicable.
// Estan en un solo archivo para no copiarlos en cada clase de prueba

// Todas las pruebas usan el emulador de Firebase. Cada useEmulator va en su propio try: si otra prueba
// de la misma corrida ya lo conecto, lanza una excepcion que no importa, y asi no deja al otro sin
// conectar. El Log.d queda en el Logcat (filtro DatosDePrueba): si la instancia ya se habia usado
// sin el emulador, ahi se ve por que la prueba no esta en el emulador
fun conectarEmulador() {
    try {
        Firebase.firestore.useEmulator("10.0.2.2", 8080)
    } catch (e: Exception) {
        Log.d("DatosDePrueba", "Firestore no se conecto al emulador: ${e.message}")
    }
    try {
        Firebase.auth.useEmulator("10.0.2.2", 9099)
    } catch (e: Exception) {
        Log.d("DatosDePrueba", "Authentication no se conecto al emulador: ${e.message}")
    }
}

fun generateUsuario(i: Int): UsuarioPerfilFirestoreDto = UsuarioPerfilFirestoreDto(
    id = "usuario_$i",
    username = "username_$i",
    nombre = "Nombre $i",
    bio = "Bio del usuario $i",
    fotoUrl = "https://picsum.photos/200/200?random=$i",
    followersCount = i * 10,
    followingCount = i * 5,
    fcmtoken = null,
)

fun generateAlbum(i: Int): AlbumDto = AlbumDto(
    id = "album_$i",
    titulo = "Album $i",
    artista = "Artista $i",
    portadaUrl = "https://picsum.photos/600/600?random=$i",
    anio = 2000 + i,
    fechaLanzamiento = "${2000 + i}-01-01",
    genero = "Rock",
    subgenero = "Rock alternativo",
    tendencia = null,
    tendenciaColombia = null,
    likesCount = 0,
    createdAt = "2025-01-01T00:00:00Z",
    updatedAt = "2025-01-01T00:00:00Z",
)

// Los datos curiosos del album i: dos secciones, como las que carga el seed
fun generateHistoria(i: Int): HistoriaDto = HistoriaDto(
    tipo = TIPO_ALBUM,
    secciones = listOf(
        SeccionDto(titulo = "Historia", texto = "Historia del album $i"),
        SeccionDto(titulo = "Dato curioso", texto = "Dato curioso del album $i"),
    ),
)

// La resena de un usuario a un album, con las copias que guarda la app
fun generateReview(usuario: UsuarioPerfilFirestoreDto, album: AlbumDto): CreateReviewDto = CreateReviewDto(
    usuarioId = usuario.id,
    itemId = album.id,
    tipo = TIPO_ALBUM,
    calificacion = 4,
    comentario = "Resena de ${usuario.nombre} a ${album.titulo}",
    fechaEscucha = "2025-01-01",
    likesCount = 0,
    commentsCount = 0,
    createdAt = "2025-01-01T00:00:00Z",
    updatedAt = "2025-01-01T00:00:00Z",
    usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
    item = CreateReviewItemDto(titulo = album.titulo, subtitulo = album.artista, imagenUrl = album.portadaUrl),
    latitud = null,
    longitud = null,
)

// Una resena publicada hace un rato desde Bogota, para el mapa de las ultimas 24 horas
fun generateReviewConUbicacion(usuario: UsuarioPerfilFirestoreDto, album: AlbumDto, createdAt: String): CreateReviewDto =
    generateReview(usuario = usuario, album = album).copy(
        createdAt = createdAt,
        updatedAt = createdAt,
        latitud = 4.65,
        longitud = -74.06,
    )

// Carga 10 usuarios, 5 albumes con sus datos curiosos y la resena "review_i" del usuario i al
// album i (i de 1 a 5)
suspend fun cargarDatosDePrueba(db: FirebaseFirestore) {
    val batch = db.batch()

    repeat(10) { i ->
        val usuario = generateUsuario(i)
        batch.set(db.collection("usuarios").document(usuario.id), usuario)
    }

    for (i in 1..5) {
        val album = generateAlbum(i)
        batch.set(db.collection("albumes").document(album.id), album)
        batch.set(db.collection("historias").document(album.id), generateHistoria(i))
        batch.set(db.collection("reviews").document("review_$i"), generateReview(generateUsuario(i), album))
    }

    batch.commit().await()
}

// Un comentario del usuario i, como lo arma el repositorio (con la copia del autor)
fun generateComentario(
    i: Int,
    parentCommentId: String? = null,
    contenido: String = "Comentario $i",
    createdAt: String = "2025-01-0${i}T00:00:00Z",
): CreateComentarioDto {
    val usuario = generateUsuario(i)

    return CreateComentarioDto(
        usuarioId = usuario.id,
        contenido = contenido,
        parentCommentId = parentCommentId,
        likesCount = 0,
        createdAt = createdAt,
        usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
    )
}

// Las pruebas de repositorio crean una cuenta en el Authentication del emulador. Si la prueba cerro
// la sesion se vuelve a entrar con esa cuenta para poder borrarla: el emulador queda como estaba
suspend fun borrarCuentaDePrueba(auth: FirebaseAuth, correo: String) {
    val user = auth.currentUser ?: auth.signInWithEmailAndPassword(correo, "123456").await().user
    user?.delete()?.await()
    auth.signOut()
}

// Antes y despues de cada prueba la base debe quedar igual, y lo ideal es que vacia. Borrar un
// documento no borra sus subcolecciones: se recorren una por una
suspend fun limpiarBase(db: FirebaseFirestore) {
    val usuarios = db.collection("usuarios").get().await()
    for (usuarioDoc in usuarios) {
        for (subcoleccion in listOf("followers", "following", "likes", "guardados", "notificaciones")) {
            val documentos = usuarioDoc.reference.collection(subcoleccion).get().await()
            for (documento in documentos) documento.reference.delete().await()
        }

        usuarioDoc.reference.delete().await()
    }

    val reviews = db.collection("reviews").get().await()
    for (reviewDoc in reviews) {
        val likes = reviewDoc.reference.collection("likes").get().await()
        for (like in likes) like.reference.delete().await()

        val comentarios = reviewDoc.reference.collection("comments").get().await()
        for (comentario in comentarios) {
            val likesComentario = comentario.reference.collection("likes").get().await()
            for (like in likesComentario) like.reference.delete().await()

            comentario.reference.delete().await()
        }

        reviewDoc.reference.delete().await()
    }

    for (coleccion in listOf("albumes", "canciones", "artistas", "historias")) {
        val items = db.collection(coleccion).get().await()
        for (itemDoc in items) {
            val likes = itemDoc.reference.collection("likes").get().await()
            for (like in likes) like.reference.delete().await()

            itemDoc.reference.delete().await()
        }
    }

    val duelos = db.collection("duelos").get().await()
    for (dueloDoc in duelos) {
        val votos = dueloDoc.reference.collection("votos").get().await()
        for (voto in votos) voto.reference.delete().await()

        dueloDoc.reference.delete().await()
    }

    // Las mixtapes no tienen subcolecciones
    val mixtapes = db.collection("mixtapes").get().await()
    for (mixtapeDoc in mixtapes) mixtapeDoc.reference.delete().await()

    val sonando = db.collection("sonando").get().await()
    for (sonandoDoc in sonando) {
        val reacciones = sonandoDoc.reference.collection("reacciones").get().await()
        for (reaccion in reacciones) reaccion.reference.delete().await()

        sonandoDoc.reference.delete().await()
    }
}

// Una mixtape vacia del usuario i, como la arma el repositorio
fun generateMixtape(i: Int, createdAt: String = "2025-01-0${i}T00:00:00Z"): CreateMixtapeDto {
    val usuario = generateUsuario(i)

    return CreateMixtapeDto(
        nombre = "Mixtape $i",
        descripcion = null,
        usuarioId = usuario.id,
        usuarioNombre = usuario.nombre,
        usuarioFotoUrl = usuario.fotoUrl,
        items = emptyList(),
        createdAt = createdAt,
    )
}

// La copia del album i que se agrega a una mixtape
fun generateItemMixtape(i: Int): ItemMixtapeDto {
    val album = generateAlbum(i)

    return ItemMixtapeDto(
        id = album.id,
        tipo = TIPO_ALBUM,
        titulo = album.titulo,
        subtitulo = album.artista,
        imagenUrl = album.portadaUrl,
        genero = album.genero,
    )
}

// Lo que el usuario i pone a sonar: el album_1, como lo arma el repositorio
fun generateSonando(i: Int, actualizadoEn: String = "2025-01-0${i}T00:00:00Z"): CreateSonandoDto {
    val usuario = generateUsuario(i)
    val album = generateAlbum(1)

    return CreateSonandoDto(
        usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
        itemId = album.id,
        tipo = TIPO_ALBUM,
        titulo = album.titulo,
        subtitulo = album.artista,
        imagenUrl = album.portadaUrl,
        reaccionesCount = 0,
        actualizadoEn = actualizadoEn,
    )
}

// La reaccion 🔥 del usuario i a lo que suena el album_1
fun generateReaccion(i: Int): CreateReaccionDto {
    val usuario = generateUsuario(i)

    return CreateReaccionDto(
        usuarioId = usuario.id,
        itemId = generateAlbum(1).id,
        nombre = usuario.nombre,
        fotoUrl = usuario.fotoUrl,
        createdAt = "2025-01-0${i}T00:00:00Z",
    )
}

// El duelo de prueba entre album_1 y album_2, como lo arma el repositorio con el primer voto
fun generateDuelo(votosA: Int, votosB: Int): CreateDueloDto {
    val albumA = generateAlbum(1)
    val albumB = generateAlbum(2)

    return CreateDueloDto(
        dia = 1,
        tipo = TIPO_ALBUM,
        genero = albumA.genero,
        itemA = ItemDelDueloDto(id = albumA.id, titulo = albumA.titulo, subtitulo = albumA.artista, imagenUrl = albumA.portadaUrl),
        itemB = ItemDelDueloDto(id = albumB.id, titulo = albumB.titulo, subtitulo = albumB.artista, imagenUrl = albumB.portadaUrl),
        votosA = votosA,
        votosB = votosB,
        createdAt = "2025-01-01T00:00:00Z",
    )
}

// El voto del usuario i, con la copia de su nombre y su foto
fun generateVoto(i: Int, opcion: String, createdAt: String = "2025-01-0${i}T00:00:00Z"): CreateVotoDto {
    val usuario = generateUsuario(i)

    return CreateVotoDto(
        opcion = opcion,
        opinion = "Opinion $i",
        createdAt = createdAt,
        usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
    )
}
