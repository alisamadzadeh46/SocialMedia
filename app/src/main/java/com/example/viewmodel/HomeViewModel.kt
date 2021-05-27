package com.example.viewmodel


import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.example.data.pagingsource.FollowPostsPagingSource
import com.example.repositories.impl.PostsImpl
import com.example.utils.Constants.PAGE_SIZE
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val postImpl: PostsImpl,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(postImpl,dispatcher) {
    val pagingFlow = Pager(PagingConfig(PAGE_SIZE)) {
        FollowPostsPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)
}