package com.rohit.asquare.adminpanel

import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.rohit.asquare.R
import com.rohit.asquare.authentication.GoogleSignIN
import com.rohit.asquare.data.TodayOrdersData
import com.rohit.asquare.fragments.CartFragment
import com.rohit.asquare.fragments.HomeFragment
import com.rohit.asquare.fragments.MoreFragment
import com.rohit.asquare.fragments.ProfileFragment
import com.rohit.asquare.services.MyNotificationListenerService

class AdminPanel : AppCompatActivity() {
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    lateinit var mProgressDialog:Dialog
    private lateinit var intent1:Intent
    private  lateinit var dbref: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_panel)
        showProgressDialog("Loading DashBoard...")

        val deleteProducts= findViewById<ImageView>(R.id.deleteProducts)
        val addProducts= findViewById<ImageView>(R.id.addProducts)
        val allUsers= findViewById<ImageView>(R.id.allUsers)
        val deletePackages= findViewById<ImageView>(R.id.deletePackages)
        val addPackages= findViewById<ImageView>(R.id.addPackages)
        val todayOrders= findViewById<ImageView>(R.id.todayOrders)
        val allOrders= findViewById<ImageView>(R.id.allOrders)
        val registeredUsers= findViewById<ImageView>(R.id.registeredUsers)
        val tvUsrCount= findViewById<TextView>(R.id.usrCount)
        val tvProdCount= findViewById<TextView>(R.id.prodCount)
        val tvRegCount= findViewById<TextView>(R.id.regCount)

        createNotificationChannel()
        auth = FirebaseAuth.getInstance()
        initializeClient()
        dbref = FirebaseDatabase.getInstance().getReference()
        val userRef = dbref.child("users")
        val userRegRef = dbref.child("registrations")
        val purchasedProdRef = dbref.child("allorders")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // User Count
                tvUsrCount.text=snapshot.childrenCount.toString()
//                sendNotification("Hello Admin", "You have a new application user\n${snapshot.child("name").value.toString()}")
            }
            override fun onCancelled(error: DatabaseError) {
//                mProgressDialog.dismiss()
            }
        })

        userRegRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                registrationCount
                tvRegCount.text=snapshot.childrenCount.toString()
//                sendNotification("Hello Admin", "You have a new package registration\n" + "${snapshot.child("name").value.toString()}")
            }
            override fun onCancelled(error: DatabaseError) {
//                mProgressDialog.dismiss()
            }
        })

        purchasedProdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
//                registrationCount
                tvProdCount.text=snapshot.childrenCount.toString()
//                sendNotification("Hello Admin", "You have a product order\n" + "${snapshot.child("title").value.toString()}")
                mProgressDialog.dismiss()
            }
            override fun onCancelled(error: DatabaseError) {
                mProgressDialog.dismiss()
            }
        })



        //Top ToolBar
        val toolbar = findViewById<Toolbar>(R.id.toolbar1)
        toolbar.setTitle("A SQUARE FITNESS CENTER")
        toolbar.setTitleTextColor(Color.WHITE)
        setSupportActionBar(toolbar)
        toolbar.setNavigationIcon(R.drawable.person_24)


        deleteProducts.setOnClickListener {
            val intent = Intent(this,DeleteProduct::class.java)
            startActivity(intent)
        }
        addProducts.setOnClickListener {
            val intent = Intent(this,AddProduct::class.java)
            startActivity(intent)
        }
        allUsers.setOnClickListener {
            val intent = Intent(this,AllUsers::class.java)
            startActivity(intent)
        }

        deletePackages.setOnClickListener {
            val intent = Intent(this,DeletePackage::class.java)
            startActivity(intent)
        }
        addPackages.setOnClickListener {
            val intent = Intent(this,AddPackage::class.java)
            startActivity(intent)
        }
        todayOrders.setOnClickListener {
            val intent = Intent(this,TodayOrders::class.java)
            startActivity(intent)
        }
        allOrders.setOnClickListener {
            val intent = Intent(this,AllOrders::class.java)
            startActivity(intent)
        }
        registeredUsers.setOnClickListener {
            val intent = Intent(this,UserRegistrations::class.java)
            startActivity(intent)
        }
        tvUsrCount.setOnClickListener {
            val intent = Intent(this,AllUsers::class.java)
            startActivity(intent)
        }
        tvProdCount.setOnClickListener {
            val intent = Intent(this,AllOrders::class.java)
            startActivity(intent)
        }
        tvRegCount.setOnClickListener {
            val intent = Intent(this,UserRegistrations::class.java)
            startActivity(intent)
        }

        intent1 = Intent(this, MyNotificationListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

//            ContextCompat.startForegroundService(this,intent1)
            this.startService(intent1)
        }else{
            this.startService(intent1)
        }
    }

    override fun onDestroy() {
//        this.stopService(intent1)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar,menu)
        return true
    }

    //option Menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id:Int =item.itemId
        if(id==R.id.logout){
            signOut()
            val intnt = Intent(this, GoogleSignIN::class.java)
            startActivity(intnt)
            finish()
            Toast.makeText(applicationContext,"Logout SuccessFul", Toast.LENGTH_LONG).show()}
        return super.onOptionsItemSelected(item)
    }

    fun signOut() {
        auth.signOut()
        googleSignInClient.signOut()
    }

    private fun initializeClient() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv= mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    private fun sendNotification(title: String,content: String){

        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.cart_24)        //Glide.with(this).load(bundle?.get("addr").toString()).override(150, 150)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(
                NotificationCompat.BigTextStyle()
                .bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(this)){
            notify(202,builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Chanel Name"
            val descriptionText = "Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}