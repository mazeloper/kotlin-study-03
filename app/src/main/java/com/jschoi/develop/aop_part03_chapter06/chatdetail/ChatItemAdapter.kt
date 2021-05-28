package com.jschoi.develop.aop_part03_chapter06.chatdetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jschoi.develop.aop_part03_chapter06.databinding.ItemChatBinding


class ChatItemAdapter : ListAdapter<ChatItem, ChatItemAdapter.ChatItemViewHolder>(diffUtil) {
    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ChatItem>() {
            override fun areItemsTheSame(oldListItem: ChatItem, newListItem: ChatItem): Boolean {
                // return oldItem.key == newItem.key
                return oldListItem == newListItem
            }

            override fun areContentsTheSame(oldListItem: ChatItem, newListItem: ChatItem): Boolean {
                return oldListItem == newListItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatItemViewHolder {
        return ChatItemViewHolder(
            ItemChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ChatItemViewHolder, position: Int) {
        holder.bind(currentList[position])
    }


    inner class ChatItemViewHolder(private val binding: ItemChatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(ChatItem: ChatItem) {

            binding.senderTextView.text = ChatItem.senderId
            binding.messageTextView.text = ChatItem.message
        }
    }
}