package com.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.data.entities.Post
import com.example.data.entities.User
import com.example.data.pagingsource.ProfilePostsPagingSource
import com.example.repositories.impl.PostsImpl
import com.example.utils.Constants.PAGE_SIZE
import com.example.utils.Event
import com.example.utils.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val postImpl: PostsImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(postImpl,dispatcher) {
    private val _profileMeta = MutableLiveData<Event<Resource<User>>>()
    val profileMeta: LiveData<Event<Resource<User>>> = _profileMeta

    private val _followStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followStatus: LiveData<Event<Resource<Boolean>>> = _followStatus



    fun getPagingFlow(uid: String): Flow<PagingData<Post>> {
        val pagingSource = ProfilePostsPagingSource(
            FirebaseFirestore.getInstance(),
            uid
        )
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource
        }.flow.cachedIn(viewModelScope)
    }

    fun toggleFollowForUser(uid: String) {
        _followStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postImpl.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun loadProfile(uid: String) {
        _profileMeta.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postImpl.user(uid)
            _profileMeta.postValue(Event(result))
        }
    }
}