package com.tecsup.gymtrackerpro.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tecsup.gymtrackerpro.data.local.database.AppDatabase
import com.tecsup.gymtrackerpro.data.local.entity.Rutina
import com.tecsup.gymtrackerpro.data.remote.api.RetrofitInstance
import com.tecsup.gymtrackerpro.data.remote.model.ExerciseInfo
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarRutinaScreen(navController: NavController, usuarioId: Int) {
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nombreRutina by remember { mutableStateOf("") }
    var ejercicioSeleccionado by remember { mutableStateOf("") }
    var series by remember { mutableStateOf("") }
    var repeticiones by remember { mutableStateOf("") }
    var pesoKg by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var showCatalog by remember { mutableStateOf(false) }
    var ejerciciosBusqueda by remember { mutableStateOf("") }
    var catalogoEjercicios by remember { mutableStateOf<List<ExerciseInfo>>(emptyList()) }
    var cargandoCatalogo by remember { mutableStateOf(false) }
    var errorCatalogo by remember { mutableStateOf<String?>(null) }

    fun cargarEjercicios(query: String = "") {
        cargandoCatalogo = true
        errorCatalogo = null
        scope.launch {
            try {
                val resp = RetrofitInstance.api.getEjercicios(limit = 100)
                val todos = resp.results
                catalogoEjercicios = if (query.isBlank()) todos
                else todos.filter { it.getNombre().contains(query, ignoreCase = true) }
            } catch (e: Exception) {
                errorCatalogo = "No se pudo cargar el catálogo. Revisa tu conexión."
            } finally {
                cargandoCatalogo = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Rutina") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = ejercicioSeleccionado,
                    onValueChange = { ejercicioSeleccionado = it },
                    label = { Text("Ejercicio") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedButton(onClick = {
                    showCatalog = true
                    cargarEjercicios()
                }) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Catálogo")
                }
            }
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
                            scope.launch { snackbarHostState.showSnackbar("Ingresa un nombre para la rutina") }
                        ejercicioSeleccionado.isBlank() ->
                            scope.launch { snackbarHostState.showSnackbar("Selecciona o escribe un ejercicio") }
                        s == null || s <= 0 ->
                            scope.launch { snackbarHostState.showSnackbar("Series inválidas") }
                        r == null || r <= 0 ->
                            scope.launch { snackbarHostState.showSnackbar("Repeticiones inválidas") }
                        p == null || p < 0 ->
                            scope.launch { snackbarHostState.showSnackbar("Peso inválido") }
                        else -> {
                            scope.launch {
                                val fecha = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                                val rutina = Rutina(
                                    usuarioId = usuarioId,
                                    nombre = nombreRutina.trim(),
                                    ejercicio = ejercicioSeleccionado.trim(),
                                    series = s,
                                    repeticiones = r,
                                    pesoKg = p,
                                    fecha = fecha,
                                    notas = notas.trim()
                                )
                                db.rutinaDao().insertar(rutina)
                                navController.popBackStack()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar rutina")
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showCatalog) {
        AlertDialog(
            onDismissRequest = { showCatalog = false },
            title = {
                Column {
                    Text("Catálogo de ejercicios", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = ejerciciosBusqueda,
                        onValueChange = {
                            ejerciciosBusqueda = it
                            cargarEjercicios(it)
                        },
                        placeholder = { Text("Buscar ejercicio...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            text = {
                Box(modifier = Modifier.height(300.dp)) {
                    when {
                        cargandoCatalogo -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        errorCatalogo != null -> Text(errorCatalogo!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                        catalogoEjercicios.isEmpty() -> Text("No se encontraron ejercicios", modifier = Modifier.align(Alignment.Center))
                        else -> LazyColumn {
                            items(catalogoEjercicios) { ejercicio ->
                                ListItem(
                                    headlineContent = { Text(ejercicio.getNombre()) },
                                    modifier = Modifier.clickable {
                                        ejercicioSeleccionado = ejercicio.getNombre()
                                        showCatalog = false
                                    }
                                )
                                HorizontalDivider()
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCatalog = false }) { Text("Cerrar") }
            }
        )
    }
}