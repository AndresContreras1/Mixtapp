package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi

object LocalMyReviewsProvider {

    const val FILTER_RECENT = "Recent"
    const val FILTER_TOP_RATED = "Top Rated"
    const val FILTER_A_Z = "A-Z"
    const val FILTER_SCORE_5 = "5"
    const val FILTER_SCORE_4 = "4"

    val filters = listOf(
        FILTER_RECENT,
        FILTER_TOP_RATED,
        FILTER_A_Z,
        FILTER_SCORE_5,
        FILTER_SCORE_4,
    )

    val reviews = listOf(
        MyReviewUi(
            id = "1",
            songId = "2",
            title = "Rush",
            artist = "Maneskin",
            coverRes = R.drawable.rush_portada,
            rating = 5, score = 4,
            excerpt = "RUSH! is an infectious, high-octane pop-rock spectacle built for global arenas. Trading some of their raw Italian grit for polished dance-punk grooves and razor-sharp riffs, Måneskin delivers pure, unapologetic attitude and addictive energy from start to finish.",
            tags = listOf("Energetic", "Chaotic"),
            duration = "1'08", date = "Aug 16, 2026",
        ),
        MyReviewUi(
            id = "2",
            songId = "4",
            title = "From Zero",
            artist = "Linkin Park",
            coverRes = R.drawable.from_zero,
            rating = 5, score = 4,
            excerpt = "From Zero strikes an impressive balance between heavy nostalgia and genuine reinvention. Instead of trying to duplicate their past, the band leverages their signature aggressive riffs and electronic textures to build a fierce, modern soundscape. Emily Armstrong delivers a standout vocal performance that commands every track with raw power, signaling a bold, confident new chapter for Linkin Park.",
            tags = listOf("Sentimental", "Strong"),
            duration = "1'08", date = "Aug 17, 2026",
        ),
        MyReviewUi(
            id = "3",
            songId = "1",
            title = "Teatro d'ira: Vol. I",
            artist = "Maneskin",
            coverRes = R.drawable.teatro_dira_vol_i_portada,
            rating = 5, score = 5,
            excerpt = "Teatro d'ira: Vol. I is a raw, electric burst of modern hard rock that captures Måneskin at their absolute peak. Driven by razor-sharp riffs, explosive live energy, and Damiano David’s theatrical vocals, it turns pure attitude and emotional drama into a tight, unforgettable masterpiece.",
            tags = listOf("Emotional", "Passionate"),
            duration = "1'08", date = "Aug 13, 2026",
        ),
        MyReviewUi(
            id = "4",
            songId = "3",
            title = "Finisterra",
            artist = "Mago de Oz",
            coverRes = R.drawable.finisterra_portada,
            rating = 5, score = 4,
            excerpt = "Finisterra is Mago de Oz’s ambitious folk metal masterpiece. By blending power metal riffs with Celtic flutes and violins, it turns an epic medieval concept into an endlessly creative, legendary album.",
            tags = listOf("Chaotic", "Active"),
            duration = "1'08", date = "Aug 10, 2026",
        ),
    )
}