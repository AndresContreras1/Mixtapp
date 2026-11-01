package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NOTIFICACION_COMENTARIO
import com.example.mixtapp.data.model.NOTIFICACION_LIKE
import com.example.mixtapp.data.model.NOTIFICACION_SEGUIDOR
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.data.model.toItemResumenUi

// Datos de ejemplo solo para los previews: la app los lee de Firestore. Son del mismo catalogo
// que carga el seed
object LocalPreviewProvider {

    val fromZero = ItemUi(
        id = "album_from_zero",
        tipo = TIPO_ALBUM,
        titulo = "From Zero",
        subtitulo = "Linkin Park",
        detalle = "",
        imagenUrl = "https://cdn-images.dzcdn.net/images/cover/1e8ffbd401303b5693226c12ee0b84fb/1000x1000-000000-80-0-0.jpg",
        anio = "2024",
        genero = "Rock",
        subgenero = "Rock alternativo",
        datosCuriosos = listOf(
            "Primer álbum con Emily Armstrong en la voz, después de la muerte de Chester Bennington en 2017.",
            "Primer álbum de estudio de Linkin Park desde One More Light (2017).",
            "El nombre recuerda a Xero, como se llamaba la banda en sus comienzos.",
        ),
        tendencia = null,
        likes = 1240,
        likesText = "1.2K",
        isLiked = true,
        isSaved = false,
    )

    val emptinessMachine = ItemUi(
        id = "cancion_the_emptiness_machine",
        tipo = TIPO_CANCION,
        titulo = "The Emptiness Machine",
        subtitulo = "From Zero",
        detalle = "Linkin Park",
        imagenUrl = "https://cdn-images.dzcdn.net/images/cover/1e8ffbd401303b5693226c12ee0b84fb/1000x1000-000000-80-0-0.jpg",
        anio = "2024",
        genero = "Rock",
        subgenero = "Rock alternativo",
        datosCuriosos = listOf(
            "Primer sencillo con Emily Armstrong en la voz, lanzado en septiembre de 2024.",
            "Es la canción 2 de From Zero y dura 3:10.",
        ),
        tendencia = null,
        likes = 86,
        likesText = "86",
    )

    val mikeShinoda = ItemUi(
        id = "artista_mike_shinoda",
        tipo = TIPO_ARTISTA,
        titulo = "Mike Shinoda",
        subtitulo = "Linkin Park",
        detalle = "",
        imagenUrl = "https://cdn-images.dzcdn.net/images/artist/83893b115ac94cfa93fc8ba94bdc91f8/1000x1000-000000-80-0-0.jpg",
        anio = "",
        genero = "Rock",
        subgenero = "Rock alternativo",
        datosCuriosos = listOf(
            "Cofundador de Linkin Park: rapea, canta, toca y produce.",
            "Tiene un proyecto propio de hip hop: Fort Minor.",
        ),
        tendencia = null,
        likes = 40,
        likesText = "40",
    )

    val karolG = ItemUi(
        id = "album_no_me_arrepiento_de_sentir_tanto",
        tipo = TIPO_ALBUM,
        titulo = "NO ME ARREPIENTO DE SENTIR TANTO",
        subtitulo = "KAROL G",
        detalle = "",
        imagenUrl = "https://cdn-images.dzcdn.net/images/cover/f794dd1931dac42fc13f938850b686c6/1000x1000-000000-80-0-0.jpg",
        anio = "2026",
        genero = "Reggaetón",
        subgenero = "Reggaetón",
        datosCuriosos = emptyList(),
        tendencia = 1,
        likes = 15400,
        likesText = "15K",
    )

    val usuario = UsuarioUi(
        id = "usuario_1",
        seguidores = 6,
        seguidos = 4,
        siguiendo = false,
        esMiPerfil = true,
        nombre = "Emilia Guerrero",
        username = "emilia_guerrero",
        bio = "Escucho de todo, pero el rock siempre gana.",
        fotoUrl = "",
    )

    val otroUsuario = UsuarioUi(
        id = "usuario_2",
        seguidores = 3,
        seguidos = 4,
        siguiendo = true,
        nombre = "Diego Alfaro",
        username = "diego_alfaro",
        bio = "Metalero de corazón, abierto a recomendaciones.",
        fotoUrl = "",
    )

    val review = ReviewUi(
        id = "usuario_2_1",
        authorId = "usuario_2",
        author = "Diego Alfaro",
        authorImage = "",
        date = "2026-10-28",
        rating = 5,
        content = "La producción está impecable, cada escucha le encuentro algo nuevo.",
        timeAgo = "3 h",
        isMine = false,
        likes = 12,
        likesText = "12",
        isLiked = true,
        comments = 3,
        item = fromZero.toItemResumenUi(),
        updatedAt = "2026-10-31T12:00:00Z",
    )

    val miReview = review.copy(
        id = "usuario_1_1",
        authorId = "usuario_1",
        author = "Emilia Guerrero",
        rating = 4,
        content = "Lo pongo cada vez que necesito energía.",
        isMine = true,
        isLiked = false,
        item = emptinessMachine.toItemResumenUi(),
    )

    val reviews = listOf(review, miReview)

    val itemConResenas = ItemConResenasUi(
        item = fromZero,
        rating = 4.5,
        estrellas = 5,
        ratingCount = 2,
        recommendRate = 100,
        userRating = 4,
        reviews = reviews,
    )

    val catalogo = listOf(
        itemConResenas,
        itemConResenas.copy(item = emptinessMachine),
        itemConResenas.copy(item = mikeShinoda),
        itemConResenas.copy(item = karolG),
    )

    val favoritos: List<ItemResumenUi> = listOf(
        fromZero.toItemResumenUi(),
        emptinessMachine.toItemResumenUi(),
        mikeShinoda.toItemResumenUi(),
    )

    val comentarios = listOf(
        DiscussionCommentUi(
            id = "comentario_1",
            authorId = "usuario_3",
            author = "Valentina Torres",
            authorImage = "",
            timeAgo = "2 h",
            content = "Totalmente de acuerdo.",
            likes = 2,
            likesText = "2",
            isReply = false,
            isLiked = false,
        ),
        DiscussionCommentUi(
            id = "comentario_2",
            authorId = "usuario_2",
            author = "Diego Alfaro",
            authorImage = "",
            timeAgo = "",
            content = "La segunda mitad es mi parte favorita.",
            likes = 0,
            likesText = "0",
            isReply = true,
            isLiked = false,
            parentId = "comentario_1",
        ),
    )

    val discussion = DiscussionUi(
        id = review.id,
        review = review,
        comments = comentarios,
    )

    val following = FollowingUi(
        id = usuario.id,
        followingCount = 4,
        followersCount = 6,
        seguidos = listOf(otroUsuario, usuario.copy(id = "usuario_3", nombre = "Valentina Torres")),
        reviews = reviews,
    )

    val notifications = listOf(
        NotificationUi(
            id = "1",
            tipo = NOTIFICACION_LIKE,
            actorId = "usuario_2",
            actorName = "Diego Alfaro",
            actorImage = "",
            itemTitle = "From Zero",
            itemImage = fromZero.imagenUrl,
            reviewId = review.id,
            timeAgo = "5 min",
            isRead = false,
            isToday = true,
        ),
        NotificationUi(
            id = "2",
            tipo = NOTIFICACION_COMENTARIO,
            actorId = "usuario_3",
            actorName = "Valentina Torres",
            actorImage = "",
            itemTitle = "The Emptiness Machine",
            itemImage = emptinessMachine.imagenUrl,
            reviewId = miReview.id,
            timeAgo = "2 d",
            isRead = true,
            isToday = false,
        ),
        NotificationUi(
            id = "3",
            tipo = NOTIFICACION_SEGUIDOR,
            actorId = "usuario_4",
            actorName = "Santiago López",
            actorImage = "",
            itemTitle = "",
            itemImage = "",
            reviewId = null,
            timeAgo = "3 d",
            isRead = true,
            isToday = false,
        ),
    )
}
