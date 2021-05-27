package com.example.repositories

import com.example.data.entities.Comment
import com.example.data.entities.User
import com.example.utils.Resource

interface CommentRepository {
    suspend fun createComment(commentText:String,postId:String): Resource<Comment>
    suspend fun deleteComment(comment: Comment): Resource<Comment>
    suspend fun getCommentForPost(postId: String): Resource<List<Comment>>
    suspend fun user(uid: String): Resource<User>
}