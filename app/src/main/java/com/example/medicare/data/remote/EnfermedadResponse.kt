package com.example.medicare.data.remote

import com.google.gson.annotations.SerializedName

data class EnfermedadResponse(
    @SerializedName("idEnfermedad") val idEnfermedad: Int,
    @SerializedName("id_usuario_fk") val idUsuario: Int,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String
)

data class EnfermedadRequest(
    @SerializedName("id_usuario_fk") val idUsuarioFk: Int,
    @SerializedName("nombre_enfermedad") val nombreEnfermedad: String
)

data class MensajeResponse(val mensaje: String)