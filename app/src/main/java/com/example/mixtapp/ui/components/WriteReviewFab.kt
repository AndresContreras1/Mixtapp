package com.example.mixtapp.ui.components

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.R
import com.example.mixtapp.navigation.Screen
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun WriteReviewFab(
    navController: NavHostController,
    albumId: String,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = { navController.navigate(Screen.WriteReview.createRoute(albumId = albumId)) },
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.nav_write_review)
        )
    }
}

@Composable
@Preview
fun WriteReviewFabPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        WriteReviewFab(navController = rememberNavController(), albumId = "")
    }
}
