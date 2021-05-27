package com.example.ui.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.example.socialmedia.R
import com.example.ui.adapters.CommentAdapter
import com.example.ui.snackBar
import com.example.utils.EventObserver
import com.example.viewmodel.CommentViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_comment.*
import javax.inject.Inject

@AndroidEntryPoint
class CommentDialog : DialogFragment(R.layout.fragment_comment) {

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var commentAdapter: CommentAdapter

    private val args: CommentDialogArgs by navArgs()

    private val viewModel: CommentViewModel by viewModels()

    private lateinit var dialogView: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return dialogView
    }

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.fragment_comment,
            null
        )
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.comment)
            .setView(dialogView)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        viewModel.getCommentsForPost(args.postId)

        btnComment.setOnClickListener {
            val commentText = comment_text.text.toString()
            viewModel.createComment(commentText, args.postId)
            comment_text.text?.clear()
        }

        commentAdapter.setOnDeleteCommentClickListener { comment ->
            viewModel.deleteComment(comment)
        }

        commentAdapter.setOnUserClickListener { comment ->
            if(FirebaseAuth.getInstance().uid!! == comment.uid) {
                requireActivity().bottomNavigationView.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController().navigate(
                CommentDialogDirections.actionToOthersProfileFragment(comment.uid)
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.commentsForPost.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressbar.isVisible = false
                snackBar(it)
            },
            onLoading = { progressbar.isVisible = true }
        ) { comments ->
            progressbar.isVisible = false
            commentAdapter.comments = comments
        })
        viewModel.createCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressbar.isVisible = false
                snackBar(it)
                btnComment.isEnabled = true
            },
            onLoading = {
                progressbar.isVisible = true
                btnComment.isEnabled = false
            }
        ) { comment ->
            progressbar.isVisible = false
            btnComment.isEnabled = true
            commentAdapter.comments += comment
        })
        viewModel.deleteCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressbar.isVisible = false
                snackBar(it)
            },
            onLoading = { progressbar.isVisible = true }
        ) { comment ->
            progressbar.isVisible = false
            commentAdapter.comments -= comment
        })
    }

    private fun setupRecyclerView() = comments.apply {
        adapter = commentAdapter
        layoutManager = LinearLayoutManager(requireContext())
    }
}