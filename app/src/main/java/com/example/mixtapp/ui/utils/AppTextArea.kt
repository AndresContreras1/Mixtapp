package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// Campo de varias lineas con el contador de caracteres debajo, para textos largos como la resena
@Composable
fun AppTextArea(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    maxLength: Int,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(126.dp),
            label = { Text(text = label) },
            placeholder = { Text(text = placeholder) },
            shape = RoundedCornerShape(16.dp),
            colors = appTextFieldColors()
        )

        Text(
            text = stringResource(R.string.contador_caracteres, value.length, maxLength),
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 6.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun AppTextAreaPreview() {
    MixtappTheme(darkTheme = true) {
        AppTextArea(
            value = "",
            onValueChange = {},
            label = "Reseña",
            placeholder = "Escribe tu reseña",
            maxLength = 500
        )
    }
}
