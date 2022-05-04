package com.rohit.makeuptouch

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class PaymentSingleItem : AppCompatActivity() {
    private lateinit var dbref: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var uid: String
    private lateinit var bundle: Bundle
    private lateinit var myText: String
    lateinit var mProgressDialog:Dialog
    var GOOGLE_PAY_PACKAGE_NAME:kotlin.String? = "com.google.android.apps.nbu.paisa.user"
    var GOOGLE_PAY_REQUEST_CODE = 123
    //private lateinit var address: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.payment_single_item)
        createNotificationChannel()
        auth = FirebaseAuth.getInstance()
        val splittedlist = auth.currentUser?.email.toString().split(".")
        uid = splittedlist[0]
        bundle = intent.extras!!
        myText="Hi Admin, \n"+
                "This Is ${bundle?.get("Name").toString()}\n" +
                "I have ordered a product ${bundle?.get("name").toString()} from your app\n"+
                "Price: ${bundle?.get("price").toString()}\n" +
                "Address: ${bundle?.get("Village").toString()}\n"+
                "Mobile: ${bundle?.get("Mobile").toString()}\n"
//                "${bundle?.get("addr").toString()}"


        val nme= findViewById<TextView>(R.id.name)
        val vlg= findViewById<TextView>(R.id.vlg)
        val mobl= findViewById<TextView>(R.id.mobl)
        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener { onBackPressed() }
        nme.text=bundle?.get("Name").toString()
        //nme.setText(bundle?.get("Name").toString())
        vlg.setText(bundle?.get("Village").toString())
        mobl.setText(bundle?.get("Mobile").toString())

        val btnDeliver = findViewById<Button>(R.id.deliverr)
        btnDeliver.setOnClickListener {
            dialogPaymentOpt()
        }
    }


    private fun dialogPaymentOpt(){
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder
            .setMessage("Select Payment Method")
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
            //clearCartAddHistory()
            areYouSureDialog()


        }

        builder.setPositiveButton("Cancel") { dialogInterface, which ->
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun areYouSureDialog() {
        val builder = AlertDialog.Builder(this, R.style.MyDialogTheme)
        builder
            .setMessage("Are You Sure You Want to place This Order\nYou will get a call from our delivery partner")
            .setCancelable(false)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, which ->
            //Toast.makeText(applicationContext, "clicked yes", Toast.LENGTH_LONG).show()
            todayOrders()
            addHistory()
            addToAllOrders()

//            initMsg()

        }

        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, which ->
            Toast.makeText(applicationContext, "Cancelled", Toast.LENGTH_LONG).show()
        }

        val alertDialog:AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun initMsg() {
        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)==PackageManager.PERMISSION_GRANTED){
            //premission is here
//            sendMsg()
            successDialog()
        }else{
            //request for permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS),100)
        }
    }

//    private fun sendMsg() {
//        var obj = SmsManager.getDefault()
//        obj.sendTextMessage("8875565063",null,myText, null,null)
//    }

    fun initPayment() {
        val uri:Uri = Uri.Builder()
            .scheme("upi")
            .authority("pay")
            .appendQueryParameter("pa", "8875565063@okbizaxis")      //7792074454@ybl    //8875565063@okbizaxis        //8875565063@ybl
            .appendQueryParameter("pn", "A Square Gym And Fitness Center")                         //A Square Gym And Fitness Center
            .appendQueryParameter("mc", "BCR2DN4TZCX3F4QJ")                                  //BCR2DN4TZCX3F4QJ
            .appendQueryParameter("tr", "qwertyuioplkjhgfdsazxcvbnm")
            .appendQueryParameter("tn", myText)
            .appendQueryParameter("am", "bundle?.get(\"price\").toString()")   //bundle?.get("price").toString()
            .appendQueryParameter("cu", "INR").build()

        val upiIntent = Intent(Intent.ACTION_VIEW)
        upiIntent.setData(uri)
        //upiIntent.setPackage("com.google.android.apps.nbu.paisa.user")
        val chooser = Intent.createChooser(upiIntent, "Pay With");
        if (this?.let { chooser.resolveActivity(it.packageManager) } != null) {
            startActivityForResult(chooser, 100)
        } else {
            Toast.makeText(this, "No paying Apps", Toast.LENGTH_SHORT).show()
        }
        //Toast.makeText(context,"In Init Payment",Toast.LENGTH_SHORT).show()
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
                myText=myText+"DONE"
//                initMsg()
                addHistory()
                addToAllOrders()
                successDialog()

            } else if (paymentCancel.equals("payment cancelled by user.")) {
                Toast.makeText(this, "payment cancelled", Toast.LENGTH_SHORT).show()
//                addHistory()

            } else {
                Toast.makeText(this, "Transaction Failed. Plaease Try later", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No InterNet Connection", Toast.LENGTH_SHORT).show()
        }


    }

    private fun addHistory(){
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy 'time ' HH:mm:ss ")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        dbref = FirebaseDatabase.getInstance().getReference("users")
        val uniqueKey = dbref.push().key!!
        dbref.child(uid).child("userOrderHistory").child(uniqueKey).child("name").setValue(bundle?.get("name").toString())
        dbref.child(uid).child("userOrderHistory").child(uniqueKey).child("price").setValue(bundle?.get("price").toString())
        dbref.child(uid).child("userOrderHistory").child(uniqueKey).child("description").setValue(bundle?.get("detail").toString())
        dbref.child(uid).child("userOrderHistory").child(uniqueKey).child("image").setValue(bundle?.get("image").toString())
        dbref.child(uid).child("userOrderHistory").child(uniqueKey).child("deliveryAddress").setValue("Address: "+bundle?.get("addr").toString()+ "\nDate:" +currentDateAndTime)

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
        //val trackOrder = Button(this)
        //trackOrder.text = "Track Order"
        layout.addView(imgsuccess)
        layout.addView(continueShopping)
        //layout.addView(trackOrder)
        builder.setView(layout)

        continueShopping.setOnClickListener {
            builder.setCancelable(true)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
//        trackOrder.setOnClickListener {
//            builder.setCancelable(true)
//            val intent = Intent(this, OrderDetailsStatus::class.java)
//            startActivity(intent)
//            finish()
//        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==100 && grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
            //when permission is granted
            //sendMessage
//            sendMsg()
            successDialog()

        }else{
            //when permission is denied
            Toast.makeText(this,"Please Allow The Permission in Settings",Toast.LENGTH_SHORT).show()
        }
    }

    private fun mailIt(){

//        val mail = SendMail(
//            "sender_mail@gmail.com", "sender_pass",
//            "papayacoders@gmail.com",
//            "Testing Email Sending",
//            "Yes, it's working well\nI will use it always."
//        )
//        mail.execute()
    }

    private fun todayOrders(){
        showProgressDialog("Placing Order")
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy 'time ' HH:mm:ss ")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        dbref = FirebaseDatabase.getInstance().getReference("todayorders")
        val uniqueKey = dbref.push().key!!
        dbref.child(uniqueKey).child("name").setValue(bundle?.get("name").toString())
        dbref.child(uniqueKey).child("price").setValue(bundle?.get("price").toString())
        dbref.child(uniqueKey).child("description").setValue(bundle?.get("detail").toString())
        dbref.child(uniqueKey).child("image").setValue(bundle?.get("image").toString())
        dbref.child(uniqueKey).child("deliveryAddress").setValue(bundle?.get("addr").toString() + " Date:" +currentDateAndTime).addOnSuccessListener {
            mProgressDialog.dismiss()
            sendNotification()
            successDialog()
        }
        mProgressDialog.dismiss()
    }

    private fun addToAllOrders(){
//        showProgressDialog("Placing Order")
        val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy 'time ' HH:mm:ss ")
        val currentDateAndTime: String = simpleDateFormat.format(Date())
        dbref = FirebaseDatabase.getInstance().getReference("allorders")
        val uniqueKey = dbref.push().key!!
        dbref.child(uniqueKey).child("name").setValue(bundle?.get("name").toString())
        dbref.child(uniqueKey).child("price").setValue(bundle?.get("price").toString())
        dbref.child(uniqueKey).child("description").setValue(bundle?.get("detail").toString())
        dbref.child(uniqueKey).child("image").setValue(bundle?.get("image").toString())
        dbref.child(uniqueKey).child("deliveryAddress").setValue(bundle?.get("addr").toString() + " Date:" +currentDateAndTime)
    }
    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this,R.style.MyDialogTheme)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv= mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

    private fun sendNotification(){

        var builder = NotificationCompat.Builder(this, "CHANNEL_ID")
            .setSmallIcon(R.drawable.cart_24)        //Glide.with(this).load(bundle?.get("addr").toString()).override(150, 150)
            .setContentTitle("Order Placed")
            .setContentText(bundle?.get("name").toString())
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(bundle?.get("name").toString()))
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