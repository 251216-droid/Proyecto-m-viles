package com.example.medicare.ui.perfil

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.MedicareApp
import com.example.medicare.ui.auth.LoginActivity
import com.example.medicare.ui.theme.MediCareTheme
import android.content.Intent
import androidx.compose.ui.res.painterResource
import com.example.medicare.R

class PerfilActivity : ComponentActivity() {

    private lateinit var viewModel: PerfilViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MedicareApp
        viewModel = ViewModelProvider(
            this,
            PerfilViewModelFactory(app.database.usuarioDao())
        )[PerfilViewModel::class.java]

        val idUsuario = intent.getIntExtra("ID_USUARIO", 1)
        viewModel.cargarUsuario(idUsuario)

        setContent {
            MediCareTheme {
                PerfilScreen(
                    viewModel = viewModel,
                    idUsuario = idUsuario,
                    onCerrarSesion = {
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    },
                    onRegresar = { finish() }
                )
            }
        }
    }
}

@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    idUsuario: Int,
    onCerrarSesion: () -> Unit,
    onRegresar: () -> Unit
) {
    val azul = Color(0xFF0086FF)
    val azulClaro = Color(0xFF66B2FF)

    val usuario by viewModel.usuario.collectAsState()
    val actualizado by viewModel.actualizado.collectAsState()

    var modoEditar by remember { mutableStateOf(false) }
    var acercaExpandido by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }

    var nombreEdit by remember { mutableStateOf("") }
    var correoEdit by remember { mutableStateOf("") }
    var contrasenaEdit by remember { mutableStateOf("") }

    // Cuando carga el usuario, llenamos los campos
    LaunchedEffect(usuario) {
        usuario?.let {
            nombreEdit = it.nombre
            correoEdit = it.correo
            contrasenaEdit = it.contrasena
        }
    }

    // Cuando se actualiza, salimos del modo editar
    LaunchedEffect(actualizado) {
        if (actualizado) modoEditar = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .verticalScroll(rememberScrollState())
    ) {

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
                .padding(top = 40.dp, bottom = 40.dp, start = 16.dp, end = 24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                
                // ── Botón Regresar ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = onRegresar) {
                        Icon(
                            painter = painterResource(id = R.drawable.atras),
                            contentDescription = "Regresar",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Unspecified
                        )
                    }
                }

                // ── Avatar con iniciales ──
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(color = Color.White.copy(alpha = 0.3f), shape = CircleShape)
                        .border(2.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val iniciales = usuario?.nombre
                        ?.split(" ")
                        ?.filter { it.isNotEmpty() }
                        ?.take(2)
                        ?.mapNotNull { it.firstOrNull()?.uppercaseChar() }
                        ?.joinToString("") ?: "?"

                    Text(
                        text = iniciales,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = usuario?.nombre ?: "Cargando...",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = usuario?.correo ?: "",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Sección Mi información ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mi información",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    OutlinedButton(
                        onClick = { modoEditar = !modoEditar },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, azul),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = azul),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = if (modoEditar) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (modoEditar) "Cancelar" else "Editar",
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (modoEditar) {

                    // ── Modo edición ──
                    Text(
                        text = "Nombre completo",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = nombreEdit,
                        onValueChange = { nombreEdit = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azul,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Correo electrónico",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = correoEdit,
                        onValueChange = { correoEdit = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azul,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Contraseña",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    OutlinedTextField(
                        value = contrasenaEdit,
                        onValueChange = { contrasenaEdit = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (passwordVisible) R.drawable.invisible
                                        else R.drawable.ojo
                                    ),
                                    contentDescription = null,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = azul,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = {
                            viewModel.actualizarUsuario(
                                idUsuario,
                                nombreEdit,
                                correoEdit,
                                contrasenaEdit
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = azul)
                    ) {
                        Text(
                            text = "Guardar cambios",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                } else {

                    // ── Modo visualización ──
                    InfoItem(
                        icono = Icons.Default.Person,
                        etiqueta = "Nombre completo",
                        valor = usuario?.nombre ?: ""
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )

                    InfoItem(
                        icono = Icons.Default.Email,
                        etiqueta = "Correo electrónico",
                        valor = usuario?.correo ?: ""
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = Color(0xFFF0F0F0)
                    )

                    InfoItem(
                        icono = Icons.Default.Lock,
                        etiqueta = "Contraseña",
                        valor = "••••••••"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Sección Acerca de MediCare ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Acerca de MediCare",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF333333)
                    )
                    IconButton(onClick = { acercaExpandido = !acercaExpandido }) {
                        Icon(
                            imageVector = if (acercaExpandido)
                                Icons.Default.KeyboardArrowUp
                            else
                                Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = azul
                        )
                    }
                }

                if (acercaExpandido) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "MediCare es una aplicación diseñada para ayudarte a gestionar tus medicamentos y enfermedades de manera organizada. Mantén un registro completo de tus tratamientos y recibe recordatorios.",
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        lineHeight = 22.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = azul,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "soporte@medicare.com",
                            fontSize = 13.sp,
                            color = azul
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── Botón Cerrar Sesión ──
        Button(
            onClick = onCerrarSesion,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFFEBEB),
                contentColor = Color(0xFFE53935)
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun InfoItem(
    icono: ImageVector,
    etiqueta: String,
    valor: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFE3F2FD), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = Color(0xFF0086FF),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = valor,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF333333)
            )
        }
    }
}