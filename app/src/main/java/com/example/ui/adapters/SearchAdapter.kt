package com.example.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.example.data.entities.User
import com.example.socialmedia.R
import kotlinx.android.synthetic.main.item_post.view.profile_image
import kotlinx.android.synthetic.main.item_user.view.*
import javax.inject.Inject

class SearchAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<SearchAdapter.SearchViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ):SearchViewHolder {
        return SearchViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_user,
                parent,
                false
            )
        )
    }



    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val user = users[position]
        holder.bind(
            user,
            glide,
            onUserClickListener
        )

    }

    override fun getItemCount(): Int {
        return users.size
    }

    class SearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(
            user: User,
            glide: RequestManager,
            onUserClickListener: ((User) -> Unit)?,
        ) {
            itemView.apply {
                glide.load(user.profilePictureUrl).into(profile_image)
                username.text = user.username
                itemView.setOnClickListener {
                    onUserClickListener?.let { click ->
                        click(user)
                    }
                }
            }
        }

    }


    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }
    private val differ = AsyncListDiffer(this, diffCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }
}