package com.example.ui.fragments.main

import android.os.Bundle
import androidx.transition.ChangeBounds
import androidx.transition.TransitionManager
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.example.data.entities.User
import com.example.socialmedia.R
import com.example.utils.EventObserver
import kotlinx.android.synthetic.main.fragment_profile.*

class OthersProfileFragment : ProfileFragment() {

    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    private var curUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        follow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            follow.isVisible = true
            setupToggleFollowButton(it)
            curUser = it
        })
        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            curUser?.isFollowing = it
            setupToggleFollowButton(curUser ?: return@EventObserver)
        })
    }

    private fun setupToggleFollowButton(user: User) {
        follow.apply {
            val changeBounds = ChangeBounds().apply {
                duration = 300
                interpolator = OvershootInterpolator()
            }
            val set1 = ConstraintSet()
            val set2 = ConstraintSet()
            set1.clone(requireContext(), R.layout.fragment_profile)
            set2.clone(requireContext(), R.layout.fragment_profile_anim)
            TransitionManager.beginDelayedTransition(
                profile,
                changeBounds
            )
            if (user.isFollowing) {
                text = requireContext().getString(R.string.unfollow)
                backgroundTintMode = null
                background = ContextCompat.getDrawable(requireContext(), R.drawable.unfollow_shape)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                set1.applyTo(profile)
            } else {
                text = requireContext().getString(R.string.follow)
                background = ContextCompat.getDrawable(requireContext(), R.drawable.follow_shape)
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                set2.applyTo(profile)
            }
        }
    }
}

