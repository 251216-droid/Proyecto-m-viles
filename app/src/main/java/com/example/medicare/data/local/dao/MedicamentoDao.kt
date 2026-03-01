package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.medicare.data.local.entity.Medicamento

@Dao
interface MedicamentoDao {
    @Insert
    suspend fun insertarMedicamento(medicamento: Medicamento): Long

    @Query("SELECT * FROM medicamentos WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerMedicamentosPorUsuario(idUsuario: Int): List<Medicamento>

    @Delete
    suspend fun eliminarMedicamento(medicamento: Medicamento)
}