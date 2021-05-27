package com.jschoi.develop.aop_part03_chapter06.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.CHILD_CHAT
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.DB_ARTICLES
import com.jschoi.develop.aop_part03_chapter06.Config.Companion.DB_USERS
import com.jschoi.develop.aop_part03_chapter06.R
import com.jschoi.develop.aop_part03_chapter06.chatlist.ChatListItem
import com.jschoi.develop.aop_part03_chapter06.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private lateinit var articleAdapter: ArticleAdapter

    private val articleList = mutableListOf<ArticleModel>()
    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // Class Mapping
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            articleModel ?: return

            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onChildRemoved(snapshot: DataSnapshot) {
        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        }

        override fun onCancelled(error: DatabaseError) {
        }
    }

    // Firebase auth
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    // Activity - onCreated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentHomeBinding.bind(view)
        binding = fragmentHomeBinding

        articleList.clear()
        // Firebase realtime database 초기화
        articleDB = Firebase.database.reference.child(DB_ARTICLES)
        userDB = Firebase.database.reference.child(DB_USERS)

        // Adapter init
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                val currentId = auth.currentUser!!
                // 내가 올린 상품이 아닌 경우
                if (currentId.uid != articleModel.sellerId) {
                    val chatRoom = ChatListItem(
                        buyerId = currentId.uid,
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    // 내 채팅목록 DB Push
                    userDB.child(currentId.uid)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)
                    // 상 채팅목록 DB Push
                    userDB.child(articleModel.sellerId)
                        .child(CHILD_CHAT)
                        .push()
                        .setValue(chatRoom)

                    showSnackbar("채팅방이 생성되었습니다. 채팅탭에서 확인해주세요")
                } else {
                    showSnackbar("내가 올린 아이템입니다")
                }
            } else {
                showSnackbar("로그인 후 사용해주세요")
            }
        })

        fragmentHomeBinding.articleRecyclerView.layoutManager = LinearLayoutManager(context)
        fragmentHomeBinding.articleRecyclerView.adapter = articleAdapter

        // Floating Button init
        fragmentHomeBinding.addFloatingButton.setOnClickListener {
            // context : 사용가능 but context == nullable
            // startActivity(Intent(requireActivity(), addArticleActivity::class.java))
            context?.let {
                if (auth.currentUser != null) {
                    startActivity(Intent(context, AddArticleActivity::class.java))
                } else {
                    showSnackbar("로그인 후 사용해주세요")
                }
            }
        }

        articleDB.addChildEventListener(listener)
    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()   // Adapter 다시 그리기.
    }

    override fun onDestroyView() {
        super.onDestroyView()

        articleDB.removeEventListener(listener)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}