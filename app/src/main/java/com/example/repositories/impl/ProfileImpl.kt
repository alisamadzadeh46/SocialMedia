package com.example.repositories.impl

import com.example.data.entities.Post
import com.example.data.entities.User
import com.example.repositories.ProfileRepository
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

@ActivityScoped
class ProfileImpl : ProfileRepository {
    private val store = FirebaseFirestore.getInstance()
    private val users = store.collection("users")
    private val posts = store.collection("posts")
    override suspend fun profilePosts(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val profilePost = posts.whereEqualTo("authorUid", uid)
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Post::class.java)
                .onEach { post ->
                    val user = user(post.authorUid).data!!
                    post.authorProfilePictureUrl = user.profilePictureUrl
                    post.authorUsername = user.username
                    post.isLiked = uid in post.likedBy

                }
            Resource.Success(profilePost)
        }
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