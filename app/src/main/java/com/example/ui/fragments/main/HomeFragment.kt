package com.example.ui.fragments.main


import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.R
import com.example.ui.fragments.BasePostFragment
import com.example.viewmodel.BasePostViewModel
import com.example.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : BasePostFragment(R.layout.fragment_home) {
    override val postProgressBar: ProgressBar
        get() = progressBar
    override val basePostViewModel: BasePostViewModel
        get() {
            val viewModel: HomeViewModel by viewModels()
            return viewModel
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RecyclerView()
    }

    private fun RecyclerView() = posts.apply {
        adapter = postAdapter
        layoutManager = LinearLayoutManager(requireContext())
        itemAnimator = null
    }
}