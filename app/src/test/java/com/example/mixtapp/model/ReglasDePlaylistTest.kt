package com.example.mixtapp.model

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.model.TipoDePlaylist
import com.example.mixtapp.data.model.armarPlaylists
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Las playlists hechas para mi: las mezclas de mis generos, Descubre, Top Colombia y lo mas escuchado
class ReglasDePlaylistTest {

    private fun cancion(id: String, genero: String, likes: Int = 0, tendenciaColombia: Int? = null) = ItemConResenasUi(
        item = LocalPreviewProvider.emptinessMachine.copy(
            id = id, tipo = TIPO_CANCION, genero = genero, likes = likes, tendenciaColombia = tendenciaColombia,
        ),
        rating = 0.0,
        estrellas = 0,
        ratingCount = 0,
        recommendRate = 0,
        recomiendan = 0,
        yaCalificado = false,
        reviews = emptyList(),
    )

    private val catalogo = listOf(
        cancion("metal_1", "Metal", likes = 5),
        cancion("metal_2", "Metal", likes = 9),
        cancion("rock_1", "Rock", likes = 3, tendenciaColombia = 2),
        cancion("rock_2", "Rock", likes = 1, tendenciaColombia = 1),
    )

    @Test
    fun armarPlaylists_meGustaElMetal_suMezclaLoMasPopularPrimero() {
        //Arrange
        val meGustan = listOf(catalogo[0].item)
        //Act
        val playlists = armarPlaylists(catalogo = catalogo, meGustan = meGustan)
        //Assert
        val mezcla = playlists.first { it.tipo == TipoDePlaylist.MezclaDeGenero }
        assertThat(mezcla.genero).isEqualTo("Metal")
        assertThat(mezcla.canciones.map { it.id }).containsExactly("metal_2", "metal_1").inOrder()
    }

    @Test
    fun armarPlaylists_descubre_sinLoQueYaMeGusta() {
        //Arrange
        val meGustan = listOf(catalogo[0].item)
        //Act
        val descubre = armarPlaylists(catalogo = catalogo, meGustan = meGustan).first { it.tipo == TipoDePlaylist.Descubre }
        //Assert
        assertThat(descubre.canciones.map { it.id }).containsExactly("metal_2")
    }

    @Test
    fun armarPlaylists_topColombia_porSuPuesto() {
        //Act
        val top = armarPlaylists(catalogo = catalogo, meGustan = emptyList()).first { it.tipo == TipoDePlaylist.TopColombia }
        //Assert
        assertThat(top.canciones.map { it.id }).containsExactly("rock_2", "rock_1").inOrder()
    }

    @Test
    fun armarPlaylists_sinLikes_sinMezclasNiDescubre() {
        //Act
        val tipos = armarPlaylists(catalogo = catalogo, meGustan = emptyList()).map { it.tipo }
        //Assert: sin gustos solo quedan las listas de todos
        assertThat(tipos).containsExactly(TipoDePlaylist.TopColombia, TipoDePlaylist.LoMasEscuchado).inOrder()
    }
}
