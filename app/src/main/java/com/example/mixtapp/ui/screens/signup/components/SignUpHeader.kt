package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.HeaderLogo


@Composable
fun SignUpHeader(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        HeaderLogo(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = stringResource(R.string.create_account),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 28.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.signup_description),
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}