package com.rohit.asquare

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rohit.asquare.data.MyCartItems

class PaymentActivity : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var itemArrayList1: ArrayList<MyCartItems>
    private lateinit var itemArrayList: ArrayList<MyCartItems>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        auth = FirebaseAuth.getInstance()
        val splittedlist = auth.currentUser?.email.toString().split(".")
        uid = splittedlist[0]
        itemArrayList = arrayListOf<MyCartItems>()
        itemArrayList1 = arrayListOf<MyCartItems>()

        val btnDeliver = findViewById<Button>(R.id.deliverr)
        btnDeliver.setOnClickListener {
            goPurchase()
        }
    }


    private fun goPurchase() {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder
            .setMessage("Select The Payment Method")
            .setCancelable(true)
        // .setMessage("this is alert")
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(Color.WHITE)
        layout.setPadding(15, 0, 15, 0)
        val cashOnDelivery = Button(this)
        val payOnline = Button(this)
        cashOnDelivery.text = "Cash On Delivery"
        payOnline.text = "Pay Online Using UPI"
        layout.addView(cashOnDelivery)
        layout.addView(payOnline)
        builder.setView(layout)

        payOnline.setOnClickListener {
            initPayment()
        }
        cashOnDelivery.setOnClickListener {
            //Toast.makeText(this, "Cashondelivery", Toast.LENGTH_SHORT).show()
            //builder.setCancelable(true)
            clearCartAddHistory()
            successDialog()

        }

        builder.setPositiveButton("Cancel") { dialogInterface, which ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun initPayment() {
        val uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", "8875565063@ybl")
            .appendQueryParameter("pn", "A Square Gym")
            .appendQueryParameter("tn", "Purchased from Asquare")
            .appendQueryParameter("am", "100")
            .appendQueryParameter("cn", "INR").build()

        val upiIntent = Intent(Intent.ACTION_VIEW)
        upiIntent.setData(uri)
        val chooser = Intent.createChooser(upiIntent, "Pay With");
        if (this?.let { chooser.resolveActivity(it.packageManager) } != null) {
            startActivityForResult(chooser, 100)
        } else {
            Toast.makeText(this, "No paying Apps", Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(context,"In Init Payment",Toast.LENGTH_SHORT).show()
    }

    private fun upiPaymentDataOperation(datalist: ArrayList<String>) {
        if (isConnectionAvailabe(this)) {

            var str: String = datalist.get(0)
            Log.d("UPI", "upiPaymentDataOperation " + str)
            var paymentCancel: String = ""
            if (str == null) str = "discard"
            var status: String = ""
            var approvalRefNo: String = ""
            val response: List<String> = str.split("&")
            for (i in response) {
                val equalStr: List<String> = str.split("=")
                if (equalStr.size >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) status =
                        equalStr[1].toLowerCase()
                    else if (equalStr[0].toLowerCase()
                            .equals("ApprovalRefNo".toLowerCase()) || equalStr[0].toLowerCase()
                            .equals("txnRef".toLowerCase())
                    ) approvalRefNo = equalStr[1]
                } else paymentCancel = "payment cancelled by user."
            }

            if (status.equals("success")) {
                Log.d("UPI", "responseStr: " + approvalRefNo)
                clearCartAddHistory()
                successDialog()

            } else if (paymentCancel.equals("payment cancelled by user.")) {
                Toast.makeText(this, "payment cancelled by user.", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Transaction Failed. Plaease Try later", Toast.LENGTH_SHORT)
                    .show()
            }
        } else {
            Toast.makeText(this, "No InterNet Connection", Toast.LENGTH_SHORT).show()
        }


    }

    private fun isConnectionAvailabe(context: Context?): Boolean {
        //val connectivityManager:ConnectivityManager=ConnectivityManager.getSystemService(Context.CONNECTIVITY_SERVICE)
        return true
    }

    private fun successDialog() {
        val builder = AlertDialog.Builder(this)
        builder
            .setMessage("Order Successfully Placed")
            .setCancelable(false)
// .setMessage("this is alert")
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setBackgroundColor(Color.WHITE)
        layout.setPadding(15, 0, 15, 0)
        val imgsuccess = pl.droidsonroids.gif.GifImageView(this)
        imgsuccess.setImageResource(R.drawable.successcircle)
        val continueShopping = Button(this)
        continueShopping.text = "Continue To Home"
        val trackOrder = Button(this)
        trackOrder.text = "Track Order"
        layout.addView(imgsuccess)
        layout.addView(continueShopping)
        layout.addView(trackOrder)
        builder.setView(layout)

        continueShopping.setOnClickListener {
            builder.setCancelable(true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
        trackOrder.setOnClickListener {
            builder.setCancelable(true)
            val intent = Intent(this, OrderDetailsStatus::class.java)
            startActivity(intent)
            finish()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode){
            100->
                if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
                    if (data!=null)
                    {
                        //Toast.makeText(context,data.toString(),Toast.LENGTH_SHORT).show()
                        val trxt=data.getStringExtra("response");
                        Log.d("UPI","onActivityResult"+trxt)
                        val datalist= ArrayList<String> ()
                        datalist.add(trxt.toString())
                        upiPaymentDataOperation(datalist)
                    }else
                    {
                        Log.d("UPI","onActivityResult"+"Return data is null")
                        val datalist= ArrayList<String> ()
                        datalist.add("nothing")
                        upiPaymentDataOperation(datalist)
                    }
                }
                else
                {
                    Log.d("UPI","onActivityResult"+"Return data is null")
                    val datalist= ArrayList<String> ()
                    datalist.add("nothing")
                    upiPaymentDataOperation(datalist)
                }
        }

    }

    private fun clearCartAddHistory(){
        //set cartItems into myOrder History
        dbref = FirebaseDatabase.getInstance().getReference()
        val snapshot1 = dbref.child("users").child(uid).child("usercart").ref
        snapshot1.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val item = userSnapshot.getValue(MyCartItems::class.java)
                        itemArrayList1.add(item!!)
                    }
                    val orderId = dbref.child("users").child(uid).child("userOrdersHistory")
                        .push().key!!
                    //val orderRef=dbref.child("users").child(uid).child("userOrdersHistory").child(orderId).setValue(itemArrayList1)
                    dbref.child("users").child(uid).child("userOrdersHistory")
                        .setValue(itemArrayList1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

        //clear cart
        dbref = FirebaseDatabase.getInstance().getReference()
        dbref.child("users").child(uid).child("usercart").setValue(null)
        itemArrayList.removeAll(itemArrayList)
//                cartAdapter.notifyDataSetChanged()
//                cartRecycler.adapter = cartAdapter
        //Toast.makeText(this, "Transaction Successful", Toast.LENGTH_SHORT).show()
        //Log.d("UPI", "responseStr: " + approvalRefNo)
    }
}


//
//val builder = AlertDialog.Builder(this)
//builder
//.setMessage("Order Successfully Placed")
//.setCancelable(false)
//// .setMessage("this is alert")
//val layout = LinearLayout(this)
//layout.orientation= LinearLayout.VERTICAL
//layout.setBackgroundColor(Color.WHITE)
//layout.setPadding(15,0,15,0)
//val imgsuccess = pl.droidsonroids.gif.GifImageView(this)
//imgsuccess.setImageResource(R.drawable.successcircle)
//val continueShopping = Button(this)
//continueShopping.text="Continue To Home"
//layout.addView(imgsuccess)
//layout.addView(continueShopping)
//builder.setView(layout)
//
//continueShopping.setOnClickListener {
//
//    builder.setCancelable(true)
//    val intent = Intent(this,MainActivity::class.java)
//    startActivity(intent)
//    finishAffinity()
//}
//val alertDialog: AlertDialog = builder.create()
//alertDialog.show()