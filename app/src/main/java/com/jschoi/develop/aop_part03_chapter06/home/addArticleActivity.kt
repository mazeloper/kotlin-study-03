package com.jschoi.develop.aop_part03_chapter06.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jschoi.develop.aop_part03_chapter06.databinding.ActivityAddArticleBinding

class addArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}