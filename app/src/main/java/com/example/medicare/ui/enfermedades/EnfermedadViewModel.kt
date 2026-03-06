package com.example.medicare.ui.enfermedades

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Enfermedad
import com.example.medicare.data.local.entity.Usuario
import com.example.medicare.ui.network.RetrofitClient
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

    private val _operacionExitosa = MutableStateFlow(false)
    val operacionExitosa: StateFlow<Boolean> = _operacionExitosa

    fun cargarUsuario(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _usuario.value = usuarioDao?.obtenerUsuarioPorId(idUsuario)
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
            try {
                val nuevaEnfermedad = Enfermedad(
                    nombreEnfermedad = nombre,
                    fecha = fecha,
                    notas = notas,
                    idUsuarioFk = idUsuario
                )
                // 1. Guardar local y obtener el ID que Room le asignó
                val idGenerado = enfermedadDao.insertarEnfermedad(nuevaEnfermedad)

                // 2. Enviar a la API incluyendo el ID local
                val datosApi = mapOf(
                    "nombre" to nombre,
                    "fecha" to fecha,
                    "notas" to notas,
                    "id_usuario" to idUsuario.toString(),
                    "id_local" to idGenerado.toString() // Sincronizamos el ID
                )
                
                RetrofitClient.instance.registrarEnfermedad(datosApi)
                _operacionExitosa.value = true
                
            } catch (e: Exception) {
                _operacionExitosa.value = true
            }
        }
    }

    fun actualizarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                enfermedadDao.actualizarEnfermedad(enfermedad)

                val datosApi = mapOf(
                    "nombre" to enfermedad.nombreEnfermedad,
                    "fecha" to enfermedad.fecha,
                    "notas" to enfermedad.notas
                )
                
                RetrofitClient.instance.actualizarEnfermedad(enfermedad.idEnfermedad, datosApi)
                _operacionExitosa.value = true
                
            } catch (e: Exception) {
                _operacionExitosa.value = true
            }
        }
    }

    fun resetEstado() {
        _operacionExitosa.value = false
    }

    fun eliminarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            enfermedadDao.eliminarEnfermedad(enfermedad)
            try {
                RetrofitClient.instance.eliminarEnfermedad(enfermedad.idEnfermedad)
            } catch (e: Exception) { }
            cargarEnfermedades(idUsuario)
        }
    }
}