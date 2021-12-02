package com.example.networkretrofit

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.networkretrofit.data.Repository
import com.example.networkretrofit.data.RepositoryItem

import com.example.networkretrofit.databinding.ItemRecyclerBinding

class CustomAdapter:RecyclerView.Adapter<CustomAdapter.Holder>() {
    var userList : Repository? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return userList?.size ?: 0
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val user = userList?.get(position)
        holder.setUser(user)
    }

    inner class Holder(val binding: ItemRecyclerBinding): RecyclerView.ViewHolder(binding.root){
        fun setUser(user: RepositoryItem?) {
            user?.let {
                binding.textName.text = it.name
                binding.textId.text = it.node_id
                Glide.with(binding.imageAvatar).load(it.owner.avatar_url).into(binding.imageAvatar)
            }
        }
    }
}

