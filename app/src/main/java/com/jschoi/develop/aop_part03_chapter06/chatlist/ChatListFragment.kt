package com.jschoi.develop.aop_part03_chapter06.chatlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.CHILD_CHAT
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.DB_USERS
import com.jschoi.develop.aop_part03_chapter06.R
import com.jschoi.develop.aop_part03_chapter06.chatdetail.ChatRoomActivity
import com.jschoi.develop.aop_part03_chapter06.databinding.FragmentChatListBinding

class ChatListFragment : Fragment(R.layout.fragment_chat_list) {

    private lateinit var binding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private lateinit var chatDB: DatabaseReference

    private val chatRoomList = mutableListOf<ChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentChatListBinding = FragmentChatListBinding.bind(view)
        binding = fragmentChatListBinding

        chatListAdapter = ChatListAdapter(onItemClicked = { item ->
            context?.let {
                val intent = Intent(context, ChatRoomActivity::class.java)
                intent.putExtra("chatKey", item.key)
                startActivity(intent)
            }
        })
        chatRoomList.clear()

        fragmentChatListBinding.chatListRecyclerView.adapter = chatListAdapter
        fragmentChatListBinding.chatListRecyclerView.layoutManager = LinearLayoutManager(context)

        auth.currentUser?.let { currentUser ->
            // 로그인이 되어있다.
            chatDB = Firebase.database.reference.child(DB_USERS).child(currentUser.uid)
                .child(CHILD_CHAT)
        }

        chatDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {
                    // Class Casting
                    val model = it.getValue(ChatListItem::class.java)
                    model ?: return

                    chatRoomList.add(model)
                }
                chatListAdapter.submitList(chatRoomList)
                //chatListAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}