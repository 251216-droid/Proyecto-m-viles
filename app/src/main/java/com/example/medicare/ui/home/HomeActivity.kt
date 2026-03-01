package com.example.medicare.ui.home

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
import com.example.medicare.R
import com.example.medicare.ui.theme.MediCareTheme

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // nombre del usuario desde el Login o Registro
        val nombreRecibido = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"

        setContent {
            MediCareTheme {
                HomeScreen(
                    nombreUsuario = nombreRecibido,
                    onIrAEnfermedades = { }, // vista de enfermedades(no esta )
                    onIrAMedicamentos = { }  //vista de medicamentos(no esta)
                )
            }
        }
    }
}

data class HistorialItem(val nombre: String, val fecha: String, val completado: Boolean)

@Composable
fun HomeScreen(
    nombreUsuario: String,
    onIrAEnfermedades: () -> Unit,
    onIrAMedicamentos: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    var menuExpandido by remember { mutableStateOf(false) }

    val historial = listOf(
        HistorialItem("Paracetamol", "Viernes, 20 de enero", true),
        HistorialItem("Ibuprofeno", "Viernes, 20 de enero", false),
        HistorialItem("Omeprazol", "Viernes, 20 de enero", true),
        HistorialItem("Paracetamol", "Jueves, 19 de enero", true)
    )

    // Contenedor pantalla de home
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            //Muestra saludo y degradado azul
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
                        Text(text = "¡Hola, $nombreUsuario!", fontSize = 29.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(text = "Tu Salud, Primero.", fontSize = 18.sp, color = Color.White.copy(alpha = 0.9f))
                    }
                    //perfil falta
                    IconButton(onClick = { /* Acción de perfil */ }) {
                        Icon(imageVector = Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


            Column(modifier = Modifier.padding(horizontal = 24.dp).weight(1f)) {
                
                // TARJETA PRÓXIMAS DOSIS
                Card(
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column {
                        // Título
                        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFFE3F2FD), shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)).padding(12.dp)) {
                            Text(text = "Próximas dosis", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = azul)
                        }
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Column {
                                    Text(text = "Paracetamol", fontSize = 19.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Tipo: Tableta", fontSize = 15.sp, color = Color.Gray)
                                    Text(text = "A las 11:30 A.M", fontSize = 15.sp, color = Color.Gray)
                                }
                                Text(text = "500mg", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = azul)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            // Botón posponer
                            Button(
                                onClick = { },
                                modifier = Modifier.fillMaxWidth().height(45.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF7DADA), contentColor = Color(0xFFEF4444))
                            ) {
                                Text(text = "Posponer", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // HISTORIAL
                Text(text = "Historial", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = azul)
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(historial) { item ->
                        // Tarjeta individual para cada registro
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                // Icono de estado
                                Icon(
                                    painter = if (item.completado) painterResource(id = R.drawable.exitoso) else painterResource(id = R.drawable.no_exitoso),
                                    contentDescription = null,
                                    modifier = Modifier.size(26.dp),
                                    tint = Color.Unspecified
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(text = item.nombre, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                    Text(text = item.fecha, fontSize = 13.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }
            //Navegación entre las vistas inferior
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 0.dp
            ) {
                val itemColors = NavigationBarItemDefaults.colors(
                    selectedIconColor = azul,
                    selectedTextColor = azul,
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color.White
                )

                // Botón de Inicio
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(painter = painterResource(id = R.drawable.home), contentDescription = "Inicio", modifier = Modifier.size(28.dp)) },
                    label = { Text("Inicio", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
                // Botón de Enfermedades
                NavigationBarItem(
                    selected = false,
                    onClick = onIrAEnfermedades,
                    icon = { Icon(painter = painterResource(id = R.drawable.emfermedad), contentDescription = "Enfermedades", modifier = Modifier.size(28.dp)) },
                    label = { Text("Enfermedades", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
                // Botón de Medicamentos
                NavigationBarItem(
                    selected = false,
                    onClick = onIrAMedicamentos,
                    icon = { Icon(painter = painterResource(id = R.drawable.medicamento), contentDescription = "Medicamentos", modifier = Modifier.size(28.dp)) },
                    label = { Text("Medicamentos", fontWeight = FontWeight.Bold) },
                    colors = itemColors
                )
            }
        }

        // MENÚ
        AnimatedVisibility(visible = menuExpandido, modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 90.dp)) {
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.width(220.dp)) {
                    // agregar enfermedad
                    Row(modifier = Modifier.fillMaxWidth().clickable { onIrAEnfermedades(); menuExpandido = false }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.emfermedad), contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified)
                        Spacer(modifier = Modifier.width(12.dp)); Text(text = "Agregar Enfermedad", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                    HorizontalDivider(color = Color(0xFFE0E0E0))
                    // agregar medicamento
                    Row(modifier = Modifier.fillMaxWidth().clickable { onIrAMedicamentos(); menuExpandido = false }.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(id = R.drawable.medicamento), contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified)
                        Spacer(modifier = Modifier.width(12.dp)); Text(text = "Agregar Medicamento", fontSize = 15.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        
        // Boton Abre/Cierra el menú
        FloatingActionButton(
            onClick = { menuExpandido = !menuExpandido },
            containerColor = azul,
            modifier = Modifier.align(Alignment.BottomEnd).padding(end = 16.dp, bottom = 100.dp),
            shape = CircleShape
        ) {
            Icon(painter = painterResource(id = R.drawable.agregar), contentDescription = "Agregar", modifier = Modifier.size(28.dp), tint = Color.White)
        }
    }
}
