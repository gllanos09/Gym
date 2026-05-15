package com.tecsup.gymtrackerpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.tecsup.gymtrackerpro.data.local.database.AppDatabase
import com.tecsup.gymtrackerpro.data.local.entity.Rutina
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaRutinasScreen(navController: NavController, usuarioId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()

    val rutinas by db.rutinaDao().listarPorUsuario(usuarioId).collectAsState(initial = emptyList())
    var rutinaAEliminar by remember { mutableStateOf<Rutina?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis rutinas", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2244AA),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("agregar/$usuarioId") },
                containerColor = Color(0xFF2244AA)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva rutina", tint = Color.White)
            }
        }
    ) { padding ->
        if (rutinas.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No tienes rutinas aún", color = Color.Gray)
                    Text("Toca + para agregar una", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(rutinas, key = { it.id }) { rutina ->
                    RutinaCard(
                        rutina = rutina,
                        onEdit = { navController.navigate("detalle/${rutina.id}") },
                        onDelete = { rutinaAEliminar = rutina }
                    )
                }
            }
        }
    }

    rutinaAEliminar?.let { rutina ->
        AlertDialog(
            onDismissRequest = { rutinaAEliminar = null },
            title = { Text("Eliminar rutina") },
            text = { Text("¿Estás seguro de que deseas eliminar \"${rutina.nombre}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { db.rutinaDao().eliminar(rutina) }
                    rutinaAEliminar = null
                }) { Text("Eliminar", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { rutinaAEliminar = null }) { Text("Cancelar") }
            }
        )
    }
}

val categoryColors = listOf(
    Color(0xFF2196F3), Color(0xFF4CAF50), Color(0xFFE91E63),
    Color(0xFFFF9800), Color(0xFF9C27B0), Color(0xFF00BCD4)
)

@Composable
fun RutinaCard(rutina: Rutina, onEdit: () -> Unit, onDelete: () -> Unit) {
    val categoryColor = categoryColors[rutina.id % categoryColors.size]
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(rutina.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(
                    rutina.ejercicio,
                    color = categoryColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "${rutina.series} series × ${rutina.repeticiones} reps · ${rutina.pesoKg} kg · ${rutina.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF2244AA))
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
            }
        }
    }
}