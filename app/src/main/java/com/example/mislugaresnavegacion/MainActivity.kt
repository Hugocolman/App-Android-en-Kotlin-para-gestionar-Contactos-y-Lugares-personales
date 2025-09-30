package com.example.mislugaresnavegacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mislugaresnavegacion.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Establecemos nuestra Toolbar como la barra de acción principal.
        setSupportActionBar(binding.toolbar)

        // --- Inicio del bloque de diagnóstico ---
        // Intentamos encontrar el NavHostFragment usando el supportFragmentManager y el ID esperado.
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
        val navController: androidx.navigation.NavController // Declarar navController aquí

        if (navHostFragment == null) {
            android.util.Log.e("NAV_DEBUG", "Error Crítico: NavHostFragment con ID R.id.nav_host_fragment NO fue encontrado por supportFragmentManager.")
            throw IllegalStateException("NavHostFragment no encontrado. Verifica el ID en activity_main.xml y asegúrate de que el FragmentContainerView esté presente y visible.")
        } else {
            android.util.Log.d("NAV_DEBUG", "NavHostFragment encontrado. Clase: " + navHostFragment.javaClass.name)
            if (navHostFragment !is androidx.navigation.fragment.NavHostFragment) {
                android.util.Log.e("NAV_DEBUG", "Error Crítico: El fragmento con ID R.id.nav_host_fragment NO es una instancia de NavHostFragment. Es: " + navHostFragment.javaClass.name)
                throw IllegalStateException("El fragmento con ID R.id.nav_host_fragment no es un NavHostFragment. Verifica el atributo android:name en tu FragmentContainerView en activity_main.xml.")
            } else {
                android.util.Log.d("NAV_DEBUG", "Comprobación exitosa: El fragmento es una instancia de NavHostFragment.")
                // Obtenemos el NavController directamente de la instancia de NavHostFragment.
                navController = (navHostFragment as androidx.navigation.fragment.NavHostFragment).navController
                android.util.Log.d("NAV_DEBUG", "NavController obtenido directamente desde la instancia de NavHostFragment.")
            }
        }
        // --- Fin del bloque de diagnóstico ---
        // 3. Configuramos la barra superior para que muestre los títulos del nav graph y el botón de "atrás".
        // Le decimos cuáles son los destinos de nivel superior para no mostrar la flecha de "atrás" en ellos.
        val appBarConfiguration = AppBarConfiguration(setOf(R.id.placesListFragment, R.id.contactsListFragment))
        setupActionBarWithNavController(navController, appBarConfiguration)

        // 4. ¡La magia! Conectamos nuestro menú inferior con el NavController.
        // Esta línea hace que al presionar un ítem del menú, se navegue al fragment correspondiente.
        binding.bottomNavigation.setupWithNavController(navController)
    }
}