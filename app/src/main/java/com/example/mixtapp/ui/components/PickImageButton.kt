package com.example.mixtapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.primary,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier.height(18.dp),
                color = MaterialTheme.colorScheme.onSurface,
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


@Composable
@Preview
fun PickImageButtonPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        PickImageButton(
            action = {},
            cargando = false
        )
    }
}
