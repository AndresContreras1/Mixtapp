package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.Screen
import com.example.mixtapp.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier // Primer parámetro opcional
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        NombreApp()

        Spacer(modifier = Modifier.height(8.dp))

        Titulo(texto= "Sign in")

        Spacer(modifier = Modifier.height(6.dp))

        TextoDescripcion(texto = "Enter your account to keep rating albums and following friends.")

        Spacer(modifier = Modifier.height(32.dp))

        // Campos de registro
        FormularioRegistro()

        Spacer(modifier = Modifier.height(20.dp))

        DivisorOr()

        TextButton(onClick = {/* TO DO */}) {
            Text(text = "Don't have an Account? Sign up", color = AccentPink, fontSize = 13.sp)
        }

    }
}

@Composable
fun NombreApp(
    modifier: Modifier = Modifier
){
    Text(
        modifier = modifier,
        text = "Mixtapp",
        color = PrimaryMaroon,
        fontSize = 42.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun Titulo(
    modifier: Modifier = Modifier,
    texto: String
){
    Text(
        text = texto,
        color = TextLight,
        fontSize = 26.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun TextoDescripcion(
    modifier: Modifier = Modifier,
    texto: String
){
    Text(
        text = texto,
        color = Color.Gray,
        fontSize = 12.sp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

@Composable
fun FormularioRegistro(
    modifier: Modifier = Modifier
){
    // Campo de Email
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text("Email", color = Color.LightGray) },
        placeholder = { Text("Enter your Email", color = Color.Gray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryMaroon,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedLabelColor = AccentPink
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Campo de Contraseña
    OutlinedTextField(
        value = "",
        onValueChange = {},
        label = { Text("Password", color = Color.LightGray) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryMaroon,
            unfocusedBorderColor = Color.Transparent,
            focusedContainerColor = SurfaceCard,
            unfocusedContainerColor = SurfaceCard,
            focusedLabelColor = AccentPink
        ),
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(28.dp))

    // Botón LOGIN

    Button(
        onClick = {/* TO DO*/},
        colors = ButtonDefaults.buttonColors(containerColor = PrimaryMaroon),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        Text(text = "LOGIN", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }

}

@Composable
fun DivisorOr(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )

        Text(
            text = "OR",
            modifier = Modifier.padding(horizontal = 12.dp),
            color = Color.Gray
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )
    }

}

@Composable
@Preview
fun LoginScreenPreview(){
    LoginScreen()
}

