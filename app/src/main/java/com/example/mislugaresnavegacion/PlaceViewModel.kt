package com.example.mislugaresnavegacion

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// El ViewModel necesita una referencia a la Application para obtener la instancia de la BD
class PlaceViewModel(application: Application) : AndroidViewModel(application) {

    private val placeDao: PlaceDao
    val allPlaces: LiveData<List<Place>>

    init {
        // Obtener la instancia del DAO desde la base de datos
        // Asegúrate de que tu AppDatabase.kt tenga un método getDatabase(context)
        // y un método abstracto placeDao()
        val database = AppDatabase.getDatabase(application)
        placeDao = database.placeDao()
        allPlaces = placeDao.getAllPlaces() // Asume que getAllPlaces() devuelve LiveData> en tu PlaceDao
    }

    /**
     * Lanza una nueva corutina para insertar un lugar de forma no bloqueante.
     */
    fun insert(place: Place) = viewModelScope.launch(Dispatchers.IO) {
        placeDao.insert(place) // Asume que insert(place: Place) es un suspend fun en PlaceDao
    }

    // Puedes añadir más funciones para update, delete, etc., si es necesario.
}