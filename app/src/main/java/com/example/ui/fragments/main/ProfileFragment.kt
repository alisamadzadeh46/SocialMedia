package com.example.ui.fragments.main

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.R
import com.example.ui.fragments.BasePostFragment
import com.example.ui.snackBar
import com.example.utils.EventObserver
import com.example.viewmodel.BasePostViewModel
import com.example.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_profile.*

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment(R.layout.fragment_profile) {

    override val postProgressBar: ProgressBar
        get() = profilePostsProgressBar
    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }
    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RecyclerView()
        subscribeToObservers()

        follow.isVisible = false
        viewModel.loadProfile(uid)
    }
    private fun RecyclerView() = RecyclerViewPost.apply {
        adapter = postAdapter
        itemAnimator = null
        layoutManager = LinearLayoutManager(requireContext())
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver(
            onError = {
                ProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { ProgressBar.isVisible = true }
        ) { user ->
            ProgressBar.isVisible = false
            username.text = user.username
            description.text = if(user.description.isEmpty()) {
                requireContext().getString(R.string.no_description)
            } else user.description
            glide.load(user.profilePictureUrl).into(profile_image)
        })
    }
}