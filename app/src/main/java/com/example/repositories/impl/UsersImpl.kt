package com.example.repositories.impl

import com.example.data.entities.User
import com.example.repositories.UsersRepository
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException

@ActivityScoped
class UsersImpl : UsersRepository {
    private val store = FirebaseFirestore.getInstance()
    private val users = store.collection("users")
    override suspend fun users(uid: List<String>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val usersList = users.whereIn("uid", uid).orderBy("username").get().await()
                    .toObjects(User::class.java)
                Resource.Success(usersList)
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