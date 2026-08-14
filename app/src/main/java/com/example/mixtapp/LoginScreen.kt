package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.AccentPink
import com.example.mixtapp.ui.theme.DarkBackground
import com.example.mixtapp.ui.theme.PrimaryMaroon
import com.example.mixtapp.ui.theme.SurfaceCard
import com.example.mixtapp.ui.theme.TextLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    modifier: Modifier = Modifier
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

        Titulo(texto = stringResource(R.string.ingresar))

        Spacer(modifier = Modifier.height(6.dp))

        TextoDescripcion(
            texto = stringResource(R.string.descripcion_login),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        FormularioRegistro(modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(20.dp))

        DivisorOr(modifier = Modifier.fillMaxWidth())

        TextButton(onClick = {/* TO DO */}) {
            Text(text = stringResource(R.string.no_tienes_cuenta_registrate), color = AccentPink, fontSize = 13.sp)
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
        modifier = modifier,
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
        modifier = modifier,
        text = texto,
        color = Color.Gray,
        fontSize = 12.sp
    )
}

@Composable
fun FormularioRegistro(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier.fillMaxWidth()
    ){
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(R.string.email), color = Color.LightGray) },
            placeholder = { Text(stringResource(R.string.enter_your_email), color = Color.Gray) },
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

        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text(stringResource(R.string.contrasena), color = Color.LightGray) },
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

        Button(
            onClick = {/* TO DO*/},
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryMaroon),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(stringResource(R.string.ingresar), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}

@Composable
fun DivisorOr(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = Color.Gray
        )

        Text(
            text = stringResource(R.string.o),
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
@Preview(showBackground = true)
fun LoginScreenPreview(){
    LoginScreen()
}
