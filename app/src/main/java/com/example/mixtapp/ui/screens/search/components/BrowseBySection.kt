package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun BrowseBySection(
    categories: List<SearchCategoryUi>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.browse_by),
            modifier = Modifier.padding(top = 50.dp, bottom = 14.dp),
            color = Color.White,
            fontSize = 31.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
        )

        HorizontalDivider(color = FieldBorder.copy(alpha = 0.6f), thickness = 1.dp)

        categories.forEach { category ->
            SearchCategoryRow(
                category = category,
                selected = category.id == selectedCategoryId,
                onClick = { onCategoryClick(category.id) },
            )
            HorizontalDivider(color = FieldBorder.copy(alpha = 0.6f), thickness = 1.dp)
        }
    }
}

@Composable
private fun SearchCategoryRow(
    category: SearchCategoryUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = if (selected) Color.White else PalePink
    val subtitleColor = if (selected) PalePink else TextPink.copy(alpha = 0.72f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        color = Color.Transparent,
        border = if (selected) {
            BorderStroke(width = 0.dp, color = Color.Transparent)
        } else {
            null
        },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 98.dp)
                .padding(vertical = 19.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.title,
                    color = contentColor,
                    fontSize = 24.sp,
                    lineHeight = 29.sp,
                    fontWeight = FontWeight.Normal,
                )
                Text(
                    text = category.subtitle,
                    modifier = Modifier.padding(top = 7.dp),
                    color = subtitleColor,
                    fontSize = 13.sp,
                    lineHeight = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = PrimaryPink,
            )
        }
    }
}
