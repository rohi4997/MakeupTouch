package com.rohit.asquare
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceIdReceiver
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal
import com.google.firebase.messaging.FirebaseMessaging
import com.rohit.asquare.activities.NoInternetAvailable
import com.rohit.asquare.authentication.GoogleSignIN

class SplashScreen : AppCompatActivity()
{    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
    getNotificationToken()

    if (isConnectionAvailable(this)){
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this,GoogleSignIN::class.java)
                startActivity(i)
                finish()},2200)
    }else{
        Handler(Looper.getMainLooper()).postDelayed(
            {
                val i = Intent(this, NoInternetAvailable::class.java)
                startActivity(i)
                finish()},2200)
    }

//        Handler(Looper.getMainLooper()).postDelayed(
//            {
//                val i = Intent(this,GoogleSignIN::class.java)
//                startActivity(i)
//                finish()},2200)
    }

    private fun getNotificationToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("Token:", task.result)
            }else{
//                Log.d("Token:", "Failed")
            }
        })
    }


    private fun isConnectionAvailable(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
}
