package com.example.medicare.ui.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("registro")
    suspend fun registrarUsuario(@Body datos: Map<String, String>): Response<Map<String, Any>>

    @POST("login")
    suspend fun login(@Body credenciales: Map<String, String>): Response<Map<String, Any>>

    @POST("usuarios/update") 
    suspend fun actualizarPerfil(@Body datos: Map<String, String>): Response<Map<String, Any>>

    // --- ENFERMEDADES ---
    @POST("enfermedades")
    suspend fun registrarEnfermedad(@Body enfermedad: Map<String, String>): Response<Map<String, Any>>

    @GET("enfermedades")
    suspend fun obtenerEnfermedades(): Response<List<Map<String, Any>>>

    @PUT("enfermedades/{id}")
    suspend fun actualizarEnfermedad(@Path("id") id: Int, @Body enfermedad: Map<String, String>): Response<Map<String, Any>>

    @DELETE("enfermedades/{id}")
    suspend fun eliminarEnfermedad(@Path("id") id: Int): Response<Map<String, Any>>
}