package com.tecsup.gymtrackerpro.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.tecsup.gymtrackerpro.data.local.entity.Rutina
import kotlinx.coroutines.flow.Flow

@Dao
interface RutinaDao {

    @Insert
    suspend fun insertar(rutina: Rutina): Long

    @Update
    suspend fun actualizar(rutina: Rutina)

    @Delete
    suspend fun eliminar(rutina: Rutina)

    @Query("SELECT * FROM Rutina WHERE usuarioId = :usuarioId ORDER BY fecha DESC")
    fun listarPorUsuario(usuarioId: Int): Flow<List<Rutina>>

    @Query("SELECT * FROM Rutina WHERE id = :id LIMIT 1")
    suspend fun buscarPorId(id: Int): Rutina?
}