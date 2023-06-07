package com.barisgungorr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.barisgungorr.instagramclone.R
import com.barisgungorr.instagramclone.databinding.ActivityMainBinding
import com.barisgungorr.instagramclone.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

    }
    fun upload(view: View) {

    }
    fun selectimage(view: View) {


    }
}