package com.rohit.makeuptouch

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*

class JoiningForm : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var nameUser: EditText
    private lateinit var bundle: Bundle
    private lateinit var locality: EditText
    private lateinit var mobile :EditText
    lateinit var mProgressDialog: Dialog
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_joining_form)

        val imgUser = findViewById<ImageView>(R.id.imgUser)
        val backBtn = findViewById<ImageView>(R.id.back)
        val saveBtn = findViewById<Button>(R.id.save_btn)
        nameUser = findViewById(R.id.nameUser)
        locality = findViewById(R.id.village)
        mobile = findViewById(R.id.mobile_no)
        val packageName = findViewById<EditText>(R.id.packageName)
        auth = FirebaseAuth.getInstance()
        bundle = intent.extras!!
        packageName.setText(bundle?.get("name").toString())
        Glide.with(this).load(auth.currentUser?.photoUrl.toString()).into(imgUser)
        backBtn.setOnClickListener {
            onBackPressed()
        }

        saveBtn.setOnClickListener {
            if (nameUser.text.toString()=="" || locality.text.toString()=="" || mobile.text.toString()==""){
                Toast.makeText(this, "Please Fill all details", Toast.LENGTH_SHORT).show()
            }
            else{
                showProgressDialog("Registering")
                firebaseApiCallUpload()
            }
        }
    }

    private fun firebaseApiCallUpload(){
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy 'time ' HH:mm:ss ")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        dbref = FirebaseDatabase.getInstance().getReference("registrations")
        val uniqueKey = dbref.push().key!!
        dbref.child(uniqueKey).child("name").setValue(nameUser.text.toString() + " (Package: "+bundle?.get("name").toString()+")")
        dbref.child(uniqueKey).child("email").setValue(locality.text.toString()+ "\nDate: "+currentDateAndTime)
        dbref.child(uniqueKey).child("image").setValue(auth.currentUser?.photoUrl.toString())
        dbref.child(uniqueKey).child("phone").setValue(mobile.text.toString()).addOnSuccessListener {
            mProgressDialog.dismiss()
            successDialog()
        }
    }

    private fun successDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("You Are Successfully Registered\nCome to gym for timing and more details")
            .setCancelable(false)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(Color.WHITE)
        layout.setPadding(15, 0, 15, 0)
        val imgsuccess = pl.droidsonroids.gif.GifImageView(this)
        imgsuccess.setImageResource(R.drawable.successcircle)
        val continueShopping = Button(this)
        continueShopping.text = "Continue To Home"
        layout.addView(imgsuccess)
        layout.addView(continueShopping)
        builder.setView(layout)

        continueShopping.setOnClickListener {
            builder.setCancelable(true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
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