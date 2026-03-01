package com.example.medicare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

// El ViewModel recibe el Repositorio para poder pedirle los datos
class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {

    // LiveData para que la pantalla observe si el login fue exitoso
    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado: LiveData<Usuario?> = _usuarioLogueado

    // LiveData para mostrar mensajes de error (ej. "Contraseña incorrecta")
    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> = _mensajeError

    // Función que será llamada desde tu pantalla de Login cuando presionen el botón
    fun iniciarSesion(correo: String, contrasena: String) {
        // Validación rápida
        if (correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Llena todos los campos"
            return
        }

        // Validación formato de correo
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _mensajeError.value = "Ingresa un correo válido"
            return
        }

        // viewModelScope.launch ejecuta esto en un hilo secundario para no congelar la app
        viewModelScope.launch {
            val usuario = repository.iniciarSesion(correo, contrasena)
            if (usuario != null) {
                _usuarioLogueado.value = usuario // ¡Login exitoso!
            } else {
                _mensajeError.value = "Correo o contraseña incorrectos" // Falló
            }
        }
    }

    // Función que será llamada desde tu pantalla de Registro
    fun registrarUsuario(nombre: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Llenar todos los campos"
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            _mensajeError.value = "Ingresa un correo válido"
            return
        }

        //contraseña mínimo 6 caracteres
        if (contrasena.length < 6) {
            _mensajeError.value = "La contraseña debe tener al menos 6 caracteres"
            return
        }
        // nombre mínimo 3 caracteres
        if (nombre.length < 3) {
            _mensajeError.value = "El nombre debe tener al menos 3 caracteres"
            return
        }

        viewModelScope.launch {

            // Validación si el correo ya existe
            val usuarioExistente = repository.iniciarSesion(correo, contrasena)
            if (usuarioExistente != null) {
                _mensajeError.value = "Este correo ya está registrado"
                return@launch
            }
            // Creamos el objeto Usuario (el ID se genera solo porque le pusimos autoGenerate = true)
            val nuevoUsuario = Usuario(nombre = nombre, correo = correo, contrasena = contrasena)
            // Le decimos al repositorio que lo guarde en Room
            val idGenerado = repository.registrarUsuario(nuevoUsuario)

            if (idGenerado > 0) {
                // Si el ID es mayor a 0, se guardó bien. Hacemos auto-login.
                _usuarioLogueado.value = nuevoUsuario
            } else {
                _mensajeError.value = "Error al guardar en la base de datos"
            }
        }
    }
}