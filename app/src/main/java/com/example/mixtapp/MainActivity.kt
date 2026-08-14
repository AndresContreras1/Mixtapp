package com.example.mixtapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import com.example.mixtapp.ui.theme.MixtappTheme

private val ScreenBackground = Color(0xFF260615)
private val DeepBackground = Color(0xFF1B020D)
private val CircleWine = Color(0xFF612D53)
private val CircleBerry = Color(0xFFA02249)
private val FieldBackground = Color(0xFF67122C)
private val FieldBorder = Color(0xFF9B4967)
private val PrimaryPink = Color(0xFFA02249)
private val LogoPink = Color(0xFFB11F50)
private val TextPink = Color(0xFFE4B4C1)
private val PalePink = Color(0xFFF8D6E0)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MixtappTheme(dynamicColor = false) {
                val view = LocalView.current
                SideEffect {
                    val window = (view.context as ComponentActivity).window
                    window.statusBarColor = android.graphics.Color.TRANSPARENT
                    window.navigationBarColor = android.graphics.Color.TRANSPARENT
                    WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                    WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ScreenBackground
                ) {
                    SignUpScreen()
                }
            }
        }
    }
}

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
                text = "Create Account",
                color = Color.White,
                fontSize = 31.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Start tracking your albums, ratings, and\nfavorite listens.",
                color = TextPink,
                fontSize = 17.sp,
                lineHeight = 23.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            FigmaTextField(
                label = "Username",
                placeholder = "Choose a username",
                value = username,
                onValueChange = { username = it },
                icon = FieldIcon.User
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = "Email",
                placeholder = "Enter your Email",
                value = email,
                onValueChange = { email = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                icon = FieldIcon.Email
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = "Password",
                placeholder = "Create password",
                value = password,
                onValueChange = { password = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                icon = FieldIcon.Lock
            )

            Spacer(modifier = Modifier.height(14.dp))

            FigmaTextField(
                label = "Confirm",
                placeholder = "Repeat password",
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
                    text = "SIGN UP",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(19.dp))

            OrDivider(modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = Color.White)) {
                        append("Already have an Account?  ")
                    }
                    withStyle(
                        SpanStyle(
                            color = Color(0xFFFF8CAA),
                            fontWeight = FontWeight.Black
                        )
                    ) {
                        append("Log in")
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
private fun BoxScope.DecorativeCircles() {
    Box(
        modifier = Modifier
            .size(240.dp)
            .offset(x = (-114).dp, y = (-38).dp)
            .clip(CircleShape)
            .background(CircleWine.copy(alpha = 0.58f))
    )

    Box(
        modifier = Modifier
            .size(228.dp)
            .align(Alignment.Center)
            .offset(y = 32.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.11f))
    )

    Box(
        modifier = Modifier
            .size(275.dp)
            .align(Alignment.BottomEnd)
            .offset(x = 112.dp, y = (-24).dp)
            .clip(CircleShape)
            .background(CircleBerry.copy(alpha = 0.33f))
    )
}

@Composable
private fun HeaderLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Color.White)) {
                    append("Mixt")
                }
                withStyle(SpanStyle(color = LogoPink)) {
                    append("app")
                }
            },
            fontSize = 47.sp,
            lineHeight = 52.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.Serif
        )

        Box(
            modifier = Modifier
                .size(66.dp)
                .clip(CircleShape)
                .background(Color(0xFF742A44)),
            contentAlignment = Alignment.Center
        ) {
            /*Text(
                text = "YO",
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif
            )*/
        }
    }
}

@Composable
private fun FigmaTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: FieldIcon,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(73.dp)
                .clip(RoundedCornerShape(13.dp))
                .background(FieldBackground.copy(alpha = 0.91f))
                .border(
                    width = 1.2.dp,
                    color = FieldBorder,
                    shape = RoundedCornerShape(13.dp)
                )
                .padding(horizontal = 30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FieldIconView(
                icon = icon,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.size(24.dp))

            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = TextStyle(
                    color = PalePink,
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(PalePink),
                keyboardOptions = keyboardOptions,
                visualTransformation = visualTransformation,
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isEmpty()) {
                            Text(
                                text = placeholder,
                                color = TextPink.copy(alpha = 0.88f),
                                fontSize = 20.sp,
                                lineHeight = 24.sp
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
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
            text = "I agree to the Mixtapp terms",
            color = Color.White,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "Privacy",
            color = Color(0xFFFF8CAA),
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun OrDivider(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 83.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(25.dp)
    ) {
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(Color(0xFF8B3654))
        )
        Text(
            text = "OR",
            color = PalePink,
            fontSize = 16.sp,
            fontWeight = FontWeight.Black
        )
        Box(
            modifier = Modifier
                .height(1.dp)
                .weight(1f)
                .background(Color(0xFF8B3654))
        )
    }
}

private enum class FieldIcon {
    User,
    Email,
    Lock
}

@Composable
private fun FieldIconView(
    icon: FieldIcon,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val color = PalePink
        val strokeWidth = 2.6f
        when (icon) {
            FieldIcon.User -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.19f,
                    center = Offset(size.width * 0.5f, size.height * 0.30f),
                    style = Stroke(width = strokeWidth)
                )
                drawArc(
                    color = color,
                    startAngle = 205f,
                    sweepAngle = 130f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.12f, size.height * 0.50f),
                    size = Size(size.width * 0.76f, size.height * 0.58f),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            FieldIcon.Email -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.08f, size.height * 0.20f),
                    size = Size(size.width * 0.84f, size.height * 0.60f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(1.5.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.10f, size.height * 0.25f),
                    end = Offset(size.width * 0.50f, size.height * 0.56f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.90f, size.height * 0.25f),
                    end = Offset(size.width * 0.50f, size.height * 0.56f),
                    strokeWidth = strokeWidth,
                    cap = StrokeCap.Round
                )
            }

            FieldIcon.Lock -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.25f, size.height * 0.43f),
                    size = Size(size.width * 0.50f, size.height * 0.39f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )
                val shackle = Path().apply {
                    moveTo(size.width * 0.32f, size.height * 0.45f)
                    cubicTo(
                        size.width * 0.32f,
                        size.height * 0.19f,
                        size.width * 0.68f,
                        size.height * 0.19f,
                        size.width * 0.68f,
                        size.height * 0.45f
                    )
                }
                drawPath(
                    path = shackle,
                    color = color,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.045f,
                    center = Offset(size.width * 0.50f, size.height * 0.61f)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
fun SignUpScreenPreview() {
    MixtappTheme(dynamicColor = false) {
        SignUpScreen()
    }
}
