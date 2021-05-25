package com.example.repositories

import com.example.data.entities.User
import com.example.utils.Resource

interface SearchUserRepository {
    suspend fun search(query: String): Resource<List<User>>
}