package com.example.medicare.ui.enfermedades

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
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
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.ui.theme.MediCareTheme

class EnfermedadesActivity : ComponentActivity() {

    private lateinit var viewModel: EnfermedadViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MedicareApp
        viewModel = ViewModelProvider(
            this,
            EnfermedadViewModelFactory(app.database.enfermedadDao())
        )[EnfermedadViewModel::class.java]

        setContent {
            MediCareTheme {
                EnfermedadesScreen(
                    viewModel = viewModel,
                    onAgregarEnfermedad = {
                        startActivity(Intent(this, RegistrarEnfermedadActivity::class.java))
                    },
                    onEditarEnfermedad = { id ->
                        val intent = Intent(this, RegistrarEnfermedadActivity::class.java).apply {
                            putExtra("ENFERMEDAD_ID", id)
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
        viewModel.cargarEnfermedades(1)
    }
}

@Composable
fun EnfermedadesScreen(
    viewModel: EnfermedadViewModel,
    onAgregarEnfermedad: () -> Unit,
    onEditarEnfermedad: (Int) -> Unit,
    onRegresar: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    val azulClaro = Color(0xFF66B2FF)

    var menuExpandido by remember { mutableStateOf(false) }
    val enfermedades by viewModel.listaEnfermedades.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarEnfermedades(1)
    }

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
                            colors = listOf(azulClaro, azul)
                        ),
                        shape = RoundedCornerShape(
                            bottomStart = 40.dp,
                            bottomEnd = 40.dp
                        )
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
                            text = "Enfermedades",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = "${enfermedades.size} Enfermedades registradas",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Lista o mensaje vacío ──
            if (enfermedades.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.emfermedad),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            tint = Color.Unspecified
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Sin enfermedades registradas",
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
                    items(enfermedades) { enfermedad ->
                        EnfermedadCard(
                            enfermedad = enfermedad,
                            onEditar = { onEditarEnfermedad(enfermedad.idEnfermedad) },
                            onEliminar = { viewModel.eliminarEnfermedad(enfermedad, 1) }
                        )
                    }
                }
            }

            // ── Barra de navegación ──
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                val itemColors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.White
                )

                NavigationBarItem(
                    selected = false,
                    onClick = onRegresar,
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
                    selected = true,
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.emfermedad),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = azul
                        )
                    },
                    label = {
                        Text(
                            text = "Enfermedades",
                            color = azul,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = itemColors
                )

                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = {
                        Icon(
                            painter = painterResource(R.drawable.medicamento),
                            contentDescription = null,
                            modifier = Modifier.size(28.dp),
                            tint = Color.Gray
                        )
                    },
                    label = { Text("Medicamentos", color = Color.Gray) },
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
                        Text("Agregar Enfermedad")
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { menuExpandido = false }
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
                        Text("Agregar Medicamento")
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
fun EnfermedadCard(
    enfermedad: Enfermedad,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD))
                    .padding(12.dp)
            ) {
                Text(
                    text = enfermedad.nombreEnfermedad,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fecha: ${enfermedad.fecha}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Notas: ${enfermedad.notas}",
                            fontSize = 14.sp,
                            color = Color.DarkGray
                        )
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