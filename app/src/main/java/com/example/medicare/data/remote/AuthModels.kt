package com.example.medicare.data.remote

import com.google.gson.annotations.SerializedName

data class RegistroRequest(
    @SerializedName("nombre_completo") val nombreCompleto: String,
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)

data class AuthResponse(
    @SerializedName("mensaje") val mensaje: String,
    @SerializedName("token") val token: String?,
    @SerializedName("idUsuario") val idUsuario: Int?
)

data class LoginRequest(
    @SerializedName("correo") val correo: String,
    @SerializedName("password") val contrasena: String
)