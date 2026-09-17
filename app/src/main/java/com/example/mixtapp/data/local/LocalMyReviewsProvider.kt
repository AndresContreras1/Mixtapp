package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.MyReviewUi

object LocalMyReviewsProvider {

    val reviews = listOf(
        MyReviewUi(
            id = "2",
            album = LocalAlbumProvider.fromZero,
            rating = 4,
            excerpt = "From Zero strikes an impressive balance between heavy nostalgia and genuine reinvention. Instead of trying to duplicate their past, the band leverages their signature aggressive riffs and electronic textures to build a fierce, modern soundscape. Emily Armstrong delivers a standout vocal performance that commands every track with raw power, signaling a bold, confident new chapter for Linkin Park.",
            tags = listOf("Sentimental", "Potente"),
            date = "17 de agosto de 2026",
        ),
        MyReviewUi(
            id = "1",
            album = LocalAlbumProvider.rush,
            rating = 4,
            excerpt = "RUSH! is an infectious, high-octane pop-rock spectacle built for global arenas. Trading some of their raw Italian grit for polished dance-punk grooves and razor-sharp riffs, Måneskin delivers pure, unapologetic attitude and addictive energy from start to finish.",
            tags = listOf("Enérgico", "Caótico"),
            date = "16 de agosto de 2026",
        ),
        MyReviewUi(
            id = "3",
            album = LocalAlbumProvider.teatroDira,
            rating = 5,
            excerpt = "Teatro d'ira: Vol. I is a raw, electric burst of modern hard rock that captures Måneskin at their absolute peak. Driven by razor-sharp riffs, explosive live energy, and Damiano David’s theatrical vocals, it turns pure attitude and emotional drama into a tight, unforgettable masterpiece.",
            tags = listOf("Emotivo", "Apasionado"),
            date = "13 de agosto de 2026",
        ),
        MyReviewUi(
            id = "4",
            album = LocalAlbumProvider.finisterra,
            rating = 4,
            excerpt = "Finisterra is Mago de Oz’s ambitious folk metal masterpiece. By blending power metal riffs with Celtic flutes and violins, it turns an epic medieval concept into an endlessly creative, legendary album.",
            tags = listOf("Caótico", "Activo"),
            date = "10 de agosto de 2026",
        ),
    )
}