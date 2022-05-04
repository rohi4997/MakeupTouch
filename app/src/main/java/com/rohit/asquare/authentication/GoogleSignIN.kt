package com.rohit.asquare.authentication

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.InputType.TYPE_CLASS_NUMBER
import android.util.Log
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.rohit.asquare.MainActivity
import com.rohit.asquare.R
import com.rohit.asquare.adminpanel.AdminPanel


open class GoogleSignIN : AppCompatActivity() {
    private  lateinit var dbref: DatabaseReference
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    lateinit var mProgressDialog:Dialog
    lateinit var phone:String

    private companion object {
        private const val RC_SIGN_IN = 100
        private const val TAG = "GOOGLE_SIGN_IN_TAG"
    }
    private lateinit var googleSignInBtn: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.google_sign_in)
        dbref = Firebase.database.reference
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser!=null ){
            if (auth.currentUser!!.email=="rohit.dhaker@juspay.in" || auth.currentUser!!.email=="asquaregym5063@gmail.com"){
                val i = Intent(this, AdminPanel::class.java)
                startActivity(i)
                finish()
            }else{
                val i = Intent(this, MainActivity::class.java)
                startActivity(i)
                finish()
            }

        }
        googleSignInBtn=findViewById(R.id.navHeaderGoogleSignInBtn)
        initializeClient()

        googleSignInBtn.setOnClickListener {
            takePhone()

        }
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

    private fun startGoogleSignIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
        //Toast.makeText(this,"initalized",Toast.LENGTH_SHORT).show()
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {

                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser // user object
                    //storing in realtime database
                    val splittedlist = user?.email.toString().split(".")
                    val uid=splittedlist[0]
                    dbref.child("users").child(uid).child("name").setValue(user?.displayName)
                    dbref.child("users").child(uid).child("phone").setValue(phone)
                    dbref.child("users").child(uid).child("email").setValue(user?.email)
                    dbref.child("users").child(uid).child("image").setValue(user?.photoUrl.toString())

                    hideProgressDialog()
                    if (auth.currentUser!!.email=="rohit.dhaker@juspay.in"){
                        val i = Intent(this, AdminPanel::class.java)
                        startActivity(i)
                        finish()
                    }else{
                        val i = Intent(this, MainActivity::class.java)
                        startActivity(i)
                        finish()
                    }
                    //Toast.makeText(this, """Welcome ${user?.displayName}  """, Toast.LENGTH_LONG).show()
                    //takePhone()
                } else {
                    hideProgressDialog()
                    // If sign in fails, display a message to the user.
                    Toast.makeText(this, "Sign In Failed", Toast.LENGTH_LONG).show()
                }
            }
    }

    private  fun takePhone() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("+91")
            .setMessage("Enter Your Phone")
            .setCancelable(true)
            .setIcon(android.R.drawable.ic_menu_call)
        val input = EditText(this@GoogleSignIN)
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.hint="Enter Your Phone"
        input.inputType=TYPE_CLASS_NUMBER
        input.layoutParams = lp
        builder.setView(input)
        builder.setPositiveButton("Go") { dialogInterface, which ->
            if (input.text==null || input.text.isEmpty() || !(input.length()==10 || input.length()==12 || input.length()==13)){
                Toast.makeText(applicationContext, "Please Enter a valid Phone No", Toast.LENGTH_LONG).show()
            }else {
                phone=input.text.toString()
                startGoogleSignIn()
                showProgressDialog("Please Wait")
            }
        }

        val alertDialog:AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                hideProgressDialog()
            }
        }
    }

    fun showProgressDialog(text:String,){
        mProgressDialog=Dialog(this,R.style.DialogThemeWhite)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv= mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }
}