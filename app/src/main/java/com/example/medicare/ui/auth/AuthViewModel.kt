package com.example.medicare.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.data.repository.UsuarioRepository
import com.example.medicare.ui.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: UsuarioRepository) : ViewModel() {

    private val _usuarioLogueado = MutableLiveData<Usuario?>()
    val usuarioLogueado: LiveData<Usuario?> = _usuarioLogueado

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> = _mensajeError

    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Llena todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val credenciales = mapOf("correo" to correo, "pass" to contrasena)
                val response = RetrofitClient.instance.login(credenciales)

                if (response.isSuccessful) {
                    val body = response.body()
                    val userMap = body?.get("usuario") as? Map<String, Any>
                    
                    // 1. Buscar en DB local
                    var usuarioLocal = repository.iniciarSesion(correo, contrasena)

                    // 2. Si no existe localmente, crearlo con los datos del servidor
                    if (usuarioLocal == null) {
                        val nombreSrv = userMap?.get("nombre") as? String ?: "Usuario MediCare"
                        val nuevo = Usuario(nombre = nombreSrv, correo = correo, contrasena = contrasena)
                        val idGenerado = repository.registrarUsuario(nuevo)
                        usuarioLocal = nuevo.copy(idUsuario = idGenerado.toInt())
                    }

                    // 3. Emitir el usuario con su ID real de Room
                    _usuarioLogueado.value = usuarioLocal
                } else {
                    _mensajeError.value = "Correo o contraseña incorrectos"
                }
            } catch (e: Exception) {
                val usuarioLocal = repository.iniciarSesion(correo, contrasena)
                if (usuarioLocal != null) {
                    _usuarioLogueado.value = usuarioLocal
                } else {
                    _mensajeError.value = "Error de conexión con el servidor"
                }
            }
        }
    }

    fun registrarUsuario(nombre: String, correo: String, contrasena: String) {
        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
            _mensajeError.value = "Llenar todos los campos"
            return
        }

        viewModelScope.launch {
            try {
                val datosApi = mapOf("nombre" to nombre, "correo" to correo, "pass" to contrasena)
                val response = RetrofitClient.instance.registrarUsuario(datosApi)

                if (response.isSuccessful) {
                    val nuevoUsuario = Usuario(nombre = nombre, correo = correo, contrasena = contrasena)
                    val idGenerado = repository.registrarUsuario(nuevoUsuario)
                    _usuarioLogueado.value = nuevoUsuario.copy(idUsuario = idGenerado.toInt())
                } else {
                    _mensajeError.value = "El correo ya está registrado"
                }
            } catch (e: Exception) {
                _mensajeError.value = "Error de red: Revisa tu servidor"
            }
        }
    }

    fun resetUsuarioLogueado() {
        _usuarioLogueado.value = null
    }
}