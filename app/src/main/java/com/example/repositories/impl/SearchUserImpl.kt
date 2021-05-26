package com.example.repositories.impl

import com.example.data.entities.User
import com.example.repositories.SearchUserRepository
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

@ActivityScoped
class SearchUserImpl : SearchUserRepository {
    private val store = FirebaseFirestore.getInstance()
    private val users = store.collection("users")
    override suspend fun search(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResult = users.whereGreaterThanOrEqualTo("username",
                query.uppercase(Locale.ROOT)
            )
                .get().await().toObjects(User::class.java)
            Resource.Success(userResult)
        }
    }
}