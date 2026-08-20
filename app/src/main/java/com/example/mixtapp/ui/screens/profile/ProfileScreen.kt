package com.example.mixtapp.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
            .background(DeepBackground)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        InformacionUsuario()

        Separador()

        Subtitulos(texto = stringResource(R.string.favoritos))

        Spacer(modifier = Modifier.height(12.dp))

        Favoritos()

        Spacer(modifier = Modifier.height(32.dp))

        Separador()

        Subtitulos(texto = stringResource(R.string.actividad_reciente))

        UltimaReseña()

        Spacer(modifier = Modifier.height(24.dp))

        Separador()

        Subtitulos(texto = stringResource(R.string.calificaciones))

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
            .background(CircleWine)
            .border(2.dp, PrimaryPink, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.yo),
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
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
        Text(
            text = "username",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        IconoUsuario()
        Spacer(modifier = Modifier.height(30.dp))
        Text(
            text = stringResource(R.string.user_stats),
            color = PalePink,
            fontSize = 13.sp
        )
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
            color = FieldBorder.copy(alpha = 0.3f),
            thickness = 1.dp
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
        color = TextPink,
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
                    .background(FieldBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, FieldBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fav ${index + 1}",
                    color = PalePink.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun UltimaReseña(
    modifier: Modifier = Modifier
){
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(FieldBackground, RoundedCornerShape(8.dp))
                .border(1.dp, FieldBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = " Album ",
                color = PalePink.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = "Nombre Canción",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "${stringResource(R.string.reviewed_today)} • 5 estrellas",
                color = PalePink,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun Calificaciones(){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(
            text = stringResource(R.string.star_rating_format, 1),
            color = PalePink,
            fontSize = 12.sp
        )
        Text(
            text = stringResource(R.string.star_rating_format, 5),
            color = PalePink,
            fontSize = 12.sp
        )
    }
}

@Composable
@Preview
fun ProfileScreenPreview(){
    MixtappTheme(dynamicColor = false) {
        ProfileScreen()
    }
}
