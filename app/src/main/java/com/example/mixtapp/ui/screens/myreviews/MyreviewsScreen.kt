package com.example.mixtapp.ui.screens.myreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewCard
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsFilter
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsHeader
import com.example.mixtapp.ui.theme.DeepBackground

@Composable
fun MyReviewsScreen(modifier: Modifier = Modifier) {
    var selectedFilter by rememberSaveable { mutableStateOf("Recent") }

    val reviews = LocalMyReviewsProvider.reviews.let { list ->
        when (selectedFilter) {
            "Top Rated" -> list.sortedByDescending { it.score }
            "A-Z" -> list.sortedBy { it.title }
            "5" -> list.filter { it.score == 5 }
            "4" -> list.filter { it.score == 4 }
            else -> list
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                MyReviewsHeader(
                    username = "Yourname",
                    joinDate = "march 2025",
                    modifier = Modifier.padding(top = 24.dp)
                )
                MyReviewsFilter(
                    selected = selectedFilter,
                    onFilterSelected = { selectedFilter = it },
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            ) {
                items(reviews, key = { it.id }) { review ->
                    MyReviewCard(review, modifier = Modifier.padding(bottom = 16.dp))
                }
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun MyReviewsScreenPreview() {
        MyReviewsScreen()
}