package com.example.mixtapp.ui.screens.mapa.components

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ResenaEnMapaUi
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Lo cerca que arranca el mapa: se ve una ciudad
private const val ZOOM_INICIAL = 12.0

// El mapa de OpenStreetMap (osmdroid, sin servicios de Google) con un marcador por resena. El mapa
// es una vista de Android, no un composable: AndroidView la mete en la pantalla de Compose. Tocar
// un marcador avisa con el id de su resena
@Composable
fun MapaOsm(
    resenas: List<ResenaEnMapaUi>,
    centro: Coordenadas,
    onMarcadorClick: (reviewId: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    // La vista del mapa se crea una sola vez y se conserva entre recomposiciones
    val mapView = remember { crearMapa(context) }

    // Al salir de la pantalla se liberan los recursos del mapa
    DisposableEffect(Unit) {
        onDispose { mapView.onDetach() }
    }

    AndroidView(
        factory = { mapView },
        modifier = modifier,
        // Se llama cuando cambian las resenas o el centro: se vuelven a poner los marcadores
        update = { mapa ->
            val nuevoCentro = GeoPoint(centro.latitud, centro.longitud)
            if (mapa.tag != nuevoCentro) {
                mapa.controller.setCenter(nuevoCentro)
                mapa.tag = nuevoCentro
            }

            mapa.overlays.removeAll { it is Marker }
            resenas.forEach { resena ->
                val marcador = Marker(mapa)
                marcador.position = GeoPoint(resena.latitud, resena.longitud)
                marcador.title = resena.review.item.titulo
                marcador.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                // true: el toque lo maneja la app y no sale la burbuja de osmdroid
                marcador.setOnMarkerClickListener { _, _ ->
                    onMarcadorClick(resena.review.id)
                    true
                }
                mapa.overlays.add(marcador)
            }
            mapa.invalidate()
        },
    )
}

// OpenStreetMap pide que cada app se identifique al bajar los mapas: se usa el paquete de la app
private fun crearMapa(context: Context): MapView {
    Configuration.getInstance().load(context, context.getSharedPreferences("osmdroid", Context.MODE_PRIVATE))
    Configuration.getInstance().userAgentValue = context.packageName

    val mapa = MapView(context)
    mapa.setTileSource(TileSourceFactory.MAPNIK)
    mapa.setMultiTouchControls(true)
    mapa.controller.setZoom(ZOOM_INICIAL)
    return mapa
}
