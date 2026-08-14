package com.example.mixtapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.*

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        InformacionUsuario()

        Separador()

        Subtitulos(texto = "Favoritos")

        Spacer(modifier = Modifier.height(12.dp))

        // Vitrina de 4 álbumes favoritos estilo Figma
        Favoritos()

        Spacer(modifier = Modifier.height(32.dp))

        Separador()

        Subtitulos(texto = "Actividad reciente")

        UltimaReseña()

        Separador()

        Subtitulos(texto = "Calificaciones")

        Calificaciones()

    }
}

@Composable
fun IconoUsuario(
    modifier: Modifier = Modifier
){
    Box(
        modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(PrimaryMaroon),
        contentAlignment = Alignment.Center
    ) {
        Text(stringResource(R.string.yo), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
    }
}

@Composable
fun InformacionUsuario(
    modifier: Modifier = Modifier
){
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("username", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        IconoUsuario()
        Spacer(modifier = Modifier.height(30.dp))
        Text("128 reviews • 64 albums • 18 lists", color = Color.Gray, fontSize = 13.sp)
    }
}

@Composable
fun Separador(
    modifier: Modifier = Modifier
){

    Column(
        modifier = modifier
    ){
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
fun Subtitulos(
    modifier: Modifier = Modifier,
    texto: String
){
    Text(
        text = texto,
        color = AccentPink,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
    )
}

@Composable
fun Favoritos(){
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(4) { index ->
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(SurfaceCard, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Fav ${index + 1}", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun UltimaReseña(
    modifier: Modifier = Modifier
){
    Row(){
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(SurfaceCard, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Esto cambiara por una imagen
            Text(" Album ", color = Color.Gray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            // Informacion que luego se le pasara por argumentos a la funcion
            Text(text="Nombre Cancion")
            Text(text="revisado hoy · 5 estrellas")
            Text(text="estrellas")
        }
    }

}

@Composable
fun Calificaciones(){
    Row(){

        // Con informacion de la BDD hacer un diagrama
        // Que cuente la cantidad de cada tipo de notas

        Column(){
            // Cambiar por imagen
            Text(text="1 estrella")
            Spacer(modifier= Modifier.weight(1f))
            Text(text="5 estrellas")
        }
    }
}

@Composable
@Preview
fun ProfileScreenPreview(){
    ProfileScreen()
}