package com.example.ui.auth.fragments


import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.socialmedia.R
import kotlinx.android.synthetic.main.fragment_register.*


class RegisterFragment : Fragment(R.layout.fragment_register) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        login.setOnClickListener {
            if (findNavController().previousBackStackEntry != null) {
                findNavController().popBackStack()
            } else {
                findNavController().navigate(
                    RegisterFragmentDirections.actionRegisterFragmentToLoginFragment()
                )
            }

        }
    }

}