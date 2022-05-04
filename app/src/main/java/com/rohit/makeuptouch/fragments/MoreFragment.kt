package com.rohit.makeuptouch.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.rohit.makeuptouch.MyOrders
import com.rohit.makeuptouch.R
import com.rohit.makeuptouch.activities.AboutUs
import com.rohit.makeuptouch.activities.ContactUs
import com.rohit.makeuptouch.activities.TermsConditions
import java.util.*

class MoreFragment : Fragment() {

    var auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v= inflater.inflate(R.layout.fragment_more, container, false)
        val userName= v.findViewById<TextView>(R.id.userName)
        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        userName.text=user?.displayName

        val myOrders=v.findViewById<TextView>(R.id.myOrders)
        val contactUs=v.findViewById<TextView>(R.id.contactUs)
        val aboutUs=v.findViewById<TextView>(R.id.aboutUs)
        val rateUs=v.findViewById<TextView>(R.id.rateUs)
        val shareApp=v.findViewById<TextView>(R.id.shareApp)
        val faQ=v.findViewById<TextView>(R.id.faQ)
        val termsCond=v.findViewById<TextView>(R.id.termsCond)


        myOrders.setOnClickListener {
            val intent = Intent(this.context,MyOrders::class.java)
            startActivity(intent)
        }
        contactUs.setOnClickListener {
            val intent = Intent(this.context,ContactUs::class.java)
            startActivity(intent)
        }
        aboutUs.setOnClickListener {
            val intent = Intent(this.context,AboutUs::class.java)
            startActivity(intent)
        }
        rateUs.setOnClickListener {
            val uri: Uri = Uri.parse("http://play.google.com/") // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
        faQ.setOnClickListener {
            val uri: String = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", 25.16625770163453, 75.32919419626366)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }
        termsCond.setOnClickListener {
            val intent = Intent(this.context,TermsConditions::class.java)
            startActivity(intent)
        }

        shareApp.setOnClickListener {
            val uri: Uri = Uri.parse("http://www.asquaregym.com") // missing 'http://' will cause crashed
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        return v
    }

}