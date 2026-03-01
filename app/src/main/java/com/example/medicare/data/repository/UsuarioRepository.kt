package com.example.medicare.data.repository

import com.example.medicare.data.local.dao.UsuarioDao
import com.example.medicare.data.local.entity.Usuario

// El repositorio recibe el DAO en su constructor para poder usar sus consultas
class UsuarioRepository(private val usuarioDao: UsuarioDao) {

    // Llama a la función de insertar del DAO
    // Usamos 'suspend' para que esta tarea pesada se haga en segundo plano
    // y no congele la pantalla del celular mientras el usuario espera.
    // Esta tambien evita que la app se cierre por el error.
    suspend fun registrarUsuario(usuario: Usuario): Long {
        return usuarioDao.registrarUsuario(usuario)
    }

    // Llama a la función de búsqueda (SELECT) del DAO
    suspend fun iniciarSesion(correo: String, password: String): Usuario? {
        return usuarioDao.iniciarSesion(correo, password)
    }
}