package com.example.mixtapp.ui.screens.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.components.*
import com.example.mixtapp.ui.screens.signup.components.SignUpFooter
import com.example.mixtapp.ui.screens.signup.components.SignUpForm
import com.example.mixtapp.ui.screens.signup.components.SignUpHeader
import com.example.mixtapp.ui.theme.*

@Composable
fun SignUpScreen(modifier: Modifier = Modifier) {

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        AppBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 31.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            SignUpHeader()

            SignUpForm()

            SignUpFooter()

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
