package com.example.repositories.impl

import android.net.Uri
import com.example.data.entities.ProfileUpdate
import com.example.data.entities.User
import com.example.repositories.ProfileUpdateRepository
import com.example.utils.Constants.DEFAULT_PROFILE_PICTURE_URL
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

@ActivityScoped
class ProfileUpdateImpl : ProfileUpdateRepository {
    private val store = FirebaseFirestore.getInstance()
    private val storage = Firebase.storage
    private val users = store.collection("users")
    override suspend fun updateProfile(profileUpdate: ProfileUpdate) = withContext(Dispatchers.IO) {
        safeCall {
            val imageUrl = profileUpdate.profilePictureUri?.let {
                updateProfilePicture(profileUpdate.uidToUpdate, it).toString()
            }
            val map = mutableMapOf(
               "username" to profileUpdate.username,
                "description" to profileUpdate.description,

            )
            imageUrl?.let {
                map["profilePictureUrl"] = it
            }
            users.document(profileUpdate.uidToUpdate).update(map.toMap()).await()
            Resource.Success(Any())
        }
    }


    override suspend fun updateProfilePicture(uid: String, imageUri: Uri) =
        withContext(Dispatchers.IO) {
            val storageRef = storage.getReference(uid)
            val user = user(uid).data!!
            if (user.profilePictureUrl != DEFAULT_PROFILE_PICTURE_URL) {
                storage.getReferenceFromUrl(user.profilePictureUrl).delete().await()
            }
            storageRef.putFile(imageUri).await().metadata?.reference?.downloadUrl?.await()

        }

    override suspend fun user(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val user = users.document(uid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            val currentUid = FirebaseAuth.getInstance().uid!!
            val currentUser = users.document(currentUid).get().await().toObject(User::class.java)
                ?: throw IllegalStateException()
            user.isFollowing = uid in currentUser.follows
            Resource.Success(user)

        }
    }
}