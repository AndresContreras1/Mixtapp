package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun NotificationSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
    )
}

@Composable
@Preview(showBackground = true)
fun NotificationSectionLabelPreview() {
    MixtappTheme(darkTheme = true) {
        NotificationSectionLabel(text = LocalNotificationsProvider.notifications.first().section)
    }
}
