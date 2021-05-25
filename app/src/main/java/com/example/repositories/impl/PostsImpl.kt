package com.example.repositories.impl


import com.example.data.entities.Post
import com.example.data.entities.User
import com.example.repositories.PostsRepository
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

@ActivityScoped
class PostsImpl : PostsRepository {
    private val store = FirebaseFirestore.getInstance()
    private val users = store.collection("users")
    private val posts = store.collection("posts")
    private val storage = Firebase.storage
    override suspend fun post() = withContext(Dispatchers.IO) {
        safeCall {
            val uid = FirebaseAuth.getInstance().uid!!
            val follows = user(uid).data!!.follows
            val allPosts = posts.whereIn("authorUid", follows)
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
            Resource.Success(allPosts)

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

    override suspend fun toggleLikeForPost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            var isLiked = false
            store.runTransaction {
                val uid = FirebaseAuth.getInstance().uid!!
                val postResult = it.get(posts.document(post.id))
                val currentLikes = postResult.toObject(Post::class.java)?.likedBy ?: listOf()
                it.update(
                    posts.document(post.id), "likedBy",
                    if (uid in currentLikes) currentLikes - uid else {
                        isLiked = true
                        currentLikes + uid

                    }
                )
            }.await()
            Resource.Success(isLiked)
        }
    }

    override suspend fun deletePost(post: Post) = withContext(Dispatchers.IO) {
        safeCall {
            posts.document(post.id).delete().await()
            storage.getReferenceFromUrl(post.imageUrl).delete().await()
            Resource.Success(post)
        }
    }

    override suspend fun users(uid: List<String>): Resource<List<User>> {
        TODO("Not yet implemented")
    }


}