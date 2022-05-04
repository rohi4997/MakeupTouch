package com.rohit.asquare
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView

class RatingBar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.rating_bar)
        val clean = findViewById<RatingBar>(R.id.cleanliness)
        val food = findViewById<RatingBar>(R.id.food)
        val time = findViewById<RatingBar>(R.id.time)
        val other = findViewById<RatingBar>(R.id.other)
        val total = findViewById<RatingBar>(R.id.total)
        val submit = findViewById<Button>(R.id.button)
        val result = findViewById<TextView>(R.id.result)

        submit.setOnClickListener {
            val c_rating = clean.rating
            val f_rating = food.rating
            val t_rating= time.rating
            val o_rating= other.rating
            var total_rating=(c_rating+f_rating+t_rating+o_rating)/4
            total.rating=total_rating
            result.text=total_rating.toString()
        }
}}