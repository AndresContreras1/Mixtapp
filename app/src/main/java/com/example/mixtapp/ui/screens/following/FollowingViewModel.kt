package com.example.mixtapp.ui.screens.following

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.ui.screens.following.model.followingFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getFollowing()
    }

    private fun getFollowing() {
        val following = LocalFollowingProvider.following

        _uiState.update {
            it.copy(
                following = following,
                reviews = aplicarBusqueda(friendQuery = it.friendQuery, todas = following.reviews),
                filters = followingFilters,
                selectedFilterId = followingFilters.first().id,
                likedReviewIds = following.reviews.filter { r -> r.isLiked }.map { r -> r.id }.toSet(),
                sharedReviewIds = following.reviews.filter { r -> r.isShared }.map { r -> r.id }.toSet(),
            )
        }
    }

    fun updateFriendQuery(friendQuery: String) {
        _uiState.update {
            it.copy(
                friendQuery = friendQuery,
                reviews = aplicarBusqueda(
                    friendQuery = friendQuery,
                    todas = it.following?.reviews ?: emptyList(),
                ),
            )
        }
    }

    fun updateSelectedFilter(filtroId: String) {
        _uiState.update { it.copy(selectedFilterId = filtroId) }
    }

    fun updateSelectedStory(storyId: String) {
        _uiState.update { it.copy(selectedStoryId = storyId) }
    }

    fun darQuitarLike(reviewId: String) {
        val actuales = _uiState.value.likedReviewIds
        val nuevos = if (reviewId in actuales) actuales - reviewId else actuales + reviewId

        _uiState.update { it.copy(likedReviewIds = nuevos) }
    }

    fun compartirQuitar(reviewId: String) {
        val actuales = _uiState.value.sharedReviewIds
        val nuevos = if (reviewId in actuales) actuales - reviewId else actuales + reviewId

        _uiState.update { it.copy(sharedReviewIds = nuevos) }
    }

    private fun aplicarBusqueda(
        friendQuery: String,
        todas: List<FollowingReviewUi>,
    ): List<FollowingReviewUi> =
        todas.filter { it.reviewerName.contains(friendQuery, ignoreCase = true) }
}
