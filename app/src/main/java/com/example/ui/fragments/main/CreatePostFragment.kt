package com.example.ui.fragments.main


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialmedia.R
import com.example.ui.snackBar
import androidx.navigation.fragment.findNavController
import com.example.utils.EventObserver
import com.example.viewmodel.CreatePostViewModel
import kotlinx.android.synthetic.main.fragment_create_post.*


class CreatePostFragment : Fragment(R.layout.fragment_create_post) {
    private val createPostViewModel: CreatePostViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        createPostViewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                loading.isVisible = false
                snackBar(it)
            },
            onLoading = { loading.isVisible = true }
        ) {
            loading.isVisible = false
            findNavController().popBackStack()
        })
    }
}