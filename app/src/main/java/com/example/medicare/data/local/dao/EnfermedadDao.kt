package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.medicare.data.local.entity.Enfermedad

@Dao
interface EnfermedadDao {

    @Insert
    suspend fun insertarEnfermedad(enfermedad: Enfermedad)

    @Update
    suspend fun actualizarEnfermedad(enfermedad: Enfermedad)

    @Query("SELECT * FROM enfermedades WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerEnfermedades(idUsuario: Int): List<Enfermedad>

    @Query("SELECT * FROM enfermedades WHERE idEnfermedad = :id")
    suspend fun obtenerEnfermedadPorId(id: Int): Enfermedad?

    @Delete
    suspend fun eliminarEnfermedad(enfermedad: Enfermedad)
}
