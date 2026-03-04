package com.example.medicare.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.dao.HistorialTomaDao
import com.example.medicare.data.local.dao.HistorialConNombre
import com.example.medicare.data.local.dao.ProximaTomaConInfo
import com.example.medicare.data.local.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(
    private val usuarioDao: UsuarioDao,
    private val historialTomaDao: HistorialTomaDao
) : ViewModel() {

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    private val _proximaToma = MutableStateFlow<ProximaTomaConInfo?>(null)
    val proximaToma: StateFlow<ProximaTomaConInfo?> = _proximaToma

    private val _historial = MutableStateFlow<List<HistorialConNombre>>(emptyList())
    val historial: StateFlow<List<HistorialConNombre>> = _historial

    fun cargarDatos(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _usuario.value = usuarioDao.obtenerUsuarioPorId(idUsuario)
            _proximaToma.value = historialTomaDao.obtenerProximaToma(idUsuario)
            _historial.value = historialTomaDao.obtenerHistorialPorUsuario(idUsuario)
        }
    }

    fun confirmarToma(idToma: Int, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val fechaActual = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
            historialTomaDao.actualizarEstadoToma(idToma, "Tomado", fechaActual)
            cargarDatos(idUsuario) // Recargar para actualizar vista
        }
    }
}

class HomeViewModelFactory(
    private val usuarioDao: UsuarioDao,
    private val historialTomaDao: HistorialTomaDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(usuarioDao, historialTomaDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}