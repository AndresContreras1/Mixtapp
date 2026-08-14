package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
        ProfileHeader(modifier = Modifier.fillMaxWidth())

        ProfileTabs(modifier = Modifier.fillMaxWidth())

        ProfileSummary(modifier = Modifier.fillMaxWidth())

        ProfileDivider(modifier = Modifier.fillMaxWidth())

        ProfileSectionTitle(
            title = "FAVORITES",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(18.dp))

        ProfileFavorites(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(30.dp))

        ProfileDivider(modifier = Modifier.fillMaxWidth())

        ProfileSectionTitle(
            title = "RECENT ACTIVITY",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        RecentActivity(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(26.dp))

        ProfileDivider(modifier = Modifier.fillMaxWidth())

        RatingsHeader(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(14.dp))

        RatingsBars(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.weight(1f))

        ProfileBottomMenu(modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "9:41",
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Configuracion",
                tint = AccentPink,
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = "username",
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "...",
                color = AccentPink,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProfileTabs(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = 18.dp)
            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            .background(Color.Black.copy(alpha = 0.35f))
    ) {
        ProfileTab(text = "Profile", selected = true, modifier = Modifier.weight(1f))
        ProfileTab(text = "Diary", selected = false, modifier = Modifier.weight(1f))
        ProfileTab(text = "Lists", selected = false, modifier = Modifier.weight(1f))
        ProfileTab(text = "Library", selected = false, modifier = Modifier.weight(1f))
    }
}

@Composable
fun ProfileTab(
    text: String,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(34.dp)
            .background(
                if (selected) PrimaryMaroon else Color.Transparent,
                RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else Color.Gray,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ProfileSummary(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(SurfaceCard.copy(alpha = 0.35f))
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(76.dp)
                .clip(CircleShape)
                .border(2.dp, PrimaryMaroon, CircleShape)
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "128 reviews · 64 albums · 18 lists",
            color = TextLight,
            fontSize = 12.sp
        )
    }
}

@Composable
fun ProfileDivider(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(1.dp)
            .background(PrimaryMaroon.copy(alpha = 0.45f))
    )
}

@Composable
fun ProfileSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        modifier = modifier.padding(top = 26.dp),
        text = title,
        color = TextLight,
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal
    )
}

@Composable
fun ProfileFavorites(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(4) { index ->
            ProfileAlbumPlaceholder(
                label = "Album ${index + 1}",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ProfileAlbumPlaceholder(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(98.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SecondaryPlum.copy(alpha = 0.55f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = TextLight.copy(alpha = 0.6f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun RecentActivity(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SecondaryPlum.copy(alpha = 0.55f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Album",
                color = TextLight.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "The Black Parade",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Reviewed today · 5 stars",
                color = TextLight,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AccentPink,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "A loud, dramatic favorite that still feels alive on every listen.",
                color = TextLight,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun RatingsHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(top = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "RATINGS",
            color = TextLight,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "More activity",
                color = AccentPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ver mas actividad",
                tint = AccentPink,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun RatingsBars(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(70.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        val heights = listOf(5, 8, 12, 6, 16, 22, 30, 48, 44, 34, 42)

        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = AccentPink,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .size(16.dp)
        )

        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            heights.forEach { height ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(height.dp)
                        .background(PrimaryMaroon.copy(alpha = 0.6f))
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Row(
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            repeat(5) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = AccentPink,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileBottomMenu(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.height(44.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Home, contentDescription = "Inicio", tint = TextLight, modifier = Modifier.size(22.dp))
        Icon(Icons.Default.Search, contentDescription = "Explorar", tint = Color.Gray, modifier = Modifier.size(22.dp))
        Icon(Icons.Default.AddCircle, contentDescription = "Agregar", tint = AccentPink, modifier = Modifier.size(32.dp))
        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Listas", tint = Color.Gray, modifier = Modifier.size(22.dp))
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(PrimaryMaroon),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = TextLight,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen()
}
