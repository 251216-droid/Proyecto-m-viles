package com.example.medicare.ui.enfermedades

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnfermedadesScreen(
    idUsuario: Int,
    viewModel: EnfermedadViewModel = viewModel(),
    onAgregarClick: () -> Unit,
    onVolver: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    val enfermedades by viewModel.enfermedades.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEnfermedades(idUsuario)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.fillMaxWidth().background(azul, RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)).padding(24.dp)) {
                Text("Enfermedades", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("${enfermedades.size} padecimientos", color = Color.White.copy(0.8f))
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregarClick, containerColor = azul, contentColor = Color.White, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(enfermedades) { enfermedad ->
                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(4.dp)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(12.dp).background(azul, CircleShape))
                        Spacer(Modifier.width(12.dp))
                        Text(enfermedad.nombreEnfermedad, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}
