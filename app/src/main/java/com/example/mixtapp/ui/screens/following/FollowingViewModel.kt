package com.example.mixtapp.ui.screens.following

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.following.model.followingFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getFollowing()
    }

    private fun getFollowing() {
        viewModelScope.launch {
            val result = socialRepository.getFollowing()

            if (result.isSuccess) {
                val following = result.getOrNull() ?: return@launch

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
        viewModelScope.launch {
            val result = socialRepository.darQuitarLikeFollowingReview(reviewId = reviewId)

            if (result.isSuccess) {
                actualizarFollowing(following = result.getOrNull())
            }
        }
    }

    fun compartirQuitar(reviewId: String) {
        viewModelScope.launch {
            val result = socialRepository.compartirQuitarFollowingReview(reviewId = reviewId)

            if (result.isSuccess) {
                actualizarFollowing(following = result.getOrNull())
            }
        }
    }

    private fun actualizarFollowing(following: FollowingUi?) {
        if (following == null) return

        _uiState.update {
            it.copy(
                following = following,
                reviews = aplicarBusqueda(friendQuery = it.friendQuery, todas = following.reviews),
                likedReviewIds = following.reviews.filter { r -> r.isLiked }.map { r -> r.id }.toSet(),
                sharedReviewIds = following.reviews.filter { r -> r.isShared }.map { r -> r.id }.toSet(),
            )
        }
    }

    private fun aplicarBusqueda(
        friendQuery: String,
        todas: List<FollowingReviewUi>,
    ): List<FollowingReviewUi> =
        todas.filter { it.reviewerName.contains(friendQuery, ignoreCase = true) }
}
