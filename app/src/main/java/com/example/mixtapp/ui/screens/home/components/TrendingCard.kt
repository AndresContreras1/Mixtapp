package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.StarRating
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.FieldBackground
import com.example.mixtapp.ui.theme.FieldBorder
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun TrendingCard(
    album: SongReviewUi,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FieldBackground.copy(alpha = 0.5f))
            .border(1.dp, FieldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick(album.id) }
    ) {
        Image(
            painter = painterResource(id = album.coverRes),
            contentDescription = stringResource(R.string.album_cover, album.title),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        Box(modifier = Modifier.fillMaxSize().padding(14.dp)) {
            Text(
                text = stringResource(R.string.now_trending),
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(PrimaryPink)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = stringResource(R.string.titulo_guion_subtitulo, album.artist, album.title),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StarRating(
                        rating = 5,
                        starCount = 5,
                        starSize = 14.dp,
                        spacing = 0.dp,
                        filledTint = TextPink,
                        emptyTint = TextPink,
                        emptyIcon = Icons.Default.Star,
                        onRatingChange = null
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = stringResource(R.string.plays_this_week),
                        color = PalePink,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}