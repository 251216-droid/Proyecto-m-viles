package com.example.medicare.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.MedicareApp
import com.example.medicare.R
import com.example.medicare.data.local.dao.HistorialConNombre
import com.example.medicare.data.local.dao.ProximaTomaConInfo
import com.example.medicare.ui.enfermedades.EnfermedadesActivity
import com.example.medicare.ui.enfermedades.RegistrarEnfermedadActivity
import com.example.medicare.ui.medicamentos.MedicamentosActivity
import com.example.medicare.ui.medicamentos.RegistrarMedicamentoActivity
import com.example.medicare.ui.perfil.PerfilActivity
import com.example.medicare.ui.theme.MediCareTheme

class HomeActivity : ComponentActivity() {

    private lateinit var viewModel: HomeViewModel
    private var idUsuarioRecibido: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MedicareApp
        viewModel = ViewModelProvider(
            this,
            HomeViewModelFactory(app.database.usuarioDao(), app.database.historialTomaDao())
        )[HomeViewModel::class.java]

        idUsuarioRecibido = intent.getIntExtra("ID_USUARIO", 1)
        viewModel.cargarDatos(idUsuarioRecibido)

        setContent {
            MediCareTheme {
                val usuario by viewModel.usuario.collectAsState()
                val proximaToma by viewModel.proximaToma.collectAsState()
                val historial by viewModel.historial.collectAsState()
                
                HomeScreen(
                    nombreUsuario = usuario?.nombre ?: "Usuario",
                    correoUsuario = usuario?.correo ?: "",
                    idUsuario = idUsuarioRecibido,
                    proximaToma = proximaToma,
                    historial = historial,
                    onConfirmarToma = { idToma -> viewModel.confirmarToma(idToma, idUsuarioRecibido) },
                    onIrAHome = { },
                    onIrAEnfermedades = {
                        val intent = Intent(this, EnfermedadesActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onIrAMedicamentos = {
                        val intent = Intent(this, MedicamentosActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onIrARegistrarEnfermedad = {
                        val intent = Intent(this, RegistrarEnfermedadActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onIrARegistrarMedicamento = {
                        val intent = Intent(this, RegistrarMedicamentoActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onIrAPerfil = {
                        val intent = Intent(this, PerfilActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarDatos(idUsuarioRecibido)
    }
}

@Composable
fun HomeScreen(
    nombreUsuario: String,
    correoUsuario: String,
    idUsuario: Int,
    proximaToma: ProximaTomaConInfo?,
    historial: List<HistorialConNombre>,
    onConfirmarToma: (Int) -> Unit,
    onIrAHome: () -> Unit,
    onIrAEnfermedades: () -> Unit,
    onIrAMedicamentos: () -> Unit,
    onIrARegistrarEnfermedad: () -> Unit,
    onIrARegistrarMedicamento: () -> Unit,
    onIrAPerfil: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    var menuExpandido by remember { mutableStateOf(false) }

    // Calcular iniciales
    val iniciales = nombreUsuario
        .split(" ")
        .filter { it.isNotEmpty() }
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Encabezado azul ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF66B2FF), Color(0xFF0086FF))
                        ),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(vertical = 40.dp, horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "¡Hola, $nombreUsuario!",
                            fontSize = 29.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Tu Salud, Primero.",
                            fontSize = 18.sp,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    // ── Avatar con iniciales ──
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = CircleShape
                            )
                            .border(2.dp, Color.White, CircleShape)
                            .clickable { onIrAPerfil() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = iniciales,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .weight(1f)
            ) {

                // ── Tarjeta próximas dosis ──
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Color(0xFFE3F2FD),
                                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                                )
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "Próxima dosis",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = azul
                            )
                        }
                        Column(modifier = Modifier.padding(20.dp)) {
                            if (proximaToma != null) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = proximaToma.nombre_med, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "Tipo: ${proximaToma.tipo_presentacion}", fontSize = 15.sp, color = Color.Gray)
                                        Text(text = "A las ${proximaToma.fecha_hora_programada}", fontSize = 15.sp, color = Color.Gray)
                                    }
                                    Text(text = proximaToma.dosis, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = azul)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Button(
                                        onClick = { onConfirmarToma(proximaToma.idToma) },
                                        modifier = Modifier.weight(1f).height(45.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFE8F5E9),
                                            contentColor = Color(0xFF2E7D32)
                                        )
                                    ) {
                                        Text(text = "Confirmar", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    Button(
                                        onClick = { },
                                        modifier = Modifier.weight(1f).height(45.dp),
                                        shape = RoundedCornerShape(20.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFFF7DADA),
                                            contentColor = Color(0xFFEF4444)
                                        )
                                    ) {
                                        Text(text = "Posponer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }
                            } else {
                                Text(
                                    text = "No hay tomas pendientes",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Historial ──
                Text(text = "Historial", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = azul)
                Spacer(modifier = Modifier.height(8.dp))

                if (historial.isEmpty()) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(text = "Tu historial de tomas aparecerá aquí", color = Color.Gray)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(historial) { item ->
                            val cardColor = when (item.estado) {
                                "Tomado" -> Color(0xFFE8F5E9) // Verde suave
                                "Omitido" -> Color(0xFFF7DADA) // Rojo suave
                                else -> Color.White
                            }
                            val iconColor = when (item.estado) {
                                "Tomado" -> Color(0xFF2E7D32)
                                "Omitido" -> Color(0xFFEF4444)
                                else -> azul
                            }

                            Card(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                shape = RoundedCornerShape(16.dp),
                                elevation = CardDefaults.cardElevation(4.dp),
                                colors = CardDefaults.cardColors(containerColor = cardColor)
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = when (item.estado) {
                                            "Tomado" -> painterResource(id = R.drawable.exitoso)
                                            "Pendiente" -> painterResource(id = R.drawable.medicamento)
                                            else -> painterResource(id = R.drawable.no_exitoso)
                                        },
                                        contentDescription = null,
                                        modifier = Modifier.size(26.dp),
                                        tint = if (item.estado == "Pendiente") azul else Color.Unspecified
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = item.nombre_med, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = if (item.estado != "Pendiente") iconColor else Color.Black)
                                        Text(text = "${item.estado} - ${item.fecha_hora_programada}", fontSize = 13.sp, color = if (item.estado != "Pendiente") iconColor.copy(alpha = 0.7f) else Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ── Barra de navegación ──
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = azul,
                    selectedTextColor = azul,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )
                NavigationBarItem(
                    selected = true,
                    onClick = onIrAHome,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "Inicio",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = { Text("Inicio", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onIrAEnfermedades,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.emfermedad),
                            contentDescription = "Enfermedades",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = { Text("Enfermedades", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onIrAMedicamentos,
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.medicamento),
                            contentDescription = "Medicamentos",
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    label = { Text("Medicamentos", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
            }
        }

        // ── Menú expandido ──
        AnimatedVisibility(
            visible = menuExpandido,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 90.dp)
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.width(220.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrARegistrarEnfermedad()
                                menuExpandido = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.emfermedad),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Agregar Enfermedad", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onIrARegistrarMedicamento()
                                menuExpandido = false
                            }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.medicamento),
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "Agregar Medicamento", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }

        // ── Botón + flotante ──
        FloatingActionButton(
            onClick = { menuExpandido = !menuExpandido },
            containerColor = azul,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 100.dp),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = R.drawable.agregar),
                contentDescription = "Agregar",
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}