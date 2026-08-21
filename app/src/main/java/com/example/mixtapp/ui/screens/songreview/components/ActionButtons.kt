package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.SurfaceCard

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = { },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Default.Check, null, modifier = Modifier.size(18.dp), tint = PrimaryPink)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save", color = PrimaryPink)
        }

        Button(
            onClick = { },
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Icon(Icons.Outlined.FavoriteBorder, null, modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Like", color = Color.White)
        }
    }
}
