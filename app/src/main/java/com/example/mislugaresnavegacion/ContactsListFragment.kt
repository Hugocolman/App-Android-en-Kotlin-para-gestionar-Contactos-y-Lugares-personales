package com.example.mislugaresnavegacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mislugaresnavegacion.databinding.FragmentContactsListBinding
import com.google.android.material.tabs.TabLayout
import com.example.mislugaresnavegacion.Contact
import com.example.mislugaresnavegacion.ContactAdapter
import com.example.mislugaresnavegacion.ContactViewModel

class ContactsListFragment : Fragment() {

    private var _binding: FragmentContactsListBinding? = null
    private val binding get() = _binding!!
    private lateinit var contactViewModel: ContactViewModel
    private lateinit var contactAdapter: ContactAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentContactsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dao = AppDatabase.getDatabase(requireContext()).contactDao()
        val factory = ContactViewModelFactory(dao)
        contactViewModel = ViewModelProvider(this, factory).get(ContactViewModel::class.java)

        setupRecyclerView()
        setupTabs()

        binding.fabAddContact.setOnClickListener {
            // Navegar a la pantalla de añadir, sin pasar ningún contacto.
            findNavController().navigate(R.id.action_contactsListFragment_to_addEditContactFragment)
        }

        observeContacts(isFavoritesOnly = false) // Cargar la lista inicial de "Todos"
    }

//    private fun setupRecyclerView() {
//        contactAdapter = ContactAdapter(
//            onItemClicked = { contact ->
//                // Navegar a la pantalla de edición, pasando el contacto seleccionado.
//                val action = ContactsListFragmentDirections.actionContactsListFragmentToAddEditContactFragment3(contact)
//                findNavController().navigate(action)
//            },
//            onFavoriteClicked = { contact ->
//                // Invertir el estado de favorito y actualizarlo en la base de datos.
//                val updatedContact = contact.copy(isFavorite = !contact.isFavorite)
//                contactViewModel.update(updatedContact)
//            }
//        )
//        binding.contactsRecyclerView.apply {
//            layoutManager = LinearLayoutManager(requireContext())
//            adapter = contactAdapter
//        }
//    }


    // Dentro de la clase ContactsListFragment, pero fuera de cualquier método
    private val handleContactClick: (Contact) -> Unit = { contact ->
        val action = ContactsListFragmentDirections.actionContactsListFragmentToAddEditContactFragment(contact)
        findNavController().navigate(action)
    }

    private val handleFavoriteToggle: (Contact) -> Unit = { contact ->
        val updatedContact = contact.copy(isFavorite = !contact.isFavorite)
        contactViewModel.update(updatedContact)
    }

    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(
//            onItemClicked = handleContactClick,
//            onFavoriteClicked = handleFavoriteToggle
        )
        // Asigna las lambdas a las propiedades del adaptador
        contactAdapter.onItemClicked = handleContactClick
        contactAdapter.onFavoriteClicked = handleFavoriteToggle

        binding.contactsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = contactAdapter
        }
    }


    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> observeContacts(isFavoritesOnly = false) // Pestaña "Todos"
                    1 -> observeContacts(isFavoritesOnly = true)  // Pestaña "Favoritos"
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun observeContacts(isFavoritesOnly: Boolean) {
        // Quitar observadores anteriores para evitar duplicados.
        contactViewModel.allContacts.removeObservers(viewLifecycleOwner)
        contactViewModel.favoriteContacts.removeObservers(viewLifecycleOwner)

        val liveDataToObserve = if (isFavoritesOnly) contactViewModel.favoriteContacts else contactViewModel.allContacts

        liveDataToObserve.observe(viewLifecycleOwner) { contacts ->
            contacts?.let {
                contactAdapter.submitList(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}