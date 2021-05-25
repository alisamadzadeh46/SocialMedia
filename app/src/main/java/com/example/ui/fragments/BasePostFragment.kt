package com.example.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.example.ui.adapters.PostAdapter
import com.example.ui.dialogs.DeletePostDialog
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
    protected abstract val postProgressBar: ProgressBar
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
    }

    private fun subscribeToObservers() {
        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { deletedPost ->
            postAdapter.posts -= deletedPost
        })
        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                postProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                postProgressBar.isVisible = true
            }
        ) { posts ->
            postProgressBar.isVisible = false
            postAdapter.posts = posts
        })
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                currentLikedIndex?.let { index ->
                    postAdapter.posts[index].isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                snackBar(it)
            },
            onLoading = {
                currentLikedIndex?.let {
                    postAdapter.posts[it].isLiking = true
                    postAdapter.notifyItemChanged(it)
                }
            }
        ) { isLiked ->
            currentLikedIndex?.let {
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.posts[it].apply {
                    this.isLiked = isLiked
                    if (isLiked) {
                        likedBy += uid
                    } else {
                        likedBy -= uid
                    }
                }
                postAdapter.notifyItemChanged(it)
            }
        })

        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                postProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                postProgressBar.isVisible = true
            }
        ) { posts ->
            postProgressBar.isVisible = false
            postAdapter.posts = posts
        })
    }
}