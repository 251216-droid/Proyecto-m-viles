package com.example.medicare.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.entity.Enfermedad
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnfermedadViewModel(private val enfermedadDao: EnfermedadDao) : ViewModel() {

    private val _listaEnfermedades = MutableStateFlow<List<Enfermedad>>(emptyList())
    val listaEnfermedades: StateFlow<List<Enfermedad>> = _listaEnfermedades

    fun cargarEnfermedades(idUsuario: Int) {
        viewModelScope.launch {
            _listaEnfermedades.value = enfermedadDao.obtenerEnfermedades(idUsuario)
        }
    }

    fun guardarEnfermedad(nombre: String, fecha: String, notas: String, idUsuario: Int) {
        viewModelScope.launch {
            val nueva = Enfermedad(
                nombreEnfermedad = nombre,
                fecha = fecha,
                notas = notas,
                idUsuarioFk = idUsuario
            )
            enfermedadDao.insertarEnfermedad(nueva)
            cargarEnfermedades(idUsuario)
        }
    }

    fun actualizarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch {
            enfermedadDao.actualizarEnfermedad(enfermedad)
            cargarEnfermedades(idUsuario)
        }
    }

    fun eliminarEnfermedad(enfermedad: Enfermedad, idUsuario: Int) {
        viewModelScope.launch {
            enfermedadDao.eliminarEnfermedad(enfermedad)
            cargarEnfermedades(idUsuario)
        }
    }
    
    // Función para obtener una enfermedad por ID (útil para editar)
    suspend fun obtenerEnfermedadPorId(id: Int): Enfermedad? {
        // Podríamos agregar una consulta en el DAO para esto, 
        // o buscarla en la lista actual si ya está cargada.
        return enfermedadDao.obtenerEnfermedadPorId(id)
    }
}