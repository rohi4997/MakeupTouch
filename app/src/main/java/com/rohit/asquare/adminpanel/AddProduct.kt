package com.rohit.asquare.adminpanel

import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rohit.asquare.R
import java.util.*


class AddProduct : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var productImage: ImageView
    private lateinit var dbref: DatabaseReference
    private lateinit var storageRefrence: StorageReference
    private lateinit var storage: FirebaseStorage
    private lateinit var accessToken: Uri
    lateinit var mProgressDialog:Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_product_adminpanel)
        storage = FirebaseStorage.getInstance()
        storageRefrence = storage.reference
        accessToken = "".toUri()
        val productName = findViewById<EditText>(R.id.product_name)
        val productDescription = findViewById<EditText>(R.id.description)
        val price = findViewById<EditText>(R.id.price)
        val backBtn = findViewById<ImageView>(R.id.back)
        productImage = findViewById(R.id.productImage)
        val addBtn = findViewById<Button>(R.id.add_btn)


        productImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                //premission is here
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            }else{
                //request for permission
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),200)
            }
        }

        addBtn.setOnClickListener {
            if (productName.text.toString() == "" || productDescription.text.toString() == "" || price.text.toString() == "" || imageUri == null) {
                Toast.makeText(this, "Please Fill all details", Toast.LENGTH_SHORT).show()
            } else {
//                Glide.with(applicationContext).load(imageUri).override(150,150).into(productImage)
                showProgressDialog("Uploading...")
                uploadOnFirebase(
                    productName.text.toString(),
                    productDescription.text.toString(),
                    price.text.toString()
                )
            }
        }

        backBtn.setOnClickListener {
            onBackPressed()
        }

    }

    private fun uploadOnFirebase(productName: String, productDescription: String, price: String) {
        dbref = FirebaseDatabase.getInstance().getReference("items")
        dbref.child(productName).child("title").setValue(productName)
        dbref.child(productName).child("description").setValue(productDescription)
        dbref.child(productName).child("price").setValue(price)
        uploadImage(productName)
//        dbref.child("title").child("image").setValue(accessToken.toString())
//        Snackbar.make(applicationContext, View(applicationContext),"Added This Product", Snackbar.LENGTH_LONG).show()
//        Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data?.data
            Glide.with(applicationContext).load(imageUri).override(150, 150).into(productImage)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode)
        {
            200 -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //granted
                val gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(gallery, 100)
            } else {
                //not granted
                Toast.makeText(this,"Please Give permission to read external Storage", Toast.LENGTH_SHORT).show()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        }
    }

    private fun uploadImage(productName: String) {
        val randomName = UUID.randomUUID().toString()
        val imageref: StorageReference = storageRefrence.child("items/products/" + randomName)
        imageref.putFile(imageUri!!).addOnSuccessListener {
            it.storage.downloadUrl.addOnSuccessListener {
                dbref = FirebaseDatabase.getInstance().getReference("items")
                dbref.child(productName).child("image").setValue(it.toString())
                mProgressDialog.dismiss()
                Toast.makeText(this, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }
    }


    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this,R.style.MyDialogTheme)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv= mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

}