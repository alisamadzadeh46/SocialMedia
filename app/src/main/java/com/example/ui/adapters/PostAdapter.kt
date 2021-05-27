package com.example.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.data.entities.Post
import com.example.socialmedia.R
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.item_post.view.*
import javax.inject.Inject

class PostAdapter @Inject constructor(
    private val glide: RequestManager
) : PagingDataAdapter<Post, PostAdapter.PostViewHolder>(Companion) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return PostViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_post,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return
        holder.bind(
            post,
            glide,
            onUserClickListener,
            onCommentsClickListener,
            onDeletePostClickListener,
            onLikeClickListener,
            onLikedByClickListener
        )

    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            post: Post,
            glide: RequestManager,
            onUserClickListener: ((String) -> Unit)?,
            onCommentsClickListener: ((Post) -> Unit)?,
            onDeletePostClickListener: ((Post) -> Unit)?,
            onLikeClickListener: ((Post, Int) -> Unit)?,
            onLikedByClickListener: ((Post) -> Unit)?
        ) {
            itemView.apply {
                glide.load(post.imageUrl).into(image)
                glide.load(post.authorProfilePictureUrl).into(profile_image)
                post_author.text = post.authorUsername
                description.text = post.text
                val likeCount = post.likedBy.size
                likedBy.text = when {
                    likeCount <= 0 -> "No likes"
                    likeCount == 1 -> "Liked by 1 person"
                    else -> "Liked by $likeCount people"
                }
                val uid = FirebaseAuth.getInstance().uid!!
                delete.isVisible = uid == post.authorUid
                like.setImageResource(
                    if (post.isLiked) {
                        R.drawable.ic_liked
                    } else R.drawable.ic_like
                )

                post_author.setOnClickListener {
                    onUserClickListener?.let { click ->
                        click(post.authorUid)
                    }
                }
                profile_image.setOnClickListener {
                    onUserClickListener?.let { click ->
                        click(post.authorUid)
                    }
                }
                likedBy.setOnClickListener {
                    onLikedByClickListener?.let { click ->
                        click(post)
                    }
                }
                like.setOnClickListener {
                    onLikeClickListener?.let { click ->
                        if (!post.isLiking) click(post, layoutPosition)

                    }
                }
                comment.setOnClickListener {
                    onCommentsClickListener?.let { click ->
                        click(post)
                    }
                }
                delete.setOnClickListener {
                    onDeletePostClickListener?.let { click ->
                        click(post)
                    }
                }
            }
        }

    }


    companion object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private var onLikeClickListener: ((Post, Int) -> Unit)? = null
    private var onUserClickListener: ((String) -> Unit)? = null
    private var onDeletePostClickListener: ((Post) -> Unit)? = null
    private var onLikedByClickListener: ((Post) -> Unit)? = null
    private var onCommentsClickListener: ((Post) -> Unit)? = null

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        onLikeClickListener = listener
    }

    fun setOnUserClickListener(listener: (String) -> Unit) {
        onUserClickListener = listener
    }

    fun setOnDeletePostClickListener(listener: (Post) -> Unit) {
        onDeletePostClickListener = listener
    }

    fun setOnLikedByClickListener(listener: (Post) -> Unit) {
        onLikedByClickListener = listener
    }

    fun setOnCommentsClickListener(listener: (Post) -> Unit) {
        onCommentsClickListener = listener
    }
}