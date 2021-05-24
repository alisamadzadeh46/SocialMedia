package com.example.repositories

import com.example.data.entities.Post
import com.example.data.entities.User
import com.example.utils.Resource

interface PostsRepository {
    suspend fun post():Resource<List<Post>>
    suspend fun user(uid: String): Resource<User>
}