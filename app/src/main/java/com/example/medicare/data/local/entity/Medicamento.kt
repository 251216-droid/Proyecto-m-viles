package com.example.medicare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "medicamentos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["idUsuario"],
            childColumns = ["id_usuario_fk"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["id_usuario_fk"])]
)
data class Medicamento(
    @PrimaryKey(autoGenerate = true)
    val idMedicamento: Int = 0,

    @ColumnInfo(name = "id_usuario_fk")
    val idUsuarioFk: Int,

    @ColumnInfo(name = "nombre_medicamento")
    val nombreMedicamento: String,

    @ColumnInfo(name = "tipo_presentacion")
    val tipoPresentacion: String, // Ej: Pastilla, Jarabe

    @ColumnInfo(name = "dosis")
    val dosis: String, // Ej: 1 tableta, 500mg

    @ColumnInfo(name = "frecuencia")
    val frecuencia: String, // Ej: cada 8h

    @ColumnInfo(name = "primera_toma")
    val primeraToma: String, // Ej: 10:30 AM

    @ColumnInfo(name = "duracion")
    val duracion: String, // Ej: 7 dias

    @ColumnInfo(name = "contenido")
    val contenido: String, // Ej: Caja con 30 tabletas

    @ColumnInfo(name = "categoria")
    val categoria: String, // "Pediátrico", "Infantil", "Adulto"

    @ColumnInfo(name = "estado_medicamento")
    val estadoMedicamento: String // "Activo", "Suspendido", "Terminado"
)
