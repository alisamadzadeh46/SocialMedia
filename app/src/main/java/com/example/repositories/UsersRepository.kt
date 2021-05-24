package com.example.repositories

import com.example.data.entities.User
import com.example.utils.Resource

interface UsersRepository {
    suspend fun users(uid: List<String>): Resource<List<User>>
    suspend fun user(uid: String): Resource<User>
}