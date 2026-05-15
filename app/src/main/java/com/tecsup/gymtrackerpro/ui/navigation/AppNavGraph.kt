package com.tecsup.gymtrackerpro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.gymtrackerpro.ui.screens.*

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        composable("login") {
            LoginScreen(navController = navController)
        }

        composable("registro") {
            RegistroScreen(navController = navController)
        }

        composable(
            route = "menu/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable
            MenuPrincipalScreen(navController = navController, usuarioId = usuarioId)
        }

        composable(
            route = "agregar/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable
            AgregarRutinaScreen(navController = navController, usuarioId = usuarioId)
        }

        composable(
            route = "rutinas/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable
            ListaRutinasScreen(navController = navController, usuarioId = usuarioId)
        }

        composable(
            route = "detalle/{rutinaId}",
            arguments = listOf(navArgument("rutinaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val rutinaId = backStackEntry.arguments?.getInt("rutinaId") ?: return@composable
            DetalleRutinaScreen(navController = navController, rutinaId = rutinaId)
        }

        composable(
            route = "perfil/{usuarioId}",
            arguments = listOf(navArgument("usuarioId") { type = NavType.IntType })
        ) { backStackEntry ->
            val usuarioId = backStackEntry.arguments?.getInt("usuarioId") ?: return@composable
            PerfilUsuarioScreen(navController = navController, usuarioId = usuarioId)
        }
    }
}