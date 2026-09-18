package com.example.mixtapp.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
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

    AppButton(
        texto = stringResource(R.string.cambiar_foto),
        onClick = { launcher.launch("image/*") },
        cargando = cargando,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.height(40.dp)
    )
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
