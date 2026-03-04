package com.example.medicare.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilViewModel(private val usuarioDao: UsuarioDao) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _actualizado = MutableStateFlow(false)
    val actualizado: StateFlow<Boolean> = _actualizado

    fun cargarUsuario(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _usuario.value = usuarioDao.obtenerUsuarioPorId(idUsuario)
        }
    }

    fun actualizarUsuario(id: Int, nombre: String, correo: String, contrasena: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val actualizado = Usuario(
                idUsuario = id,
                nombre = nombre,
                correo = correo,
                contrasena = contrasena
            )
            usuarioDao.actualizarUsuario(actualizado)
            _usuario.value = actualizado
            _actualizado.value = true
        }
    }
}

class PerfilViewModelFactory(private val usuarioDao: UsuarioDao) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) {
            return PerfilViewModel(usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}