package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.ErrorMessage

@Composable
fun ReviewActionsCard(
    listenedDate: String,
    publicando: Boolean,
    errorMessageRes: Int?,
    onDateChange: (String) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                DateListenedRow(
                    listenedDate = listenedDate,
                    onDateChange = onDateChange
                )
            }
        }

        Spacer(modifier = Modifier.height(26.dp))

        // El mismo boton de la app: mientras se guarda muestra que esta cargando
        AppButton(
            texto = stringResource(R.string.post),
            onClick = onPostClick,
            cargando = publicando,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        )

        ErrorMessage(
            messageRes = errorMessageRes,
            modifier = Modifier.padding(top = 12.dp)
        )
    }
}
