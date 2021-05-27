package com.example.repositories.impl

import com.example.data.entities.Comment
import com.example.data.entities.User
import com.example.repositories.CommentRepository
import com.example.utils.Resource
import com.example.utils.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.scopes.ActivityScoped
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import java.util.*

@ActivityScoped
class CommentImpl : CommentRepository {
    private val store = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val comments = store.collection("comments")
    private val users = store.collection("users")
    override suspend fun createComment(commentText: String, postId: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                val uid = auth.uid!!
                val commentId = UUID.randomUUID().toString()
                val user = user(uid).data!!
                val comment = Comment(
                    commentId,
                    postId,
                    uid,
                    user.username,
                    user.profilePictureUrl,
                    commentText
                )
                comments.document(commentId).set(comment).await()
                Resource.Success(comment)
            }
        }


    override suspend fun deleteComment(comment: Comment) = withContext(Dispatchers.IO) {
        safeCall {
            comments.document(comment.commentId).delete().await()
            Resource.Success(comment)
        }
    }

    override suspend fun getCommentForPost(postId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val commentForPOST = comments
                .whereEqualTo("postId", postId)
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(Comment::class.java)
                .onEach { comment ->
                    val user = user(comment.uid).data!!
                    comment.username = user.username
                    comment.profilePictureUrl = user.profilePictureUrl

                }
            Resource.Success(commentForPOST)
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
