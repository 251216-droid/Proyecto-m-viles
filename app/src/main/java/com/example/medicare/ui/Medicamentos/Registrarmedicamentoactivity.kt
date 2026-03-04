package com.example.medicare.ui.medicamentos

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.MedicareApp
import com.example.medicare.R
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.ui.theme.MediCareTheme
import java.util.Calendar
import java.util.Locale

class RegistrarMedicamentoActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MedicareApp
        val viewModel = ViewModelProvider(
            this,
            MedicamentoViewModelFactory(app.database.medicamentoDao(), app.database.usuarioDao())
        )[MedicamentoViewModel::class.java]

        val medicamentoId = intent.getIntExtra("MEDICAMENTO_ID", -1)
        val idUsuario = intent.getIntExtra("ID_USUARIO", 1)

        setContent {
            MediCareTheme {
                var medicamentoAEditar by remember { mutableStateOf<Medicamento?>(null) }

                LaunchedEffect(medicamentoId) {
                    if (medicamentoId != -1) {
                        medicamentoAEditar = viewModel.obtenerMedicamentoPorId(medicamentoId)
                    }
                }

                RegistrarMedicamentoScreen(
                    medicamentoExistente = medicamentoAEditar,
                    onCancelar = { finish() },
                    onGuardar = { nombre, tipo, dosis, frecuencia, primeraToma, duracion, contenido, categoria, estado ->
                        if (medicamentoId == -1) {
                            viewModel.guardarMedicamento(
                                nombre, tipo, dosis, frecuencia, primeraToma, 
                                duracion, contenido, categoria, estado, idUsuario
                            )
                        } else {
                            val actualizado = Medicamento(
                                idMedicamento = medicamentoId,
                                idUsuarioFk = idUsuario,
                                nombreMedicamento = nombre,
                                tipoPresentacion = tipo,
                                dosis = dosis,
                                frecuencia = frecuencia,
                                primeraToma = primeraToma,
                                duracion = duracion,
                                contenido = contenido,
                                categoria = categoria,
                                estadoMedicamento = estado
                            )
                            viewModel.actualizarMedicamento(actualizado, idUsuario)
                        }
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarMedicamentoScreen(
    medicamentoExistente: Medicamento?,
    onCancelar: () -> Unit,
    onGuardar: (String, String, String, String, String, String, String, String, String) -> Unit
) {
    val azul = Color(0xFF0086FF)
    val context = LocalContext.current

    var nombre by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("") }
    var dosis by remember { mutableStateOf("") }
    var frecuencia by remember { mutableStateOf("") }
    var primeraToma by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("Activo") }

    var tipoExpandido by remember { mutableStateOf(false) }
    var categoriaExpandida by remember { mutableStateOf(false) }
    var estadoExpandido by remember { mutableStateOf(false) }

    val tiposOpciones = listOf("Tableta", "Cápsula", "Jarabe", "Inyección", "Gotas", "Crema", "Parche")
    val categoriaOpciones = listOf("Pediátrico", "Infantil", "Adulto")
    val estadoOpciones = listOf("Activo", "Suspendido", "Terminado")

    LaunchedEffect(medicamentoExistente) {
        medicamentoExistente?.let {
            nombre = it.nombreMedicamento
            tipo = it.tipoPresentacion
            dosis = it.dosis
            frecuencia = it.frecuencia
            primeraToma = it.primeraToma
            duracion = it.duracion
            contenido = it.contenido
            categoria = it.categoria
            estado = it.estadoMedicamento
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column {

            // ── Encabezado azul ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4AA3FF), Color(0xFF0086FF))
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(top = 40.dp, bottom = 80.dp, start = 16.dp, end = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onCancelar) {
                            Icon(
                                painter = painterResource(id = R.drawable.atras),
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                tint = Color.Unspecified
                            )
                        }
                        Text(
                            text = if (medicamentoExistente == null)
                                "Registrar\nMedicamento"
                            else
                                "Editar\nMedicamento",
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (medicamentoExistente == null)
                            "Agrega un nuevo medicamento"
                        else
                            "Modifica los datos guardados",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }

            // ── Tarjeta del formulario ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 20.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    // ── Nombre ──
                    LabelMed("Nombre del medicamento:")
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Ej: Paracetamol") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Tipo (dropdown) ──
                    LabelMed("Tipo de presentación:")
                    ExposedDropdownMenuBox(
                        expanded = tipoExpandido,
                        onExpandedChange = { tipoExpandido = !tipoExpandido }
                    ) {
                        OutlinedTextField(
                            value = tipo,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona el tipo") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpandido) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = azul,
                                unfocusedBorderColor = Color(0xFFCCCCCC)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = tipoExpandido,
                            onDismissRequest = { tipoExpandido = false }
                        ) {
                            tiposOpciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        tipo = opcion
                                        tipoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Dosis ──
                    LabelMed("Dosis:")
                    OutlinedTextField(
                        value = dosis,
                        onValueChange = { dosis = it },
                        placeholder = { Text("Ej: 1 tableta, 500mg") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Frecuencia ──
                    LabelMed("Frecuencia:")
                    OutlinedTextField(
                        value = frecuencia,
                        onValueChange = { frecuencia = it },
                        placeholder = { Text("cada 8h") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Primera toma (TimePicker) ──
                    LabelMed("Primera toma:")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                val calendar = Calendar.getInstance()
                                TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        val cal = Calendar.getInstance()
                                        cal.set(Calendar.HOUR_OF_DAY, hour)
                                        cal.set(Calendar.MINUTE, minute)
                                        val sdf = java.text.SimpleDateFormat("hh:mm a", Locale.getDefault())
                                        primeraToma = sdf.format(cal.time)
                                    },
                                    calendar.get(Calendar.HOUR_OF_DAY),
                                    calendar.get(Calendar.MINUTE),
                                    false
                                ).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = primeraToma,
                            onValueChange = { },
                            placeholder = { Text("Selecciona la hora") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true,
                            readOnly = true,
                            enabled = false,
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.calendario),
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp),
                                    tint = azul
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFCCCCCC),
                                disabledTextColor = Color.Black,
                                disabledPlaceholderColor = Color.Gray,
                                disabledTrailingIconColor = azul
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Duración ──
                    LabelMed("Duración:")
                    OutlinedTextField(
                        value = duracion,
                        onValueChange = { duracion = it },
                        placeholder = { Text("Ej: 7dias") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Contenido ──
                    LabelMed("Contenido:")
                    OutlinedTextField(
                        value = contenido,
                        onValueChange = { contenido = it },
                        placeholder = { Text("Ej: Caja con 30 tabletas") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Categoría (dropdown) ──
                    LabelMed("Categoría:")
                    ExposedDropdownMenuBox(
                        expanded = categoriaExpandida,
                        onExpandedChange = { categoriaExpandida = !categoriaExpandida }
                    ) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = {},
                            readOnly = true,
                            placeholder = { Text("Selecciona categoría") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = azul,
                                unfocusedBorderColor = Color(0xFFCCCCCC)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = categoriaExpandida,
                            onDismissRequest = { categoriaExpandida = false }
                        ) {
                            categoriaOpciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        categoria = opcion
                                        categoriaExpandida = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ── Estado (dropdown) ──
                    LabelMed("Estado:")
                    ExposedDropdownMenuBox(
                        expanded = estadoExpandido,
                        onExpandedChange = { estadoExpandido = !estadoExpandido }
                    ) {
                        OutlinedTextField(
                            value = estado,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpandido) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedBorderColor = azul,
                                unfocusedBorderColor = Color(0xFFCCCCCC)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = estadoExpandido,
                            onDismissRequest = { estadoExpandido = false }
                        ) {
                            estadoOpciones.forEach { opcion ->
                                DropdownMenuItem(
                                    text = { Text(opcion) },
                                    onClick = {
                                        estado = opcion
                                        estadoExpandido = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // ── Botones ──
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
                        ) {
                            Text(text = "Cancelar", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.ExtraBold)
                        }

                        Button(
                            onClick = {
                                if (nombre.isNotBlank() && tipo.isNotBlank() && dosis.isNotBlank() && categoria.isNotBlank()) {
                                    onGuardar(nombre, tipo, dosis, frecuencia, primeraToma, duracion, contenido, categoria, estado)
                                }
                            },
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azul)
                        ) {
                            Text(text = "Guardar", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelMed(texto: String) {
    Text(
        text = texto,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF0086FF),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}