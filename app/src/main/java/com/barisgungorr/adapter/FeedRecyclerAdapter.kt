package com.barisgungorr.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.barisgungorr.instagramclone.databinding.ActivityFeedBinding
import com.barisgungorr.instagramclone.databinding.RecyclerRowBinding
import com.barisgungorr.model.Post
import com.squareup.picasso.Picasso
import kotlinx.coroutines.NonDisposableHandle.parent

class FeedRecyclerAdapter(private val  postList: ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {
    class PostHolder(val binding: RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return PostHolder(binding)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerEmailText.text = postList.get(position).email
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).dowlandUrl).into(holder.binding.recyclerImageView);
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
