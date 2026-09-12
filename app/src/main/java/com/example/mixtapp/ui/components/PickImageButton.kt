package com.example.mixtapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun PickImageButton(
    action: (Uri) -> Unit,
    cargando: Boolean,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { action(it) }
    }

    Button(
        onClick = { launcher.launch("image/*") },
        modifier = modifier.height(40.dp),
        enabled = !cargando,
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryPink,
            contentColor = Color.White,
            disabledContainerColor = PrimaryPink,
            disabledContentColor = Color.White
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.height(18.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = stringResource(R.string.cambiar_foto),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
