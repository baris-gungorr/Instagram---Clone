package com.barisgungorr.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.barisgungorr.adapter.FeedRecyclerAdapter
import com.barisgungorr.instagramclone.MainActivity
import com.barisgungorr.instagramclone.R
import com.barisgungorr.instagramclone.databinding.ActivityFeedBinding
import com.barisgungorr.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var postArrayList : ArrayList<Post>
    private lateinit var feedAdapter : FeedRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        db = Firebase.firestore
        postArrayList = ArrayList<Post>()
        getData()
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        feedAdapter = FeedRecyclerAdapter(postArrayList)
        binding.recyclerView.adapter = feedAdapter


    }
    private fun getData() {
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error -> // tarihe göre sıralama yapıyoruz en son en yukarıda

            if (error != null) {
                Toast.makeText(this,error.localizedMessage,Toast.LENGTH_LONG).show()
            }else{
                if (value != null) {
                    if (!value.isEmpty) {
                        val documents = value.documents
                        postArrayList.clear()

                        for (document in documents) { // dokümanları alıyoruz 

                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val dowlandUrl = document.get("dowlandUrl") as String

                            // bunları bir dizi içerisinde göstermemiz lazım bunun için bir model oluşturalım
                            val post = Post(userEmail,comment,dowlandUrl)

                            postArrayList.add(post)
                        }
                     feedAdapter!!.notifyDataSetChanged()
                    }
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean { // menümüzü bağlayalım
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // item'lar seçildiğinde ne olacak

        if (item.itemId == R.id.add_post) {
            val intent = Intent(this, UploadActivity::class.java) // addPost seçilirse
            startActivity(intent)

        }else if (item.itemId == R.id.Signout) { // Çıkış yapmak isterse
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}