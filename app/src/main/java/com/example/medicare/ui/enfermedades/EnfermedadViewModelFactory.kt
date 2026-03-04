package com.example.medicare.ui.enfermedades

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicare.data.local.dao.EnfermedadDao
import com.example.medicare.data.local.dao.UsuarioDao

class EnfermedadViewModelFactory(
    private val enfermedadDao: EnfermedadDao,
    private val usuarioDao: UsuarioDao? = null
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EnfermedadViewModel::class.java)) {
            return EnfermedadViewModel(enfermedadDao, usuarioDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}