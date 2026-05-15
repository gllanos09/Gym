package com.tecsup.gymtrackerpro.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.gymtrackerpro.data.local.database.AppDatabase
import com.tecsup.gymtrackerpro.data.local.entity.Usuario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilUsuarioScreen(navController: NavController, usuarioId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var totalRutinas by remember { mutableStateOf(0) }
    var volumenTotal by remember { mutableStateOf(0.0) }

    LaunchedEffect(usuarioId) {
        usuario = db.usuarioDao().buscarPorId(usuarioId)
        totalRutinas = db.usuarioDao().contarRutinas(usuarioId)
        volumenTotal = db.usuarioDao().calcularVolumenTotal(usuarioId) ?: 0.0
    }

    val iniciales = usuario?.nombre?.split(" ")
        ?.take(2)?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
        ?.joinToString("") ?: ""

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2244AA),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Avatar circular con iniciales
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFBBCCEE), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(iniciales, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2244AA))
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(usuario?.nombre ?: "", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("@${usuario?.email?.substringBefore("@") ?: ""}", color = Color.Gray, fontSize = 14.sp)

            Spacer(modifier = Modifier.height(20.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard2(label = "Rutinas", value = totalRutinas.toString(), modifier = Modifier.weight(1f))
                StatCard2(label = "kg totales", value = String.format("%.0f", volumenTotal), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            // Info rows
            PerfilInfoRow(icon = Icons.Default.Email, label = "Email", value = usuario?.email ?: "")
            PerfilInfoRow(icon = Icons.Default.DateRange, label = "Miembro desde", value = usuario?.fechaRegistro ?: "")

            Spacer(modifier = Modifier.weight(1f))

            // Cerrar sesión
            OutlinedButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión")
            }
        }
    }
}

@Composable
fun StatCard2(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4FF))
    ) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2244AA))
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PerfilInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.weight(1f))
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}