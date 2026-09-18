package com.example.mixtapp.ui.screens.login.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.OrDivider

@Composable
fun LoginFooter(
    onSignUpClick: () -> Unit,
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OrDivider(
            text = stringResource(R.string.o),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 83.dp)
        )

        Spacer(modifier = Modifier.height(22.dp))

        TextButton(onClick = onSignUpClick) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onSurface)) {
                        append(stringResource(R.string.no_tienes_cuenta))
                    }
                    append(" ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.inversePrimary,
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        append(stringResource(R.string.sign_up_btn))
                    }
                },
                fontSize = 19.sp,
                lineHeight = 24.sp
            )
        }
    }
}