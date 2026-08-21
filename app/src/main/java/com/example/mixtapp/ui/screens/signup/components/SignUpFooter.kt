package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
fun SignUpFooter(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier
    ) {
        OrDivider(
            text = stringResource(R.string.o),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) {
                    append(stringResource(R.string.already_have_account))
                }
                append(" ")
                withStyle(
                    SpanStyle(
                        color = Color(0xFFFF8CAA),
                        fontWeight = FontWeight.Black
                    )
                ) {
                    append(stringResource(R.string.log_in))
                }
            },
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(80.dp))
    }
}
