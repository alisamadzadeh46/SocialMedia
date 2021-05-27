package com.example.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.data.entities.Comment
import com.example.socialmedia.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_comment.view.*
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CommentViewHolder {
        return CommentViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_comment,
                parent,
                false
            )
        )
    }


    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment, glide, onDeleteCommentClickListener, onUserClickListener)

    }

    override fun getItemCount(): Int {
        return comments.size
    }

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            comment: Comment,
            glide: RequestManager,
            onDeleteCommentClickListener: ((Comment) -> Unit)?,
            onUserClickListener: ((Comment) -> Unit)?
        ) {
            itemView.apply {
                glide.load(comment.profilePictureUrl).into(profile)

                delete.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!

                comment_text.text = comment.comment
                comment_username.text = comment.username
                comment_username.setOnClickListener {
                    onUserClickListener?.let { click ->
                        click(comment)
                    }
                }
                delete.setOnClickListener {
                    onDeleteCommentClickListener?.let { click ->
                        click(comment)
                    }
                }
            }
        }

    }


    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {
        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.commentId == newItem.commentId
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var comments: List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onUserClickListener: ((Comment) -> Unit)? = null
    private var onDeleteCommentClickListener: ((Comment) -> Unit)? = null

    fun setOnUserClickListener(listener: (Comment) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeleteCommentClickListener(listener: (Comment) -> Unit) {
        onDeleteCommentClickListener = listener
    }
}