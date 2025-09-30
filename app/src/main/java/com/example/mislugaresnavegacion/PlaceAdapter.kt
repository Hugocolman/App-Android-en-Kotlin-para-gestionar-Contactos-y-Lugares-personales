package com.example.mislugaresnavegacion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mislugaresnavegacion.databinding.ItemPlaceBinding // Importa la clase de enlace generada

// El adaptador ahora toma una función lambda para manejar los clics en los ítems (opcional)
class PlaceAdapter(private val onItemClicked: (Place) -> Unit) :
    ListAdapter<Place, PlaceAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        // Infla el layout del ítem usando ViewBinding
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        val currentPlace = getItem(position)
        holder.bind(currentPlace)
        // Configura el OnClickListener para el ítem completo
        holder.itemView.setOnClickListener {
            onItemClicked(currentPlace)
        }
    }

    // ViewHolder para mantener las vistas de cada ítem
    class PlaceViewHolder(private val binding: ItemPlaceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(place: Place) {
            // Asigna los datos del objeto Place a las vistas usando ViewBinding
            binding.placeNameTextView.text = place.name
            binding.coordinatesTextView.text =
                "Lat: ${"%.4f".format(place.latitude)}, Lon: ${"%.4f".format(place.longitude)}"

            // Aquí podrías establecer un icono diferente basado en el tipo de lugar, si tuvieras esa lógica.
            // Por ahora, el icono se establece en item_place.xml (android:src="@drawable/ic_place")
            // binding.imageViewPlaceIcon.setImageResource(R.drawable.ic_custom_place_icon)
        }
    }

    // DiffUtil.ItemCallback para calcular las diferencias entre listas de forma eficiente
    class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            // Comprueba si los ítems representan el mismo objeto (generalmente por ID)
            return oldItem.id == newItem.id // Asume que tu entidad Place tiene un campo 'id'
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            // Comprueba si los datos del ítem son los mismos (todos los campos relevantes)
            return oldItem == newItem // Asume que Place es una data class o has implementado equals()
        }
    }
}
