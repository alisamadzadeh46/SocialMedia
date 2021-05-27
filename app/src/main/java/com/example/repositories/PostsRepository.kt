package com.example.repositories

import com.example.data.entities.Comment
import com.example.data.entities.Post
import com.example.data.entities.User
import com.example.utils.Resource

interface PostsRepository {
    suspend fun post(): Resource<List<Post>>
    suspend fun user(uid: String): Resource<User>
    suspend fun toggleLikeForPost(post: Post): Resource<Boolean>
    suspend fun deletePost(post: Post): Resource<Post>
    suspend fun users(uid: List<String>): Resource<List<User>>
    suspend fun profilePosts(uid: String): Resource<List<Post>>
    suspend fun toggleFollowForUser(uid: String): Resource<Boolean>
    suspend fun createComment(commentText:String,postId:String):Resource<Comment>
    suspend fun deleteComment(comment: Comment):Resource<Comment>
}