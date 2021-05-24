package com.example.ui.fragments.main


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.socialmedia.R
import com.example.ui.snackBar
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.example.ui.slideUpViews
import com.example.utils.EventObserver
import com.example.viewmodel.CreatePostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_create_post.*
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_create_post) {
    private val createPostViewModel: CreatePostViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager
    private var curImageUri: Uri? = null
    private lateinit var cropContent: ActivityResultLauncher<String>
    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(16, 9)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uriContent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                createPostViewModel.setCurImageUri(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        select_image.setOnClickListener {
            cropContent.launch("image/*")
        }
        image.setOnClickListener {
            cropContent.launch("image/*")
        }
        btnPost.setOnClickListener {
            curImageUri?.let { uri ->
                createPostViewModel.createPost(
                    requireContext(),
                    uri,
                    description_text.text.toString()
                )
            } ?: snackBar(getString(R.string.error_no_image_chosen))
        }
        slideUpViews(requireContext(), image, select_image, description, btnPost)
    }

    private fun subscribeToObservers() {
        createPostViewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            select_image.isVisible = false
            glide.load(curImageUri).into(image)
        }
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