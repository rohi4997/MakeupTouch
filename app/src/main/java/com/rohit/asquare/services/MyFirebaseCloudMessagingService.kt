package com.rohit.asquare.services

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseCloudMessagingService : FirebaseMessagingService()  {
    //triggered when the app is running
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d("Title:"+ message.notification!!.title ," Body:"+message.notification!!.body)
    }

}