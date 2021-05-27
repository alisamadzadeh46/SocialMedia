package com.example.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.example.socialmedia.R
import com.example.ui.adapters.PostAdapter
import com.example.ui.adapters.SearchAdapter
import com.example.ui.dialogs.DeletePostDialog
import com.example.ui.dialogs.LikedByDialog
import com.example.ui.snackBar
import com.example.utils.EventObserver
import com.example.viewmodel.BasePostViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

abstract class BasePostFragment(
    layoutId: Int
) : Fragment(layoutId) {
    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostAdapter
    protected abstract val basePostViewModel: BasePostViewModel
    private var currentLikedIndex: Int? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        postAdapter.setOnLikeClickListener { post, i ->
            currentLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggleLikeForPost(post)
        }
        postAdapter.setOnDeletePostClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }
        postAdapter.setOnLikedByClickListener { post ->
            basePostViewModel.users(post.likedBy)

        }
        postAdapter.setOnCommentsClickListener {
            findNavController().navigate(
                R.id.actionToCommentDialog,
                Bundle().apply {
                    putString("postId", it.id)
                }
            )
        }
    }

    private fun subscribeToObservers() {
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                currentLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                snackBar(it)
            },
            onLoading = {
                currentLikedIndex?.let { index ->
                    postAdapter.peek(index)?.isLiking = true
                    postAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            currentLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.peek(index)?.apply {
                    this.isLiked = isLiked
                    isLiking = false
                    if(isLiked) {
                        likedBy += uid
                    } else {
                        likedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(index)
            }
        })
        basePostViewModel.likedByUsers.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { users ->
            val searchAdapter = SearchAdapter(glide)
            searchAdapter.users = users
            LikedByDialog(searchAdapter).show(childFragmentManager, null)
        })

    }
}