package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.*

@Composable
fun HomeScreen(
    onSelectTrack: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        HomeHeader(onOpenProfile = onOpenProfile)

        Spacer(modifier = Modifier.height(24.dp))

        FilterChips()

        Spacer(modifier = Modifier.height(28.dp))

        TrendingCard(onClick = onSelectTrack)

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle(title = stringResource(R.string.popular_albums))

        Spacer(modifier = Modifier.height(14.dp))

        PopularAlbums(onSelectTrack = onSelectTrack)

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle(title = stringResource(R.string.friends_activity))

        Spacer(modifier = Modifier.height(14.dp))

        FriendsActivity()

        Spacer(modifier = Modifier.weight(1f))

        BottomMenu(onOpenProfile = onOpenProfile)
    }
}

@Composable
fun HomeHeader(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) {
                    append("Mixt")
                }
                withStyle(SpanStyle(color = LogoPink)) {
                    append("app")
                }
            },
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.search_icon),
                tint = TextPink,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(CircleWine)
                    .clickable { onOpenProfile() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.yo),
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Composable
fun FilterChips(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HomeChip(text = stringResource(R.string.for_you), isSelected = true)
        HomeChip(text = stringResource(R.string.trending), isSelected = false)
        HomeChip(text = stringResource(R.string.friends), isSelected = false)
    }
}

@Composable
fun HomeChip(
    text: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryPink else FieldBackground)
            .padding(horizontal = 18.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun TrendingCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(FieldBackground.copy(alpha = 0.5f))
            .border(1.dp, FieldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
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
                text = "My Chemical Romance",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = TextPink,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "128k plays this week",
                    color = PalePink,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif
        )

        Text(
            text = stringResource(R.string.see_all),
            color = TextPink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PopularAlbums(
    onSelectTrack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        AlbumPlaceholder(
            title = "From Zero",
            artist = "Linkin Park",
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
        AlbumPlaceholder(
            title = "V8",
            artist = "The8 Vernon",
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
        AlbumPlaceholder(
            title = "Toxicity",
            artist = "System of a down",
            modifier = Modifier.weight(1f),
            onClick = onSelectTrack
        )
    }
}

@Composable
fun AlbumPlaceholder(
    title: String,
    artist: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(FieldBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Album",
                color = TextPink.copy(alpha = 0.55f),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )

        Text(
            text = artist,
            color = PalePink.copy(alpha = 0.7f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun FriendsActivity(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(FieldBackground.copy(alpha = 0.45f))
            .border(1.dp, FieldBorder.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = TextPink,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Random Access Memories - Daft Punk",
            color = PalePink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "\"A masterpiece from start to finish.\"",
            color = Color.White.copy(alpha = 0.9f),
            fontSize = 12.sp
        )
    }
}

@Composable
fun BottomMenu(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(DeepBackground),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Home, contentDescription = stringResource(R.string.home_label), tint = Color.White, modifier = Modifier.size(24.dp))
        Icon(Icons.Default.Search, contentDescription = stringResource(R.string.explore_label), tint = PalePink, modifier = Modifier.size(24.dp))
        Icon(Icons.Default.AddCircle, contentDescription = stringResource(R.string.add_label), tint = PrimaryPink, modifier = Modifier.size(36.dp))
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.lists_label), tint = PalePink, modifier = Modifier.size(24.dp))
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = stringResource(R.string.profile_label),
            tint = PalePink,
            modifier = Modifier
                .size(24.dp)
                .clickable { onOpenProfile() }
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreen(
        onSelectTrack = {},
        onOpenProfile = {}
    )
}
