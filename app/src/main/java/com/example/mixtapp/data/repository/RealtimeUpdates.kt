package com.example.mixtapp.data.repository

import com.google.firebase.firestore.Query
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

// Cada suscripcion tiene su propio listener y lo libera al cancelar el ViewModel.
internal fun <T> observeChanges(query: Query, read: suspend () -> Result<T>): Flow<Result<T>> =
    callbackFlow {
        val listener = query.addSnapshotListener { snapshot, error ->
            if (error != null) close(error)
            else if (snapshot != null) trySend(Unit)
        }
        awaitClose { listener.remove() }
    }.conflate().map { read() }
        .retryWhen { error, attempt ->
            if (error is CancellationException) throw error
            emit(Result.failure(if (error is com.google.firebase.firestore.FirebaseFirestoreException) errorDeFirestore(error) else error))
            delay(minOf(30_000L, 1_000L * (attempt + 1)))
            true
        }
