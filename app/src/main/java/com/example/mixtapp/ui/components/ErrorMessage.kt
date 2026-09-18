package com.example.mixtapp.ui.components

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun ErrorMessage(
    @StringRes messageRes: Int?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = messageRes != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            text = stringResource(messageRes ?: R.string.error_generico),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
        )
    }
}

@Composable
@Preview
fun ErrorMessagePreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        ErrorMessage(messageRes = R.string.error_generico)
    }
}
