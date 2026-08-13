package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.theme.*

@Composable
fun TrackDetailScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userRating by remember { mutableStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp)
    ) {
        // Portada grande estilo Figma
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(SecondaryPlum, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("THE BLACK PARADE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("The Sharpest Lives", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("My Chemical Romance", color = Color.Gray, fontSize = 16.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Cuadro de Calificación Media
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCard, RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("4.5", color = PrimaryMaroon, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Calificación Media", color = Color.Gray, fontSize = 12.sp)
            }
            Text("28.4k ratings • 94% recommend", color = Color.LightGray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("Your Rating:", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Row(modifier = Modifier.padding(vertical = 8.dp)) {
            for (i in 1..5) {
                IconButton(onClick = { userRating = i }) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Calificar $i estrellas",
                        tint = if (i <= userRating) PrimaryMaroon else Color.DarkGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onBack,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryMaroon),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al Inicio", color = Color.White)
        }
    }
}