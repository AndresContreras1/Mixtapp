package com.example.mixtapp.ui.screens.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.*

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            ProfileHeader()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                ProfileAvatarSection()
                
                Spacer(modifier = Modifier.height(32.dp))

                FavoriteSection()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                RecentActivitySection()
                
                Spacer(modifier = Modifier.height(32.dp))
                
                RatingsSection()
                
                Spacer(modifier = Modifier.height(80.dp))
            }
            
            ProfileBottomNav()
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(DeepBackground)
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                tint = PalePink,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "username",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = PalePink,
                modifier = Modifier.size(28.dp)
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White.copy(alpha = 0.05f)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val tabs = listOf("Profile", "Diary", "Lists", "Library")
            tabs.forEach { tab ->
                val isSelected = tab == "Profile"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) CircleWine else Color.Transparent)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.White else PalePink.copy(alpha = 0.6f),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
        HorizontalDivider(color = Color.White.copy(alpha = 0.1f), thickness = 0.5.dp)
    }
}

@Composable
fun ProfileAvatarSection() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(1.5.dp, PrimaryPink, CircleShape)
                .background(CircleWine.copy(alpha = 0.2f))
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.user_stats),
            color = PalePink.copy(alpha = 0.8f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun FavoriteSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.favoritos).uppercase(),
            color = TextPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            repeat(4) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(FieldBackground)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
}

@Composable
fun RecentActivitySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.actividad_reciente).uppercase(),
            color = TextPink,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(FieldBackground)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "The Black Parade",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "${stringResource(R.string.reviewed_today)} · 5 stars",
                    color = PalePink.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    repeat(5) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = PrimaryPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "A loud, dramatic favorite that still feels alive on every listen.",
                    color = PalePink.copy(alpha = 0.9f),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(24.dp))
    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
}

@Composable
fun RatingsSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.calificaciones).uppercase(),
                color = TextPink,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "More activity",
                    color = PrimaryPink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = PrimaryPink,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val values = listOf(0.1f, 0.2f, 0.15f, 0.3f, 0.4f, 0.35f, 0.5f, 0.8f, 1f, 0.7f, 0.6f)
            values.forEach { h ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(60.dp * h)
                        .background(if (h > 0.5f) PrimaryPink else CircleWine.copy(alpha = 0.6f))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Default.Star, null, tint = PrimaryPink.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) {
                    Icon(Icons.Default.Star, null, tint = PrimaryPink.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileBottomNav() {
    Surface(
        color = Color.Black.copy(alpha = 0.9f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .height(64.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Home, null, tint = PalePink.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
            Icon(Icons.Default.Search, null, tint = PalePink.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
            
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(PrimaryPink),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            
            Icon(Icons.AutoMirrored.Filled.List, null, tint = PalePink.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
            
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(CircleWine.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, null, tint = PrimaryPink, modifier = Modifier.size(28.dp))
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ProfileScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        ProfileScreen()
    }
}
