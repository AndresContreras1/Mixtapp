package com.example.mixtapp.ui.screens.Login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.*
import com.example.mixtapp.ui.theme.*

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        DecorativeCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 31.dp),
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

            FigmaTextField(
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                icon = FieldIcon.Email
            )

            Spacer(modifier = Modifier.height(16.dp))

            FigmaTextField(
                label = stringResource(R.string.contrasena),
                placeholder = stringResource(R.string.password),
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                icon = FieldIcon.Lock
            )

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(82.dp),
                shape = RoundedCornerShape(41.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryPink,
                    contentColor = Color.White
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text = stringResource(R.string.ingresar).uppercase(),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(19.dp))

            FigmaOrDivider(
                text = stringResource(R.string.o),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(22.dp))

            TextButton(onClick = { /* Navigate to Sign Up */ }) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = Color.White)) {
                            append(stringResource(R.string.no_tienes_cuenta_registrate).substringBefore(" "))
                            append(" ")
                            append(stringResource(R.string.no_tienes_cuenta_registrate).substringAfter(" ").substringBefore("?"))
                            append("?  ")
                        }
                        withStyle(
                            SpanStyle(
                                color = Color(0xFFFF8CAA),
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

            Spacer(modifier = Modifier.height(68.dp))
        }

        Box(
            modifier = Modifier
                .size(39.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-15).dp)
                .clip(CircleShape)
                .background(PrimaryPink.copy(alpha = 0.78f))
        )
    }
}

@Composable
@Preview(showBackground = true, widthDp = 393, heightDp = 852)
fun LoginScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        LoginScreen()
    }
}
