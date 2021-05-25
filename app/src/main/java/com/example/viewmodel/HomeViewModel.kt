package com.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.data.entities.Post
import com.example.repositories.impl.PostsImpl
import com.example.utils.Event
import com.example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postImpl: PostsImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(postImpl,dispatcher) {
    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()

    override val posts: LiveData<Event<Resource<List<Post>>>>
        get() = _posts

    init {
        getPosts()
    }

    override fun getPosts(uid: String) {
        _posts.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postImpl.post()
            _posts.postValue(Event(result))
        }
    }
}