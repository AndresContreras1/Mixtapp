package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.Cancion
import com.example.mixtapp.data.Resenia
import com.example.mixtapp.ui.theme.*

@Composable
fun HomeScreen(
    onSelectTrack: () -> Unit,
    onOpenProfile: () -> Unit,
    modifier: Modifier = Modifier
) {
    val albums = listOf(
        Cancion(1, "From Zero", "Linkin Park", "Rock"),
        Cancion(2, "V8", "The8 Vernon", "Pop"),
        Cancion(3, "Toxicity", "System of a Down", "Metal")
    )

    val friendReviews = listOf(
        Resenia(1, "CarlosM", "Random Access Memories - Daft Punk", "Una obra maestra de principio a fin.", 5),
        Resenia(2, "SofiaR", "The Black Parade - MCR", "Excelente producción y arreglos analógicos.", 4)
    )

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text("Mixt", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("app", color = PrimaryMaroon, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SecondaryPlum)
                        .clickable { onOpenProfile() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("YO", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        },
        bottomBar = {
            NavigationBar(containerColor = DarkBackground) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null, tint = AccentPink) })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.CompassCalibration, null, tint = Color.Gray) })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.List, null, tint = Color.Gray) })
                NavigationBarItem(selected = false, onClick = onOpenProfile, icon = { Icon(Icons.Default.Person, null, tint = Color.Gray) })
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Text("Popular Albums", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(albums, key = { it.idCancion }) { album ->
                        Card(
                            modifier = Modifier
                                .width(120.dp)
                                .clickable { onSelectTrack() },
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(80.dp)
                                        .background(PrimaryMaroon, RoundedCornerShape(8.dp))
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(album.titulo, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                                Text(album.artista, color = Color.Gray, fontSize = 11.sp, maxLines = 1)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Friends Activity", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
            }

            items(friendReviews, key = { it.idResenia }) { review ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(review.nombreUsuario, color = AccentPink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(review.tituloObra, color = Color.White, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                        Row(modifier = Modifier.padding(vertical = 4.dp)) {
                            repeat(review.calificacion) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = PrimaryMaroon, modifier = Modifier.size(14.dp))
                            }
                        }
                        Text(review.comentario, color = Color.LightGray, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}