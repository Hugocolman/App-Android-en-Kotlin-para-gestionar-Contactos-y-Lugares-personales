package com.example.mislugaresnavegacion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

// @Entity le dice a Room que esta clase representa una tabla en la base de datos.
@Entity(tableName = "contact_table")
// 'data class' es una clase de Kotlin optimizada para contener datos.
// 'Serializable' nos permitirá pasar el objeto Contact completo entre fragments más adelante.
data class Contact(
    // @PrimaryKey define la clave primaria de la tabla. 'autoGenerate = true' hace que Room genere un ID único para cada nuevo contacto.
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // @ColumnInfo permite especificar un nombre diferente para la columna en la BD.
    @ColumnInfo(name = "contact_name")
    val name: String,

    @ColumnInfo(name = "contact_phone")
    val phone: String,

    // Campo para gestionar si un contacto es favorito. Por defecto, no lo es.
    @ColumnInfo(name = "is_favorite")
    val isFavorite: Boolean = false
) : Serializable