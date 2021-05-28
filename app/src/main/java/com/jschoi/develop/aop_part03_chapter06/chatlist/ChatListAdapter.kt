package com.jschoi.develop.aop_part03_chapter06.chatlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jschoi.develop.aop_part03_chapter06.databinding.ItemChatListBinding

class ChatListAdapter(val onItemClicked: (ChatListItem) -> Unit) :
    ListAdapter<ChatListItem, ChatListAdapter.ChatListItemViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatListItem>() {
            override fun areItemsTheSame(oldListItem: ChatListItem, newListItem: ChatListItem): Boolean {
                return oldListItem.key == newListItem.key
            }

            override fun areContentsTheSame(oldListItem: ChatListItem, newListItem: ChatListItem): Boolean {
                return oldListItem == newListItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListItemViewHolder {
        return ChatListItemViewHolder(
            ItemChatListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatListItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    inner class ChatListItemViewHolder(private val binding: ItemChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatListListItem: ChatListItem) {

            binding.chatRoomTitleTextView.text = chatListListItem.itemTitle
            
            binding.root.setOnClickListener {
                onItemClicked(chatListListItem)
            }
        }
    }
}