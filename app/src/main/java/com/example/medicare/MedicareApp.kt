package com.example.medicare

import android.app.Application
import com.example.medicare.data.local.database.AppDatabase
import com.example.medicare.data.repository.UsuarioRepository

class MedicareApp : Application() {
    // Esto crea la base de datos de forma "perezosa"
    val database by lazy { AppDatabase.getDatabase(this) }

    // Esto crea el repositorio usando el DAO de la base de datos
    val usuarioRepository by lazy { UsuarioRepository(database.usuarioDao()) }
}