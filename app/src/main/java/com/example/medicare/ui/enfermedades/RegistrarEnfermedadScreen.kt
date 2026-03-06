package com.example.medicare.ui.enfermedades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistrarEnfermedadScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel = viewModel(),
    onVolver: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    val azul = Color(0xFF0086FF)
    val operacionExitosa by viewModel.operacionExitosa.collectAsState()

    LaunchedEffect(operacionExitosa) {
        if (operacionExitosa) {
            viewModel.resetEstado()
            onVolver()
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(azul)) {
        Card(modifier = Modifier.fillMaxSize().padding(top = 80.dp), shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Nombre de la enfermedad", color = azul, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Diabetes") }
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        if (nombre.isNotBlank()) {
                            viewModel.guardarEnfermedad(nombre, "", "", idUsuario)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = azul)
                ) {
                    Text("Guardar Enfermedad", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
