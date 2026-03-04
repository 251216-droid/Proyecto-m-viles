package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.medicare.data.local.entity.Medicamento

@Dao
interface MedicamentoDao {

    @Insert
    suspend fun insertarMedicamento(medicamento: Medicamento): Long

    @Update
    suspend fun actualizarMedicamento(medicamento: Medicamento)

    @Delete
    suspend fun eliminarMedicamento(medicamento: Medicamento)

    @Query("SELECT * FROM medicamentos WHERE id_usuario_fk = :idUsuario")
    suspend fun obtenerMedicamentosPorUsuario(idUsuario: Int): List<Medicamento>

    @Query("SELECT * FROM medicamentos WHERE idMedicamento = :id")
    suspend fun obtenerMedicamentoPorId(id: Int): Medicamento?
}