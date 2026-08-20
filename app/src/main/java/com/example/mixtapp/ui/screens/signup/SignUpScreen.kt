package com.example.mixtapp.ui.screens.signup

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
fun SignUpScreen(modifier: Modifier = Modifier) {
    var username by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var acceptedTerms by rememberSaveable { mutableStateOf(false) }

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
                text = stringResource(R.string.create_account),
                color = Color.White,
                fontSize = 31.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = stringResource(R.string.signup_description),
                color = TextPink,
                fontSize = 17.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            FigmaTextField(
                label = stringResource(R.string.username),
                placeholder = stringResource(R.string.choose_username),
                value = username,
                onValueChange = { username = it },
                icon = FieldIcon.User
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = stringResource(R.string.email),
                placeholder = stringResource(R.string.enter_your_email),
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                icon = FieldIcon.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = stringResource(R.string.password),
                placeholder = stringResource(R.string.create_password),
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                icon = FieldIcon.Lock
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = stringResource(R.string.confirm_password),
                placeholder = stringResource(R.string.repeat_password),
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                icon = FieldIcon.Lock
            )

            Spacer(modifier = Modifier.height(29.dp))

            TermsRow(
                checked = acceptedTerms,
                onCheckedChange = { acceptedTerms = !acceptedTerms },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(26.dp))

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
                    text = stringResource(R.string.sign_up_btn),
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

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) {
                        append(stringResource(R.string.already_have_account))
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFFF8CAA),
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        append(stringResource(R.string.log_in))
                    }
                },
                fontSize = 19.sp,
                lineHeight = 24.sp
            )

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
private fun TermsRow(
    checked: Boolean,
    onCheckedChange: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(29.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(
                    width = 2.2.dp,
                    color = PalePink,
                    shape = RoundedCornerShape(5.dp)
                )
                .clickable(onClick = onCheckedChange),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Canvas(modifier = Modifier.size(17.dp)) {
                    val stroke = Stroke(width = 3.4f, cap = StrokeCap.Round)
                    drawLine(
                        color = PalePink,
                        start = Offset(size.width * 0.08f, size.height * 0.55f),
                        end = Offset(size.width * 0.40f, size.height * 0.86f),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = PalePink,
                        start = Offset(size.width * 0.40f, size.height * 0.86f),
                        end = Offset(size.width * 0.94f, size.height * 0.16f),
                        strokeWidth = stroke.width,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = stringResource(R.string.agree_terms),
            color = Color.White,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = stringResource(R.string.privacy),
            color = Color(0xFFFF8CAA),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun SignUpScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        SignUpScreen()
    }
}
