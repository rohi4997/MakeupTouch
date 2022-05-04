//package com.rohit.asquare.services
//
//import android.app.Dialog
//import android.provider.Settings.Global.getString
//import com.google.android.gms.auth.api.signin.GoogleSignIn
//import com.google.android.gms.auth.api.signin.GoogleSignInClient
//import com.google.android.gms.auth.api.signin.GoogleSignInOptions
//import com.google.firebase.auth.FirebaseAuth
//import com.rohit.asquare.R
//
//
//object AuthService {
//    private lateinit var googleSignInClient: GoogleSignInClient
//    private lateinit var auth: FirebaseAuth
//    private const val RC_SIGN_IN = 100
//    private const val TAG = "GOOGLE_SIGN_IN_TAG"
//
//
//    var instance: AuthService? = null
//        get() {
//            if (field == null) {
//                synchronized(AuthService::class.java) {
//                    if (field == null) {
//                        field = AuthService
//                        auth = FirebaseAuth.getInstance()
//                        initializeClient()
//                    }
//                }
//            }
//            return field
//        }
//        private set
//
//    fun signIn() {
//
//    }
//
//    fun signOut() {
//
//    }
//
//    private fun initializeClient() {
//        // Configure Google Sign In
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        //googleSignInClient = GoogleSignIn.getClient(this, gso)
//    }
//}