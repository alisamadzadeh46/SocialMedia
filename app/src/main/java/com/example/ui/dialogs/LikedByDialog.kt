package com.example.ui.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.socialmedia.R
import com.example.ui.adapters.SearchAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class LikedByDialog(
    private val searchAdapter: SearchAdapter
):DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val likedBy = RecyclerView(requireContext()).apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.liked_by_dialog_title)
            .setView(likedBy)
            .create()
    }
}