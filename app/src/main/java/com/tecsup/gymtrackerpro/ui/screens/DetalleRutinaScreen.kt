package com.tecsup.gymtrackerpro.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tecsup.gymtrackerpro.data.local.database.AppDatabase
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleRutinaScreen(navController: NavController, rutinaId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nombreRutina by remember { mutableStateOf("") }
    var ejercicio by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticiones by remember { mutableStateOf("") }
    var pesoKg by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }
    var cargando by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(rutinaId) {
        val rutina = db.rutinaDao().buscarPorId(rutinaId)
        rutina?.let {
            nombreRutina = it.nombre
            ejercicio = it.ejercicio
            series = it.series.toString()
            repeticiones = it.repeticiones.toString()
            pesoKg = it.pesoKg.toString()
            notas = it.notas
        }
        cargando = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Rutina") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        if (cargando) {
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nombreRutina,
                    onValueChange = { nombreRutina = it },
                    label = { Text("Nombre de la rutina") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = ejercicio,
                    onValueChange = { ejercicio = it },
                    label = { Text("Ejercicio") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = series,
                        onValueChange = { series = it },
                        label = { Text("Series") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = repeticiones,
                        onValueChange = { repeticiones = it },
                        label = { Text("Reps") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pesoKg,
                        onValueChange = { pesoKg = it },
                        label = { Text("Peso (kg)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = notas,
                    onValueChange = { notas = it },
                    label = { Text("Notas (opcional)") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        val s = series.toIntOrNull()
                        val r = repeticiones.toIntOrNull()
                        val p = pesoKg.toDoubleOrNull()
                        when {
                            nombreRutina.isBlank() ->
                                scope.launch { snackbarHostState.showSnackbar("El nombre no puede estar vacío") }
                            ejercicio.isBlank() ->
                                scope.launch { snackbarHostState.showSnackbar("El ejercicio no puede estar vacío") }
                            s == null || s <= 0 ->
                                scope.launch { snackbarHostState.showSnackbar("Series inválidas") }
                            r == null || r <= 0 ->
                                scope.launch { snackbarHostState.showSnackbar("Repeticiones inválidas") }
                            p == null || p < 0 ->
                                scope.launch { snackbarHostState.showSnackbar("Peso inválido") }
                            else -> {
                                scope.launch {
                                    val rutina = db.rutinaDao().buscarPorId(rutinaId) ?: return@launch
                                    db.rutinaDao().actualizar(
                                        rutina.copy(
                                            nombre = nombreRutina.trim(),
                                            ejercicio = ejercicio.trim(),
                                            series = s,
                                            repeticiones = r,
                                            pesoKg = p,
                                            notas = notas.trim()
                                        )
                                    )
                                    navController.popBackStack()
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Guardar cambios")
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar rutina") },
            text = { Text("¿Eliminar esta rutina? La acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val rutina = db.rutinaDao().buscarPorId(rutinaId)
                        rutina?.let { db.rutinaDao().eliminar(it) }
                        navController.popBackStack()
                    }
                    showDeleteDialog = false
                }) {
                    Text("Eliminar", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}