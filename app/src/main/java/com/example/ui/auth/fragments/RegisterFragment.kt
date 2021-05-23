package com.example.ui.auth.fragments


import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.socialmedia.R
import com.example.utils.Resource
import com.example.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_register.*

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register) {

    private lateinit var authViewModel: AuthViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authViewModel = ViewModelProvider(requireActivity()).get(AuthViewModel::class.java)
        subscribeToObservers()
        register.setOnClickListener {
            authViewModel.register(
                requireContext(),
                email_text.text.toString(),
                username_text.text.toString(),
                password_text.text.toString(),
                repeat_password_text.text.toString()
            )
        }
        login.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else
                findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )


        }
    }
    private fun subscribeToObservers() {
        authViewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                registerProgressBar.isVisible = false
                snackbar(it)
            },
            onLoading = { registerProgressBar.isVisible = true }
        ) {
            registerProgressBar.isVisible = false
            snackbar(getString(R.string.success_registration))
        })
    }

}