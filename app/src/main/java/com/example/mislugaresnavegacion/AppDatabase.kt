package com.example.mislugaresnavegacion
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 1. Añade 'Place::class' a la lista de entidades.
// 2. Incrementa el número de versión a 2 porque hemos cambiado la estructura de la base de datos.
@Database(entities = [Contact::class, Place::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao
    abstract fun placeDao(): PlaceDao // 3. Añade el método abstracto para el nuevo DAO.

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration() // 4. Añade esto para manejar el cambio de versión. En una app real, se haría una migración controlada.
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}