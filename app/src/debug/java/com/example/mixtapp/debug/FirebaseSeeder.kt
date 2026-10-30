package com.example.mixtapp.debug

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Sube datos falsos a la instancia de Firestore que reciba: la del emulador o la de Firebase real
class FirebaseSeeder(
    private val db: FirebaseFirestore,
    private val factory: FakeDataFactory = FakeDataFactory()
) {

    data class Resumen(val usuarios: Int, val albumes: Int, val resenas: Int) {
        fun describir() = "$usuarios usuarios, $albumes albumes, $resenas resenas"
    }

    // Orden: usuarios y albumes primero, porque cada resena los referencia
    suspend fun cargar(usuarios: Int = 5, albumes: Int = 10, resenasPorAlbum: Int = 3): Resumen {
        require(usuarios > 0 && albumes > 0)
        require(resenasPorAlbum in 0..usuarios) { "No pueden haber mas resenas por album que usuarios" }
        require(usuarios + albumes + albumes * resenasPorAlbum <= 500) { "Un batch admite 500 escrituras" }

        val batch = db.batch()

        val datosUsuarios = (0 until usuarios).map { factory.crearUsuario(FakeDataFactory.idUsuario(it)) }
        datosUsuarios.forEachIndexed { i, datos ->
            batch.set(db.collection("usuarios").document(FakeDataFactory.idUsuario(i)), datos)
        }

        val datosAlbumes = (0 until albumes).map { factory.crearAlbum(FakeDataFactory.idAlbum(it)) }
        datosAlbumes.forEach { album ->
            batch.set(db.collection("albumes").document(album.id), album)
        }

        var resenas = 0
        datosAlbumes.forEachIndexed { a, album ->
            repeat(resenasPorAlbum) { k ->
                // (a + k) % usuarios evita que un mismo usuario resene dos veces el mismo album
                val u = (a + k) % usuarios
                val usuario = datosUsuarios[u]
                val resena = factory.crearResena(
                    usuarioId = FakeDataFactory.idUsuario(u),
                    nombreUsuario = usuario["nombre"] as String,
                    fotoUsuario = usuario["fotoUrl"] as String?,
                    album = album
                )
                batch.set(db.collection("reviews").document(FakeDataFactory.idResena(a, u)), resena)
                resenas++
            }
        }

        batch.commit().await()
        return Resumen(usuarios, albumes, resenas)
    }
}
