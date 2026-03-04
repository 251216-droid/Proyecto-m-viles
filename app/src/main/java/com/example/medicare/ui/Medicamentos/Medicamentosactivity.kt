package com.example.medicare.ui.medicamentos

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.MedicareApp
import com.example.medicare.R
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.ui.perfil.PerfilActivity
import com.example.medicare.ui.home.HomeActivity
import com.example.medicare.ui.enfermedades.EnfermedadesActivity
import com.example.medicare.ui.enfermedades.RegistrarEnfermedadActivity
import com.example.medicare.ui.theme.MediCareTheme

class MedicamentosActivity : ComponentActivity() {

    private lateinit var viewModel: MedicamentoViewModel
    private var idUsuarioRecibido: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        idUsuarioRecibido = intent.getIntExtra("ID_USUARIO", 1)

        val app = application as MedicareApp
        viewModel = ViewModelProvider(
            this,
            MedicamentoViewModelFactory(app.database.medicamentoDao(), app.database.usuarioDao())
        )[MedicamentoViewModel::class.java]

        viewModel.cargarUsuario(idUsuarioRecibido)

        setContent {
            MediCareTheme {
                val usuario by viewModel.usuario.collectAsState()
                val nombreUsuario = usuario?.nombre ?: "Usuario"
                val correoUsuario = usuario?.correo ?: ""

                MedicamentosScreen(
                    viewModel = viewModel,
                    nombreUsuario = nombreUsuario,
                    correoUsuario = correoUsuario,
                    idUsuario = idUsuarioRecibido,
                    onIrAHome = {
                        val intent = Intent(this, HomeActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }
                        startActivity(intent)
                        finish()
                    },
                    onIrAEnfermedades = {
                        val intent = Intent(this, EnfermedadesActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                        finish()
                    },
                    onAgregarEnfermedad = {
                        val intent = Intent(this, RegistrarEnfermedadActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onAgregarMedicamento = {
                        val intent = Intent(this, RegistrarMedicamentoActivity::class.java).apply {
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onEditarMedicamento = { id ->
                        val intent = Intent(this, RegistrarMedicamentoActivity::class.java).apply {
                            putExtra("MEDICAMENTO_ID", id)
                            putExtra("ID_USUARIO", idUsuarioRecibido)
                        }
                        startActivity(intent)
                    },
                    onRegresar = { finish() }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cargarMedicamentos(idUsuarioRecibido)
        viewModel.cargarUsuario(idUsuarioRecibido)
    }
}

@Composable
fun MedicamentosScreen(
    viewModel: MedicamentoViewModel,
    nombreUsuario: String,
    correoUsuario: String,
    idUsuario: Int,
    onIrAHome: () -> Unit,
    onIrAEnfermedades: () -> Unit,
    onAgregarEnfermedad: () -> Unit,
    onAgregarMedicamento: () -> Unit,
    onEditarMedicamento: (Int) -> Unit,
    onRegresar: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    val azulClaro = Color(0xFF66B2FF)
    val context = LocalContext.current

    var menuExpandido by remember { mutableStateOf(false) }
    val medicamentos by viewModel.medicamentos.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarMedicamentos(idUsuario)
    }

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
                        brush = Brush.verticalGradient(colors = listOf(azulClaro, azul)),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
                    .padding(top = 60.dp, bottom = 40.dp, start = 24.dp, end = 24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Medicamentos",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${medicamentos.size} Medicamentos registrados",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    // ── Avatar con iniciales ──
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(color = Color.White.copy(alpha = 0.3f), shape = CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {
                                val intent = Intent(context, PerfilActivity::class.java).apply {
                                    putExtra("ID_USUARIO", idUsuario)
                                }
                                context.startActivity(intent)
                            },
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

            // ── Lista o mensaje vacío ──
            if (medicamentos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.medicamento),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sin medicamentos registrados",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(medicamentos) { medicamento ->
                        MedicamentoCard(
                            medicamento = medicamento,
                            onEditar = { onEditarMedicamento(medicamento.idMedicamento) },
                            onEliminar = { viewModel.eliminarMedicamento(medicamento, idUsuario) }
                        )
                    }
                }
            }

            // ── Barra de navegación ──
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                val itemColors = NavigationBarItemDefaults.colors(indicatorColor = Color.White)

                NavigationBarItem(
                    selected = false,
                    onClick = onIrAHome,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.home),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Gray
                        )
                    },
                    label = { Text("Inicio", color = Color.Gray) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onIrAEnfermedades,
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.emfermedad),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Gray
                        )
                    },
                    label = { Text("Enfermedades", color = Color.Gray) },
                    colors = itemColors
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.medicamento),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = azul
                        )
                    },
                    label = { Text("Medicamentos", color = azul, fontWeight = FontWeight.Bold) },
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
                                onAgregarEnfermedad()
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
                                onAgregarMedicamento()
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
                painter = painterResource(R.drawable.agregar),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }
    }
}

@Composable
fun MedicamentoCard(
    medicamento: Medicamento,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    // Color del badge de estado
    val (estadoColor, estadoFondo) = when (medicamento.estadoMedicamento) {
        "Activo" -> Color(0xFF2E7D32) to Color(0xFFE8F5E9)
        "Suspendido" -> Color(0xFFE65100) to Color(0xFFFFF3E0)
        else -> Color(0xFF616161) to Color(0xFFF5F5F5) // Terminado
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            // ── Cabecera azul con nombre ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = medicamento.nombreMedicamento,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    // Badge de tipo
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF0086FF), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = medicamento.tipoPresentacion,
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Cuerpo de la card ──
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dosis: ${medicamento.dosis}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Categoría: ${medicamento.categoria}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        // Badge de estado
                        Box(
                            modifier = Modifier
                                .background(estadoFondo, RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = medicamento.estadoMedicamento,
                                fontSize = 12.sp,
                                color = estadoColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    Row {
                        IconButton(onClick = onEditar) {
                            Icon(
                                painter = painterResource(R.drawable.editar),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                        IconButton(onClick = onEliminar) {
                            Icon(
                                painter = painterResource(R.drawable.eliminar),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = Color.Unspecified
                            )
                        }
                    }
                }
            }
        }
    }
}