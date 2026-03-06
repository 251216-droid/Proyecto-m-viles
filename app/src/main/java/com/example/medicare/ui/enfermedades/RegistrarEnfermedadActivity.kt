package com.example.medicare.ui.enfermedades

import android.app.DatePickerDialog
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
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.ui.theme.MediCareTheme
import java.util.Calendar

class RegistrarEnfermedadActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MedicareApp
        val viewModel = ViewModelProvider(
            this,
            EnfermedadViewModelFactory(app.database.enfermedadDao(), app.database.usuarioDao())
        )[EnfermedadViewModel::class.java]

        val enfermedadId = intent.getIntExtra("ENFERMEDAD_ID", -1)
        val idUsuario = intent.getIntExtra("ID_USUARIO", 1)

        setContent {
            MediCareTheme {
                var enfermedadAEditar by remember { mutableStateOf<Enfermedad?>(null) }
                val operacionExitosa by viewModel.operacionExitosa.collectAsState()

                LaunchedEffect(operacionExitosa) {
                    if (operacionExitosa) {
                        viewModel.resetEstado()
                        finish()
                    }
                }

                LaunchedEffect(enfermedadId) {
                    if (enfermedadId != -1) {
                        enfermedadAEditar = viewModel.obtenerEnfermedadPorId(enfermedadId)
                    }
                }

                RegistrarEnfermedadScreen(
                    enfermedadExistente = enfermedadAEditar,
                    onCancelar = { finish() },
                    onGuardar = { nombre, fecha, notas ->
                        if (enfermedadId == -1) {
                            // NUEVA ENFERMEDAD
                            viewModel.guardarEnfermedad(nombre, fecha, notas, idUsuario)
                        } else {
                            // ACTUALIZAR EXISTENTE (Aquí estaba el error)
                            val actualizada = Enfermedad(
                                idEnfermedad = enfermedadId,
                                nombreEnfermedad = nombre,
                                fecha = fecha,
                                notas = notas,
                                idUsuarioFk = idUsuario
                            )
                            viewModel.actualizarEnfermedad(actualizada, idUsuario)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RegistrarEnfermedadScreen(
    enfermedadExistente: Enfermedad?,
    onCancelar: () -> Unit,
    onGuardar: (String, String, String) -> Unit
) {
    val azul = Color(0xFF0086FF)
    val context = LocalContext.current
    val calendario = Calendar.getInstance()

    var nombre by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    LaunchedEffect(enfermedadExistente) {
        enfermedadExistente?.let {
            nombre = it.nombreEnfermedad
            fecha = it.fecha
            notas = it.notas
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF4AA3FF), Color(0xFF0086FF))
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 40.dp,
                            bottomEnd = 40.dp
                        )
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
                            text = if (enfermedadExistente == null)
                                "Registrar\nEnfermedad"
                            else
                                "Editar\nEnfermedad",
                            fontSize = 28.sp,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (enfermedadExistente == null)
                            "Agrega una nueva enfermedad"
                        else
                            "Modifica los datos guardados",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(start = 56.dp)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-40).dp)
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {

                    Label("Nombre de la enfermedad:")
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        placeholder = { Text("Ej: Hipertensión arterial") },
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

                    Label("Fecha de diagnóstico:")
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                DatePickerDialog(
                                    context,
                                    { _, anio, mes, dia ->
                                        fecha = "%02d/%02d/%04d".format(dia, mes + 1, anio)
                                    },
                                    calendario.get(Calendar.YEAR),
                                    calendario.get(Calendar.MONTH),
                                    calendario.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                    ) {
                        OutlinedTextField(
                            value = fecha,
                            onValueChange = { },
                            placeholder = { Text("dd/mm/aaaa") },
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

                    Label("Notas adicionales:")
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        placeholder = { Text("Ej: Síntomas") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            focusedBorderColor = azul,
                            unfocusedBorderColor = Color(0xFFCCCCCC)
                        )
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedButton(
                            onClick = onCancelar,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.LightGray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color.Black
                            )
                        ) {
                            Text(
                                text = "Cancelar",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Button(
                            onClick = {
                                if (nombre.isNotBlank()) onGuardar(nombre, fecha, notas)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = azul)
                        ) {
                            Text(
                                text = "Guardar",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Label(texto: String) {
    Text(
        text = texto,
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF0086FF),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}