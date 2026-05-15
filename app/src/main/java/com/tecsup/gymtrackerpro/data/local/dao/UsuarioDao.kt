package com.tecsup.gymtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tecsup.gymtrackerpro.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Insert
    suspend fun insertar(usuario: Usuario): Long

    @Query("SELECT * FROM Usuario WHERE email = :email AND password = :password LIMIT 1")
    suspend fun buscarPorCredenciales(email: String, password: String): Usuario?

    @Query("SELECT * FROM Usuario WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Usuario?

    @Query("SELECT * FROM Usuario WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): Usuario?

    @Query("SELECT COUNT(*) FROM Rutina WHERE usuarioId = :id")
    suspend fun contarRutinas(id: Int): Int

    @Query("SELECT SUM(pesoKg * series * repeticiones) FROM Rutina WHERE usuarioId = :id")
    suspend fun calcularVolumenTotal(id: Int): Double?
}