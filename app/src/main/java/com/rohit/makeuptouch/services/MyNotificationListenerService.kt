package com.rohit.makeuptouch.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.*
import com.rohit.makeuptouch.adminpanel.AdminPanel
import com.rohit.makeuptouch.R

public class MyNotificationListenerService : Service()
{
    private  lateinit var dbref: DatabaseReference
//    private lateinit var builder:Notification

    override fun onCreate() {
//        stopForeground(true)
//        stopSelf()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        createNotificationChannel()

        val callbackIntent = Intent(this, AdminPanel::class.java)
        val pendingIntent = PendingIntent.getActivity(this,0,callbackIntent,0)

        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.cart_24)        //Glide.with(this).load(bundle?.get("addr").toString()).override(150, 150)
            .setContentTitle("title")
            .setContentText("content")
            .setStyle(NotificationCompat.BigTextStyle().bigText("content"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent).build()


        startForeground(0,builder)

        dbref = FirebaseDatabase.getInstance().getReference()
        val userRef = dbref.child("users")
        val userRegRef = dbref.child("registrations")
        val purchasedProdRef = dbref.child("allorders")

        System.out.println("zxc")
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sendNotification("Hello Admin", "You have a new application user\n${snapshot.child("name").value.toString()}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        userRegRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sendNotification("Hello Admin", "You have a new package registration\n" + "${snapshot.child("name").value.toString()}")
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })

        purchasedProdRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                sendNotification("Hello Admin", "You have a product order\n" + "${snapshot.child("title").value.toString()}")
            }
            override fun onCancelled(error: DatabaseError) {
//                Toast.makeText(this@MyNotificationListenerService,error.code,Toast.LENGTH_SHORT).show()
            }
        })

        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
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
            val channel = NotificationChannel("CHANNEL_ID", "Chanel Name", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Description"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        stopForeground(true)
        stopSelf()
        super.onDestroy()
    }
}