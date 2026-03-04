package com.example.medicare.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.data.local.entity.Usuario
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class EnfermedadViewModel(
    private val enfermedadDao: EnfermedadDao,
    private val usuarioDao: UsuarioDao? = null
) : ViewModel() {

    private val _enfermedades = MutableStateFlow<List<Enfermedad>>(emptyList())
    val enfermedades: StateFlow<List<Enfermedad>> = _enfermedades

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun cargarUsuario(idUsuario: Int) {
        usuarioDao?.let { dao ->
            viewModelScope.launch(Dispatchers.IO) {
                _usuario.value = dao.obtenerUsuarioPorId(idUsuario)
            }
        }
    }

    fun cargarEnfermedades(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _enfermedades.value = enfermedadDao.obtenerEnfermedades(idUsuario)
        }
    }

    suspend fun obtenerEnfermedadPorId(id: Int): Enfermedad? {
        return withContext(Dispatchers.IO) {
            enfermedadDao.obtenerEnfermedadPorId(id)
        }
    }

    fun guardarEnfermedad(nombre: String, fecha: String, notas: String, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val nuevaEnfermedad = Enfermedad(
                nombreEnfermedad = nombre,
                fecha = fecha,
                notas = notas,
                idUsuarioFk = idUsuario
            )
            enfermedadDao.insertarEnfermedad(nuevaEnfermedad)
            cargarEnfermedades(idUsuario) // Recargar después de guardar
        }
    }

    fun actualizarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            enfermedadDao.actualizarEnfermedad(enfermedad)
            cargarEnfermedades(idUsuario) // Recargar después de actualizar
        }
    }

    fun eliminarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            enfermedadDao.eliminarEnfermedad(enfermedad)
            cargarEnfermedades(idUsuario) // Recargar después de eliminar
        }
    }
}