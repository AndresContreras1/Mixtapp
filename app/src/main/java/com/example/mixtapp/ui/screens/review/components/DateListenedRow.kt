package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

@Composable
fun DateListenedRow(
    listenedDate: String,
    onDateChange: (String) -> Unit,
    onDatePickerClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = stringResource(R.string.date_listened),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 15.sp
            )
            Text(
                text = stringResource(R.string.when_did_you_listen),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = listenedDate,
            onValueChange = { onDateChange(it.take(10)) },
            modifier = Modifier.width(190.dp),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = onDatePickerClick) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = stringResource(R.string.elegir_fecha_escucha),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}
