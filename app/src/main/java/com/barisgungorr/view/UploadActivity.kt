package com.barisgungorr.view

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.barisgungorr.instagramclone.databinding.ActivityUploadBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private var selectedBitmap: Bitmap? = null

    private lateinit var auth : FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        registerLauncher()

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    private fun registerLauncher() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) { selectedPicture = intentFromResult.data
                        selectedPicture?.let {

                            binding.imageView.setImageURI(it)
                            binding.imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                            /*
                            Özetlemek gerekirse, URI'ler, Android uygulamalarında dosyaları veya kaynakları tanımlamak
                            ve erişmek için kullanılan birer tanımlayıcılardır.
                             Galeri gibi uygulamalarda, kullanıcının izin verdiği dosyalara erişmek için URI'leri kullanırız.
                             */

                        }
                    }
                }
            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    // permission granted
                    val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    Toast.makeText(this@UploadActivity, "Permission needed!", Toast.LENGTH_LONG).show()
                }
            }
    }

    fun upload(view: View) {
        val uuid = UUID.randomUUID() // random bir rakam veriyor dosyaya
        val imageName = "$uuid.jpg"

      val reference = storage.reference  //storage depo işlemleri
       val imageReference = reference.child("images").child(imageName)  // iamges diye klasör aç jpg koy

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {  // url'yi fireStore'a kaydediyoruz
            //dowland url -> firestore

                val uploadPictureReference = storage.reference.child("images").child(imageName) // veritabanı kayıt işlemleri
                uploadPictureReference.downloadUrl.addOnSuccessListener {

                    val dowlandUrl = it.toString()

                    if (auth.currentUser != null) {

                        val postMap = hashMapOf<String,Any>() // Anahtar kelimemiz string değer ise Any türünde olacak

                        if(auth.currentUser != null) {

                            postMap.put("dowlandUrl",dowlandUrl)
                            postMap.put("userEmail",auth.currentUser!!.email!!)
                            postMap.put("comment",binding.commentText.text.toString())
                            postMap.put("date",Timestamp.now())
                        }

                        firestore.collection("Posts").add(postMap).addOnSuccessListener {
                            finish()
                        }.addOnFailureListener {
                            Toast.makeText(this@UploadActivity,it.localizedMessage,Toast.LENGTH_LONG).show()
                        }

                    }

                }
                Toast.makeText(this,"SAVED",Toast.LENGTH_LONG).show()

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)
            val outputStream = ByteArrayOutputStream()
            smallBitmap.compress(
                Bitmap.CompressFormat.PNG,
                50,
                outputStream
            )   //görseli byteDizisine çevirmek
            val byteArray = outputStream.toByteArray()
            // Diğer işlemleri buraya ekleyin
        }

    }

    fun selectImage(view: View) {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view, "Galerin için izne ihtiyacım var ! ", Snackbar.LENGTH_INDEFINITE).setAction("Give permission") {

                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE) // Çıkan butona bastığımızda ne olacak
                    }.show()
            } else {
                //request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) // izni aldıysak
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    private fun makeSmallerBitmap(
        image: Bitmap,
        maximumSize: Int
    ): Bitmap {
        var width = image.width
        var height = image.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()

        if (bitmapRatio > 1) {
            // Landscape
            width = maximumSize
            val scaledHeight = (width.toDouble() / bitmapRatio).toInt()
            height = scaledHeight.coerceAtMost(maximumSize)
        } else if (bitmapRatio < 1) {
            // Portrait
            height = maximumSize
            val scaledWidth = (height.toDouble() * bitmapRatio).toInt()
            width = scaledWidth.coerceAtMost(maximumSize)
        } else {
            // Square
            width = maximumSize
            height = maximumSize
        }

        return Bitmap.createScaledBitmap(image, width, height, true)
    }
}

