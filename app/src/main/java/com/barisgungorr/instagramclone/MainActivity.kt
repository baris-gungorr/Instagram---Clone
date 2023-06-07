package com.barisgungorr.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.barisgungorr.FeedActivity
import com.barisgungorr.instagramclone.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val currentUser = auth.currentUser  // burada eğer kullanıcı daha önce giriş yaptı ise direkt ana ekrandan başlatıyoruz
        if(currentUser != null) {
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()

        }

    }
    fun signinClick(view: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.equals("") || (password.equals(""))) {   // boş mu ? evet
            Toast.makeText(this, "Enter email and password!", Toast.LENGTH_LONG).show()
        }else{
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {

                Toast.makeText(this,"Welcome!${email}",Toast.LENGTH_LONG).show()


                val intent = Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()
            }.addOnFailureListener {
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }
    }
    fun signupclick(view: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.equals("") || (password.equals(""))) {   // boş mu ? evet
        Toast.makeText(this,"Enter email and password!",Toast.LENGTH_LONG).show()

        }else {  // boş mu ? - hayır  - // bu işlemi aSenkron yapmamız lazım çünkü cpu ile değil kullanıcı internet gücü ile yapılacak

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { // succes
                val intent = Intent(this@MainActivity,FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener { // failed

                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show() // eksik bir giriş yapıldığında örn: şifreyi 6 haneden az yapmak
            }


        }


    }
}