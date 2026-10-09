package com.example.mixtapp.ui.screens.userprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewCard
import com.example.mixtapp.ui.screens.userprofile.components.UserProfileHeader
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun UserProfileScreen(
    userId: String,
    userProfileViewModel: UserProfileViewModel,
    onReviewClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by userProfileViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        userProfileViewModel.getUserProfile(userId = userId)
        userProfileViewModel.getUserReviews(userId = userId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        state.usuario == null -> {
            Text(text = stringResource(state.errorMessageRes ?: R.string.perfil_no_encontrado))
        }
        else -> UserProfileScreenContent(
            usuario = state.usuario!!,
            reviews = state.reviews,
            errorMessageRes = state.errorMessageRes,
            onReviewClick = onReviewClick,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}

@Composable
fun UserProfileScreenContent(
    usuario: UsuarioUi,
    reviews: List<MyReviewUi>,
    errorMessageRes: Int?,
    onReviewClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp)
        ) {
            item {
                UserProfileHeader(
                    usuario = usuario,
                    reviewsCount = reviews.size,
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            items(reviews.size) { index ->
                MyReviewCard(
                    review = reviews[index],
                    onReviewClick = onReviewClick,
                    showEditButton = false,
                    onEditClick = {},
                    onDeleteClick = {},
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun UserProfileScreenPreview() {
    MixtappTheme(darkTheme = true) {
        UserProfileScreenContent(
            usuario = UsuarioUi(
                id = "2",
                nombre = "Mateo Gómez",
                email = "mateo@example.com",
                fotoUrl = "",
            ),
            reviews = LocalMyReviewsProvider.reviews,
            errorMessageRes = null,
            onReviewClick = {},
            onBackClick = {}
        )
    }
}
