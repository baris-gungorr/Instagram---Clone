package com.barisgungorr.instagramclone

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.barisgungorr.view.FeedActivity
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

        val currentUser = auth.currentUser  // Eğer kullanıcı daha önce giriş yaptı ise direkt feed'den başlatıyoruz

        if(currentUser != null) {  //Güncel kullanıcı yok ise feed activity'e git diyoruz
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
    fun signinClick(view: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.equals("") || (password.equals(""))) {
            Toast.makeText(this, "Please enter email and password!", Toast.LENGTH_LONG).show() // boş ise
        }else{
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener { // Boş değilse giriş yap

                Toast.makeText(this,"WElCOME!${email}",Toast.LENGTH_LONG).show()

                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener { // Hata olur ise
                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()

            }
        }
    }
    fun signupclick(view: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()

        if (email.equals("") || (password.equals(""))) {   // e-mail ve passWord almak boş mu değil mi kontrolü yapıyoruz
        Toast.makeText(this,"Please enter email and password!",Toast.LENGTH_LONG).show()

        }else {  // boş değilse bu işlemi aSenkron yapmamız lazım çünkü cpu ile değil kullanıcı internet gücü ile yapılacak

            auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { //kayıt başarılı olursa intent yapılacak
                // succes
                val intent = Intent(this@MainActivity, FeedActivity::class.java)
                startActivity(intent)
                finish()

            }.addOnFailureListener { // başarısız bir giriş olursa kullanıcının anlayacağı dilden hata mesajını yazdır

                Toast.makeText(this@MainActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                 //  Örn: şifreyi 6 haneden az yapmak
            }
        }
    }
}