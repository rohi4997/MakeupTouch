package com.rohit.asquare.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.rohit.asquare.R

class TermsConditions : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.terms_conditions)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }
    }
}