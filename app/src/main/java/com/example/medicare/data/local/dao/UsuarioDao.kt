package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicare.data.local.entity.Usuario // Importamos la entidad que acabas de llenar

@Dao
interface UsuarioDao {

    @Insert
    suspend fun registrarUsuario(usuario: Usuario): Long

    @Query("SELECT * FROM usuarios WHERE correo = :email AND password = :password LIMIT 1")
    suspend fun iniciarSesion(email: String, password: String): Usuario?
}