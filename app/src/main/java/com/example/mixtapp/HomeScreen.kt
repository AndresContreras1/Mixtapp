package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.AccentPink
import com.example.mixtapp.ui.theme.DarkBackground
import com.example.mixtapp.ui.theme.PrimaryMaroon
import com.example.mixtapp.ui.theme.SecondaryPlum
import com.example.mixtapp.ui.theme.SurfaceCard
import com.example.mixtapp.ui.theme.TextLight

@Composable
fun HomeScreen(
    onSelectTrack: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        HomeHeader(
            onOpenProfile = onOpenProfile,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        FilterChips(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(28.dp))

        TrendingCard(
            onClick = onSelectTrack,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle(
            title = "Popular Albums",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        PopularAlbums(
            onSelectTrack = onSelectTrack,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(28.dp))

        SectionTitle(
            title = "Friends Activity",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(14.dp))

        FriendsActivity(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.weight(1f))

        BottomMenu(
            onOpenProfile = onOpenProfile,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun HomeHeader(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) {
                    append("Mixt")
                }
                withStyle(SpanStyle(color = PrimaryMaroon)) {
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
                contentDescription = "Buscar",
                tint = AccentPink,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(14.dp))

            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(SecondaryPlum)
                    .clickable { onOpenProfile() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "YO",
                    color = TextLight.copy(alpha = 0.75f),
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
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        HomeChip(text = "For you")
        HomeChip(text = "Trending")
        HomeChip(text = "Friends")
    }
}

@Composable
fun HomeChip(
    text: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(PrimaryMaroon)
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
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(190.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF2B2125))
            .border(1.dp, PrimaryMaroon.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Text(
            text = "Now trending",
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(PrimaryMaroon)
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
                        tint = AccentPink,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "128k plays this week",
                    color = TextLight,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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
            text = "See all",
            color = PrimaryMaroon,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PopularAlbums(
    onSelectTrack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
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
    modifier: Modifier = Modifier
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
                .background(SurfaceCard),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Album",
                color = TextLight.copy(alpha = 0.55f),
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
            color = Color.Gray,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

@Composable
fun FriendsActivity(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SurfaceCard.copy(alpha = 0.45f))
            .border(1.dp, AccentPink.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentPink,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Random Access Memories - Daft Punk",
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = "\"A masterpiece from start to finish.\"",
            color = TextLight,
            fontSize = 12.sp
        )
    }
}

@Composable
fun BottomMenu(
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .height(44.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = TextLight, modifier = Modifier.size(22.dp))
        Icon(Icons.Default.Search, contentDescription = "Explorar", tint = Color.Gray, modifier = Modifier.size(22.dp))
        Icon(Icons.Default.AddCircle, contentDescription = "Agregar", tint = AccentPink, modifier = Modifier.size(32.dp))
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listas", tint = Color.Gray, modifier = Modifier.size(22.dp))
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = Color.Gray,
            modifier = Modifier
                .size(22.dp)
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
