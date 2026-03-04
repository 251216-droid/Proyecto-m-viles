package com.example.medicare.ui.medicamentos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.medicare.data.local.dao.MedicamentoDao
import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Medicamento
import com.example.medicare.data.local.entity.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MedicamentoViewModel(
    private val medicamentoDao: MedicamentoDao,
    private val usuarioDao: UsuarioDao? = null
) : ViewModel() {

    private val _medicamentos = MutableStateFlow<List<Medicamento>>(emptyList())
    val medicamentos: StateFlow<List<Medicamento>> = _medicamentos

    private val _usuario = MutableStateFlow<Usuario?>(null)
    val usuario: StateFlow<Usuario?> = _usuario

    fun cargarUsuario(idUsuario: Int) {
        usuarioDao?.let { dao ->
            viewModelScope.launch(Dispatchers.IO) {
                _usuario.value = dao.obtenerUsuarioPorId(idUsuario)
            }
        }
    }

    fun cargarMedicamentos(idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _medicamentos.value = medicamentoDao.obtenerMedicamentosPorUsuario(idUsuario)
        }
    }

    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento? {
        return withContext(Dispatchers.IO) {
            medicamentoDao.obtenerMedicamentoPorId(id)
        }
    }

    fun guardarMedicamento(
        nombre: String,
        tipo: String,
        dosis: String,
        frecuencia: String,
        primeraToma: String,
        duracion: String,
        contenido: String,
        categoria: String,
        estado: String,
        idUsuario: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val nuevo = Medicamento(
                idUsuarioFk = idUsuario,
                nombreMedicamento = nombre,
                tipoPresentacion = tipo,
                dosis = dosis,
                frecuencia = frecuencia,
                primeraToma = primeraToma,
                duracion = duracion,
                contenido = contenido,
                categoria = categoria,
                estadoMedicamento = estado
            )
            medicamentoDao.insertarMedicamento(nuevo)
            cargarMedicamentos(idUsuario)
        }
    }

    fun actualizarMedicamento(medicamento: Medicamento, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            medicamentoDao.actualizarMedicamento(medicamento)
            cargarMedicamentos(idUsuario)
        }
    }

    fun eliminarMedicamento(medicamento: Medicamento, idUsuario: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            medicamentoDao.eliminarMedicamento(medicamento)
            cargarMedicamentos(idUsuario)
        }
    }
}

class MedicamentoViewModelFactory(
    private val medicamentoDao: MedicamentoDao,
    private val usuarioDao: UsuarioDao? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicamentoViewModel::class.java)) {
            return MedicamentoViewModel(medicamentoDao, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}