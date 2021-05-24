package com.example.repositories

import android.net.Uri
import com.example.utils.Resource

interface CreatePostRepository {
    suspend fun createPost(imageUri: Uri,text:String):Resource<Any>

}