package com.rohit.asquare.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.rohit.asquare.R

class ContactUs : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_us)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}