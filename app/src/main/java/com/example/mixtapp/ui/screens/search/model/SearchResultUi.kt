package com.example.mixtapp.ui.screens.search.model

import com.example.mixtapp.data.model.SongReviewUi

// La pantalla solo pinta: las estrellas ya vienen redondeadas del ViewModel
data class SearchResultUi(
    val songReview: SongReviewUi,
    val estrellas: Int,
)
