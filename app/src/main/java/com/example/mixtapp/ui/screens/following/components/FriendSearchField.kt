package com.example.mixtapp.ui.screens.following.components

import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun FriendSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        label = { Text(text = stringResource(R.string.label_buscar_amigos)) },
        placeholder = { Text(text = stringResource(R.string.find_friends)) },
        singleLine = true
    )
}

@Composable
@Preview(showBackground = true)
fun FriendSearchFieldPreview() {
    MixtappTheme(darkTheme = true) {
        FriendSearchField(
            value = "",
            onValueChange = {},
        )
    }
}
