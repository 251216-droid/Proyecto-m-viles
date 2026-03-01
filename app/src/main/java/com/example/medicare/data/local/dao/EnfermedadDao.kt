package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.medicare.data.local.entity.Enfermedad

@Dao
interface EnfermedadDao {

    // Guardar una nueva enfermedad
    @Insert
    suspend fun insertarEnfermedad(enfermedad: Enfermedad)

    // Traer todas las enfermedades de un usuario específico
    @Query("SELECT * FROM enfermedades WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerEnfermedades(idUsuario: Int): List<Enfermedad>

    // Eliminar una enfermedad si el usuario se equivocó
    @Delete
    suspend fun eliminarEnfermedad(enfermedad: Enfermedad)
}