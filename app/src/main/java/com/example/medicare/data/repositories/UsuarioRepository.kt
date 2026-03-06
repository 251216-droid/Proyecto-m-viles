package com.example.medicare.data.repositories

import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Usuario

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrarUsuario(usuario)
    }

    suspend fun iniciarSesion(correo: String, contrasena: String): Usuario? {
        return usuarioDao.iniciarSesion(correo, contrasena)
    }

    suspend fun obtenerUsuarioPorId(id: Int): Usuario? {
        return usuarioDao.obtenerUsuarioPorId(id)
    }

    suspend fun actualizarUsuario(usuario: Usuario) {
        usuarioDao.actualizarUsuario(usuario)
    }
}
