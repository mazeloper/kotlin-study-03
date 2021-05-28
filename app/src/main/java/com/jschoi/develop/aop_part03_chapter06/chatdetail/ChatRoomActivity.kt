package com.jschoi.develop.aop_part03_chapter06.chatdetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.DB_CHATS
import com.jschoi.develop.aop_part03_chapter06.databinding.AcitivityChatRoomBinding

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: AcitivityChatRoomBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val chatList = mutableListOf<ChatItem>()
    private val chatAdapter = ChatItemAdapter()
    private var chatDB: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AcitivityChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val chatKey = intent.getLongExtra("chatKey", -1)
        // DB 초기화
        chatDB = Firebase.database.reference.child(DB_CHATS).child("$chatKey")
        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(ChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                chatAdapter.submitList(chatList)
                chatAdapter.notifyDataSetChanged()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        binding.chatRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ChatRoomActivity)
            adapter = chatAdapter
        }
        binding.sendButton.setOnClickListener {
            auth.currentUser ?: return@setOnClickListener
            val currentUser = auth.currentUser!!
            val chatItem = ChatItem(
                senderId = currentUser.uid,
                message = binding.messageEditText.text.toString()
            )
            chatDB?.push()?.setValue(chatItem)
        }
    }
}