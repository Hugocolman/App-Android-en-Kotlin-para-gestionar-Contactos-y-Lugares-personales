package com.example.mislugaresnavegacion

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mislugaresnavegacion.databinding.FragmentPlacesListBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class PlacesListFragment : Fragment() {

    private var _binding: FragmentPlacesListBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double? = null
    private var currentLongitude: Double? = null

    private lateinit var placeViewModel: PlaceViewModel
    private lateinit var placeAdapter: PlaceAdapter

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("PermissionGrant", "Permission granted by user.")
                getCurrentLocation()
            } else {
                Log.d("PermissionGrant", "Permission denied by user.")
                Toast.makeText(requireContext(), "Permiso de ubicación denegado.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPlacesListBinding.inflate(inflater, container, false)
        Log.d("PlacesListFragment", "onCreateView called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("PlacesListFragment", "onViewCreated called")

        // Inicializar ViewModel
        placeViewModel = ViewModelProvider(this).get(PlaceViewModel::class.java)
        Log.d("PlacesListFragment", "PlaceViewModel initialized")

        // Inicializar FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        Log.d("PlacesListFragment", "FusedLocationProviderClient initialized")

        // Configurar el Adapter y RecyclerView
        placeAdapter = PlaceAdapter { place ->
            // Acción al hacer clic en un ítem de la lista
            Toast.makeText(requireContext(), "Lugar clickeado: ${place.name}", Toast.LENGTH_SHORT).show()
            Log.i("PlaceClick", "Clicked on place: ${place.name}, ID: ${place.id}")
            // Aquí podrías navegar a un fragmento de detalles, pasar el ID del lugar, etc.
            // Ejemplo:
            // val action = PlacesListFragmentDirections.actionPlacesListFragmentToPlaceDetailFragment(place.id)
            // findNavController().navigate(action)
        }
        binding.placesRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placeAdapter
        }
        Log.d("PlacesListFragment", "RecyclerView configured with adapter and layout manager")

        // Observar los datos de lugares desde el ViewModel
        placeViewModel.allPlaces.observe(viewLifecycleOwner) { places ->
            places?.let {
                Log.d("PlacesListFragment", "Observed ${it.size} places from ViewModel.")
                placeAdapter.submitList(it)
            }
        }

        binding.getLocationButton.setOnClickListener {
            Log.d("GetLocationButton", "Button clicked.")
            checkLocationPermission()
        }

        binding.savePlaceButton.setOnClickListener {
            Log.d("SavePlaceButton", "Button clicked.")
            val placeName = binding.placeNameEditText.text.toString()

            if (placeName.isNotBlank() && currentLatitude != null && currentLongitude != null) {
                val newPlace = Place(
                    name = placeName,
                    latitude = currentLatitude!!,
                    longitude = currentLongitude!!
                    // 'id' se autogenerará por Room, así que no es necesario establecerlo aquí.
                )
                placeViewModel.insert(newPlace)
                Log.i("SavePlace", "Attempting to save place: $placeName, Lat: $currentLatitude, Lon: $currentLongitude")
                Toast.makeText(requireContext(), "'$placeName' guardado.", Toast.LENGTH_LONG).show()

                // Limpiar campos después de guardar
                binding.placeNameEditText.text.clear()
                binding.currentLocationTextView.text = "" // O un texto predeterminado como "Lat: ---, Lon: ---"
                currentLatitude = null
                currentLongitude = null
                Log.d("SavePlace", "Input fields cleared after saving.")

            } else if (placeName.isBlank()){
                binding.placeNameEditText.error = "El nombre no puede estar vacío"
                Toast.makeText(requireContext(), "Por favor, ingresa un nombre para el lugar.", Toast.LENGTH_SHORT).show()
                Log.w("SavePlace", "Save attempt failed: Place name is blank.")
            } else { // currentLatitude o currentLongitude es null
                Toast.makeText(requireContext(), "Por favor, primero obtén la ubicación actual.", Toast.LENGTH_SHORT).show()
                Log.w("SavePlace", "Save attempt failed: Location not obtained.")
            }
        }
    }

    private fun isGPSEnabled(): Boolean {
        val locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        val isEnabled = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) ?: false
        Log.d("GPSEnabledCheck", "GPS Enabled: $isEnabled")
        return isEnabled
    }

    private fun checkLocationPermission() {
        Log.d("LocationPermission", "Checking location permission.")
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.d("LocationPermission", "Permission already granted.")
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Log.d("LocationPermission", "Showing permission rationale dialog.")
                AlertDialog.Builder(requireContext())
                    .setTitle("Permiso de Ubicación Necesario")
                    .setMessage("Esta aplicación necesita el permiso de ubicación para obtener tu posición actual y guardar lugares. Por favor, concede el permiso.")
                    .setPositiveButton("OK") { _, _ ->
                        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                        Toast.makeText(requireContext(), "Permiso de ubicación denegado.", Toast.LENGTH_SHORT).show()
                    }
                    .create()
                    .show()
            }
            else -> {
                Log.d("LocationPermission", "Requesting permission.")
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission") // Los permisos se verifican en checkLocationPermission
    private fun getCurrentLocation() {
        Log.d("GetCurrentLocation", "Attempting to get current location.")
        if (!isGPSEnabled()) {
            Toast.makeText(requireContext(), "No se pudo obtener la ubicación. Activa el GPS.", Toast.LENGTH_LONG).show()
            Log.w("GetCurrentLocation", "GPS is not enabled.")
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    binding.currentLocationTextView.text = "Lat: ${"%.4f".format(currentLatitude)}, Lon: ${"%.4f".format(currentLongitude)}"
                    Log.i("GetCurrentLocation", "Last known location: Lat $currentLatitude, Lon $currentLongitude")
                } else {
                    Log.d("GetCurrentLocation", "Last known location is null. Requesting new location data.")
                    requestNewLocationData()
                }
            }
            .addOnFailureListener { e ->
                Log.e("GetCurrentLocation", "Error getting last known location.", e)
                Toast.makeText(requireContext(), "Error al obtener la última ubicación.", Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("MissingPermission") // Los permisos se verifican en checkLocationPermission
    private fun requestNewLocationData() {
        Log.d("RequestNewLocation", "Requesting new location data.")
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L) // Intervalo de 10 segundos
            .setMinUpdateIntervalMillis(5000L) // Intervalo mínimo de 5 segundos
            .setMaxUpdates(1) // Solo necesitamos una actualización
            .build()

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { location ->
                    currentLatitude = location.latitude
                    currentLongitude = location.longitude
                    binding.currentLocationTextView.text = "Lat: ${"%.4f".format(currentLatitude)}, Lon: ${"%.4f".format(currentLongitude)}"
                    Log.i("RequestNewLocation", "New location obtained: Lat $currentLatitude, Lon $currentLongitude")
                } ?: run {
                    Log.w("RequestNewLocation", "New location data is null.")
                    Toast.makeText(requireContext(), "No se pudo obtener una nueva ubicación.", Toast.LENGTH_SHORT).show()
                }
            }
        }, Looper.getMainLooper()) // Es importante especificar el Looper
            .addOnFailureListener { e ->
                Log.e("RequestNewLocation", "Error requesting location updates.", e)
                Toast.makeText(requireContext(), "Fallo al solicitar ubicación. Servicios de ubicación podrían estar desactivados.", Toast.LENGTH_LONG).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        Log.d("PlacesListFragment", "onDestroyView called, _binding set to null")
    }
}