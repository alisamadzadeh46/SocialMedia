package com.example.repositories

import android.net.Uri
import com.example.data.entities.ProfileUpdate
import com.example.data.entities.User
import com.example.utils.Resource

interface ProfileUpdateRepository {
    suspend fun updateProfile(profileUpdate: ProfileUpdate): Resource<Any>
    suspend fun updateProfilePicture(uid: String, imageUri: Uri): Uri?
    suspend fun user(uid: String): Resource<User>
}