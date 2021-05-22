package com.example.repositories.impl

import com.example.repositories.AuthRepository
import com.example.utils.Resource
import com.google.firebase.auth.AuthResult

class AuthImpl : AuthRepository {
    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        TODO("Not yet implemented")
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        TODO("Not yet implemented")
    }
}