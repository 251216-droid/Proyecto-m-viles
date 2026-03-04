package com.example.medicare.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.medicare.data.local.entity.HistorialToma

@Dao
interface HistorialTomaDao {
    @Insert
    suspend fun registrarToma(historialToma: HistorialToma)

    @Query("UPDATE historial_tomas SET estado = :nuevoEstado, fecha_hora_real = :fechaReal WHERE idToma = :idToma")
    suspend fun actualizarEstadoToma(idToma: Int, nuevoEstado: String, fechaReal: String)

    @Query("""
        SELECT ht.*, m.nombre_medicamento as nombre_med 
        FROM historial_tomas ht
        JOIN programacion_horarios p ON ht.id_programacion_fk = p.idProgramacion
        JOIN medicamentos m ON p.id_medicamento_fk = m.idMedicamento
        WHERE m.id_usuario_fk = :idUsuario
        ORDER BY ht.fecha_hora_programada DESC
    """)
    suspend fun obtenerHistorialPorUsuario(idUsuario: Int): List<HistorialConNombre>

    @Query("""
        SELECT ht.*, m.nombre_medicamento as nombre_med, m.dosis, m.tipo_presentacion
        FROM historial_tomas ht
        JOIN programacion_horarios p ON ht.id_programacion_fk = p.idProgramacion
        JOIN medicamentos m ON p.id_medicamento_fk = m.idMedicamento
        WHERE m.id_usuario_fk = :idUsuario AND ht.estado = 'Pendiente'
        ORDER BY ht.fecha_hora_programada ASC
        LIMIT 1
    """)
    suspend fun obtenerProximaToma(idUsuario: Int): ProximaTomaConInfo?
}

data class HistorialConNombre(
    val idToma: Int,
    val id_programacion_fk: Int,
    val fecha_hora_programada: String,
    val fecha_hora_real: String?,
    val estado: String,
    val nombre_med: String
)

data class ProximaTomaConInfo(
    val idToma: Int,
    val fecha_hora_programada: String,
    val nombre_med: String,
    val dosis: String,
    val tipo_presentacion: String
)