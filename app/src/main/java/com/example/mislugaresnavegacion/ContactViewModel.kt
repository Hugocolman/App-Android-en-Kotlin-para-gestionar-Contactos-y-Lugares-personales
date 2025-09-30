package com.example.mislugaresnavegacion

import androidx.lifecycle.*
import kotlinx.coroutines.launch

class ContactViewModel(private val dao: ContactDao) : ViewModel() {

    val allContacts: LiveData<List<Contact>> = dao.getAllContacts()
    val favoriteContacts: LiveData<List<Contact>> = dao.getFavoriteContacts()

    fun insert(contact: Contact) = viewModelScope.launch {
        dao.insert(contact)
    }

    fun update(contact: Contact) = viewModelScope.launch {
        dao.update(contact)
    }

    fun delete(contact: Contact) = viewModelScope.launch {
        dao.delete(contact)
    }
}

// La Factory que le enseña al sistema cómo crear nuestro ViewModel
class ContactViewModelFactory(private val dao: ContactDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContactViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ContactViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}