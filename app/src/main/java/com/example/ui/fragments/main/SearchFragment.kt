package com.example.ui.fragments.main


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.socialmedia.R
import com.example.ui.adapters.SearchAdapter
import com.example.ui.snackBar
import com.example.utils.Constants.SEARCH_TIME_DELAY
import com.example.utils.EventObserver
import com.example.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_search.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {

    @Inject
    lateinit var searchAdapter: SearchAdapter
    private val viewModel: SearchViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        RecyclerView()
        subscribeToObservers()

        var job: Job? = null
        search_text.addTextChangedListener { edit ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                edit?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        searchAdapter.setOnUserClickListener { user ->
            findNavController()
                .navigate(
                    SearchFragmentDirections.actionToOthersProfileFragment(user.uid)
                )
        }

    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
            onError = {
                progressbar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                progressbar.isVisible = true
            }
        ) { users ->
            progressbar.isVisible = false
            searchAdapter.users = users
        })
    }

    private fun RecyclerView() = recyclerview.apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = searchAdapter
        itemAnimator = null
    }

}