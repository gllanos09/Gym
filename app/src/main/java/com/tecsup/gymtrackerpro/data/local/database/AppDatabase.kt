package com.tecsup.gymtrackerpro.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tecsup.gymtrackerpro.data.local.dao.RutinaDao
import com.tecsup.gymtrackerpro.data.local.dao.UsuarioDao
import com.tecsup.gymtrackerpro.data.local.entity.Rutina
import com.tecsup.gymtrackerpro.data.local.entity.Usuario

@Database(entities = [Usuario::class, Rutina::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun usuarioDao(): UsuarioDao
    abstract fun rutinaDao(): RutinaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gymtracker_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}