package com.example.medicare.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "enfermedades",
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
data class Enfermedad(
    @PrimaryKey(autoGenerate = true)
    val idEnfermedad: Int = 0,

    @ColumnInfo(name = "id_usuario_fk")
    val idUsuarioFk: Int,

    @ColumnInfo(name = "nombre_enfermedad")
    val nombreEnfermedad: String
)