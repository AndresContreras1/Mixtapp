package com.example.mixtapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AppTextField(
    label: String,
    placeholder: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: FieldIcon,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    isPassword: Boolean = false,
    passwordVisible: Boolean = false,
    onPasswordVisibleChange: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val currentTransformation =
        if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            visualTransformation
        }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.91f))
                .border(
                    width = 1.2.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 24.dp),
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                singleLine = true,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurfaceVariant),
                keyboardOptions = keyboardOptions,
                visualTransformation = currentTransformation,
                decorationBox = { innerTextField ->
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = placeholder,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.88f),
                                    fontSize = 18.sp,
                                )
                            }
                            innerTextField()
                        }

                        if (isPassword) {
                            IconButton(
                                onClick = onPasswordVisibleChange,
                                modifier = Modifier.size(24.dp)
                            ) {
                                PasswordVisibilityIcon(passwordVisible = passwordVisible)
                            }
                        }
                    }
                }
            )
        }
    }
}
