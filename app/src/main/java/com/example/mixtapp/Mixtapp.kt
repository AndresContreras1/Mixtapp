package com.example.mixtapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mixtapp.ui.navigation.AppNavigation
import com.example.mixtapp.ui.navigation.BottomNav
import com.example.mixtapp.ui.navigation.NavigationLogic
import com.example.mixtapp.ui.navigation.Screen
import com.example.mixtapp.ui.utils.WriteReviewFab
import com.google.firebase.messaging.FirebaseMessaging

@Composable
fun Mixtapp(modifier: Modifier = Modifier) {
    // El contexto sirve para revisar si el permiso de notificaciones ya esta concedido
    val context = LocalContext.current

    // Launcher para pedir el permiso de notificaciones; si se niega, solo se pierden las
    // notificaciones y la app sigue igual
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                Log.d("NotificationPermission", "Permission granted")
            } else {
                Log.d("NotificationPermission", "Permission denied")
            }
        }
    )

    // Apenas se abre la app: desde Android 13 (Tiramisu) hay que pedir el permiso, y solo si
    // todavia no esta concedido
    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // El token de este celular en el Logcat (filtro Token), para probar desde la consola
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Token", task.result)
            } else {
                Log.e("Token", "Error obteniendo token", task.exception)
            }
        }
    }

    val navController = rememberNavController()
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    val mostrarBarra = NavigationLogic.shouldShowBottomBar(currentRoute)

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (mostrarBarra) {
                BottomNav(navController = navController)
            }
        },
        floatingActionButton = {
            if (mostrarBarra) {
                // El + abre la lista del catalogo para elegir que calificar
                WriteReviewFab(
                    onClick = { navController.navigate(Screen.SelectItem.route) }
                )
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            modifier = Modifier.padding(it)
        )
    }
}
