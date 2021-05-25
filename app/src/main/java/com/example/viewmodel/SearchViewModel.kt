package com.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entities.User
import com.example.repositories.impl.SearchUserImpl
import com.example.utils.Event
import com.example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchUserImpl: SearchUserImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _searchResults = MutableLiveData<Event<Resource<List<User>>>>()
    val searchResults: LiveData<Event<Resource<List<User>>>> = _searchResults

    fun searchUser(query: String) {
        if (query.isEmpty()) return

        _searchResults.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = searchUserImpl.search(query)
            _searchResults.postValue(Event(result))
        }
    }
}