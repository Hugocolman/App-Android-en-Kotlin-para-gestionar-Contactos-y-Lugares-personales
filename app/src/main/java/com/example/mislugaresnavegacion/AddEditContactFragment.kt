package com.example.mislugaresnavegacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.mislugaresnavegacion.databinding.FragmentAddEditContactBinding

class AddEditContactFragment : Fragment() {

    private var _binding: FragmentAddEditContactBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactViewModel: ContactViewModel
    private val args: AddEditContactFragmentArgs by navArgs()
    private var contactToEdit: Contact? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditContactBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getDatabase(requireContext()).contactDao()
        val factory = ContactViewModelFactory(dao)
        contactViewModel = ViewModelProvider(this, factory).get(ContactViewModel::class.java)

        contactToEdit = args.contactToEdit

        // Si estamos editando (contactToEdit no es nulo), rellenamos los campos.
        contactToEdit?.let { contact ->
            binding.nameEditText.setText(contact.name)
            binding.phoneEditText.setText(contact.phone)
            binding.favoriteSwitch.isChecked = contact.isFavorite
        }

        binding.saveButton.setOnClickListener {
            saveContact()
        }
    }

    private fun saveContact() {
        val name = binding.nameEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val isFavorite = binding.favoriteSwitch.isChecked

        if (name.isBlank() || phone.isBlank()) {
            Toast.makeText(requireContext(), "Nombre y teléfono no pueden estar vacíos", Toast.LENGTH_SHORT).show()
            return
        }

        if (contactToEdit == null) { // Creando un nuevo contacto
            val newContact = Contact(name = name, phone = phone, isFavorite = isFavorite)
            contactViewModel.insert(newContact)
            Toast.makeText(requireContext(), "Contacto guardado", Toast.LENGTH_SHORT).show()
        } else { // Actualizando un contacto existente
            val updatedContact = contactToEdit!!.copy(name = name, phone = phone, isFavorite = isFavorite)
            contactViewModel.update(updatedContact)
            Toast.makeText(requireContext(), "Contacto actualizado", Toast.LENGTH_SHORT).show()
        }

        // Regresar a la lista de contactos
        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}