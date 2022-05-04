package com.rohit.asquare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.rohit.asquare.R

class AddAddress : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_address)
        val bundle:Bundle? = intent.extras
        val saveBtn= findViewById<Button>(R.id.save_btn)
        val nameUser= findViewById<EditText>(R.id.name)
        val mob= findViewById<EditText>(R.id.mobile_no)
        val village= findViewById<EditText>(R.id.village)
        val back= findViewById<ImageView>(R.id.back)
        back.setOnClickListener { onBackPressed() }

        //val addr:String="${nameUser.text.toString()} ${mob.text.toString()} ${village.text.toString()}"

        saveBtn.setOnClickListener {
            if (nameUser.text.toString().isEmpty() || mob.text.toString().isEmpty() || village.text.toString().isEmpty()){
                //empty
                Toast.makeText(this,"Please Fill the full address", Toast.LENGTH_SHORT).show()
            }else{
                val addr="Name:" +nameUser.text.toString()+" Mobile:" +mob.text.toString()+ " Village:" +village.text.toString()
                //Toast.makeText(this,addr, Toast.LENGTH_SHORT).show()
                val intent = Intent(this,PaymentSingleItem::class.java)
                intent.putExtra("name",bundle?.get("name").toString())
                intent.putExtra("price",bundle?.get("price").toString())
                intent.putExtra("detail",bundle?.get("detail").toString())
                intent.putExtra("image",bundle?.get("image").toString())
                intent.putExtra("Name",nameUser.text.toString())
                intent.putExtra("Village",village.text.toString())
                intent.putExtra("Mobile",mob.text.toString())
                intent.putExtra("addr",addr)
                startActivity(intent)
                finish()
            }
        }
    }
}