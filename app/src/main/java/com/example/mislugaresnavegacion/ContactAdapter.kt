package com.example.mislugaresnavegacion

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mislugaresnavegacion.databinding.ItemContactBinding

class ContactAdapter : ListAdapter<Contact, ContactAdapter.ContactViewHolder>(ContactDiffCallback()) {
    // Propiedades para las lambdas de clic, con implementaciones predeterminadas vacías
    var onItemClicked: (Contact) -> Unit = { Log.w("ContactAdapter", "onItemClicked not set") }
    var onFavoriteClicked: (Contact) -> Unit = { Log.w("ContactAdapter", "onFavoriteClicked not set") }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pasamos las referencias a las funciones del adaptador al ViewHolder
        return ContactViewHolder(binding, { contact -> onItemClicked(contact) }, { contact -> onFavoriteClicked(contact) })
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val currentContact = getItem(position)
        holder.bind(currentContact)
        // El OnClickListener del itemView ahora usa la propiedad del adaptador
        // Esto se puede quitar si el ViewHolder ya maneja el clic en todo el ítem
        // O si quieres que el clic en el ítem completo sea manejado aquí además del botón de favorito
        // Por consistencia con cómo estaba antes, lo ponemos en el ViewHolder
    }

    class ContactViewHolder(
        private val binding: ItemContactBinding, // Esta es la clase de enlace para item_contact.xml
        private val performItemClick: (Contact) -> Unit,
        private val performFavoriteClick: (Contact) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(contact: Contact) {
            // CORREGIDO: Usar los IDs correctos de item_contact.xml y los campos de Contact.kt
            binding.nameTextView.text = contact.name // Asume que ItemContactBinding tiene 'nameTextView'
            binding.phoneTextView.text = contact.phone ?: "No phone" // Asume que ItemContactBinding tiene 'phoneTextView'

            // CORREGIDO: Actualizar el ImageView para el estado de favorito
            val favoriteIconRes = if (contact.isFavorite) {
                R.drawable.ic_star_filled // Necesita res/drawable/ic_favorite_filled
            } else {
                R.drawable.ic_star_outline // Necesita res/drawable/ic_favorite_border
            }
            binding.favoriteIcon.setImageResource(favoriteIconRes) // Asume que ItemContactBinding tiene 'favoriteIcon'

            // Configura el OnClickListener para el ImageView de favorito
            binding.favoriteIcon.setOnClickListener { // El ImageView puede tener un OnClickListener
                performFavoriteClick(contact)
            }

            // Configura el OnClickListener para el ítem completo
            itemView.setOnClickListener {
                performItemClick(contact)
            }
        }
    }

    class ContactDiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }
}