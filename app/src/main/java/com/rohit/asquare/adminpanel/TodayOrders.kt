package com.rohit.asquare.adminpanel

import android.app.Dialog
import android.app.job.JobScheduler
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rohit.asquare.R
import com.rohit.asquare.adapter.CartAdapter
import com.rohit.asquare.adapter.CategoryOneAdapter
import com.rohit.asquare.adapter.TodayOrdersAdapter
import com.rohit.asquare.adapter.UserAdapter
import com.rohit.asquare.data.MyCartItems
import com.rohit.asquare.data.TodayOrdersData
import com.rohit.asquare.data.User

class TodayOrders : AppCompatActivity() {
    private  lateinit var dbref: DatabaseReference
    private lateinit var categoryOneRecycler: RecyclerView
    private lateinit var itemArrayList: ArrayList<TodayOrdersData>
    private lateinit var categoryOneAdapter: TodayOrdersAdapter
    lateinit var mProgressDialog: Dialog
//    var jobScheduler: JobScheduler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.adminpanel_today_orders)



        showProgressDialog("Loading...")
        itemArrayList= arrayListOf()
        categoryOneAdapter= TodayOrdersAdapter(this,itemArrayList,this@TodayOrders)

        val back = findViewById<ImageView>(R.id.back)
        back.setOnClickListener {
            onBackPressed()
        }

        //Recycler View1
        categoryOneRecycler=findViewById(R.id.categoryOneRecycler)
        categoryOneRecycler.layoutManager= LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        getItemData()
    }

    private fun getItemData() {
        dbref = FirebaseDatabase.getInstance().getReference("todayorders")

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val item = userSnapshot.getValue(TodayOrdersData::class.java)
                        itemArrayList.add(item!!)
                    }
                    categoryOneRecycler.adapter= categoryOneAdapter
                    mProgressDialog.dismiss()
                }else{
                    mProgressDialog.dismiss()
                }
            }
            override fun onCancelled(error: DatabaseError) {
                mProgressDialog.dismiss()
            }
        })
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
}