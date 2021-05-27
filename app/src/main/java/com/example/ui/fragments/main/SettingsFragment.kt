package com.example.ui.fragments.main


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.data.entities.ProfileUpdate
import com.example.socialmedia.R
import com.example.ui.slideUpViews
import com.example.ui.snackBar
import com.example.utils.EventObserver
import com.example.viewmodel.SettingsViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {


    @Inject
    lateinit var glide: RequestManager

    private val viewModel: SettingsViewModel by viewModels()

    private var curImageUri: Uri? = null

    private lateinit var cropContent: ActivityResultLauncher<Any?>

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uriContent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) { uri ->
            uri?.let {
                viewModel.setCurImageUri(it)
                btnUpdateProfile.isEnabled = true
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        val uid = FirebaseAuth.getInstance().uid!!
        viewModel.getUser(uid)
        btnUpdateProfile.isEnabled = false
        username_text.addTextChangedListener {
            btnUpdateProfile.isEnabled = true
        }
        description_text.addTextChangedListener {
            btnUpdateProfile.isEnabled = true
        }

        profile_image.setOnClickListener {
            cropContent.launch(null)
        }

        btnUpdateProfile.setOnClickListener {
            val username = username_text.text.toString()
            val description = description_text.text.toString()
            val profileUpdate = ProfileUpdate(uid, username, description, curImageUri)
            viewModel.updateProfile(requireContext(),profileUpdate)
        }

        slideUpViews(
            requireContext(),
            profile_image,
            username_text,
            description_text,
            btnUpdateProfile
        )
    }

    private fun subscribeToObservers() {
        viewModel.getUserStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                settingsProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { settingsProgressBar.isVisible = true }
        ) { user ->
            settingsProgressBar.isVisible = false
            glide.load(user.profilePictureUrl).into(profile_image)
            username_text.setText(user.username)
            description_text.setText(user.description)
            btnUpdateProfile.isEnabled = false
        })
        viewModel.curImageUri.observe(viewLifecycleOwner) { uri ->
            curImageUri = uri
            glide.load(uri).into(profile_image)
        }
        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                settingsProgressBar.isVisible = false
                snackBar(it)
                btnUpdateProfile.isEnabled = true
            },
            onLoading = {
                settingsProgressBar.isVisible = true
                btnUpdateProfile.isEnabled = false
            }
        ) {
            settingsProgressBar.isVisible = false
            btnUpdateProfile.isEnabled = false
            snackBar(requireContext().getString(R.string.profile_updated))
        })
    }

}