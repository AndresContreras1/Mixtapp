package com.example.mixtapp.ui.screens.Login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.HeaderLogo
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun LoginHeader(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(86.dp))

        HeaderLogo(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(46.dp))

        Text(
            text = stringResource(R.string.ingresar),
            color = Color.White,
            fontSize = 31.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = stringResource(R.string.descripcion_login),
            color = TextPink,
            fontSize = 17.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}