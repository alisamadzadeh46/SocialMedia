package com.example.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.entities.Comment
import com.example.repositories.impl.PostsImpl
import com.example.utils.Event
import com.example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val postsImpl: PostsImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val createCommentStatus: LiveData<Event<Resource<Comment>>> = _createCommentStatus

    private val _deleteCommentStatus = MutableLiveData<Event<Resource<Comment>>>()
    val deleteCommentStatus: LiveData<Event<Resource<Comment>>> = _deleteCommentStatus

    private val _commentsForPost = MutableLiveData<Event<Resource<List<Comment>>>>()
    val commentsForPost: LiveData<Event<Resource<List<Comment>>>> = _commentsForPost

    fun createComment(commentText: String, postId: String) {
        _createCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postsImpl.createComment(commentText, postId)
            _createCommentStatus.postValue(Event(result))
        }
    }

    fun deleteComment(comment: Comment) {
        _deleteCommentStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postsImpl.deleteComment(comment)
            _deleteCommentStatus.postValue(Event(result))
        }
    }

    fun getCommentsForPost(postId: String) {
        _commentsForPost.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = postsImpl.getCommentForPost(postId)
            _commentsForPost.postValue(Event(result))
        }
    }
}