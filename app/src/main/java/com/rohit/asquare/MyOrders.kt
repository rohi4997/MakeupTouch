package com.rohit.asquare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rohit.asquare.adapter.MyOrdersAdapter
import com.rohit.asquare.adapter.TodayOrdersAdapter
import com.rohit.asquare.data.CategoryOneData
import com.rohit.asquare.data.TodayOrdersData

class MyOrders : AppCompatActivity() {
    private  lateinit var dbref: DatabaseReference
    private lateinit var categoryOneRecycler: RecyclerView
    private lateinit var auth: FirebaseAuth
    private lateinit var itemArrayList: ArrayList<TodayOrdersData>
    private lateinit var categoryOneAdapter: TodayOrdersAdapter
    private lateinit var uid:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)

        auth = FirebaseAuth.getInstance()
        val splittedlist = auth.currentUser?.email.toString().split(".")
        uid = splittedlist[0]

        itemArrayList= arrayListOf<TodayOrdersData>()
        categoryOneAdapter= TodayOrdersAdapter(this,itemArrayList,this@MyOrders)


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
        dbref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("userOrderHistory")

        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val item = userSnapshot.getValue(TodayOrdersData::class.java)
                        itemArrayList.add(item!!)
                    }
                    categoryOneRecycler.adapter= categoryOneAdapter
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MyOrders, "", Toast.LENGTH_SHORT)
            }
        })
    }

//    override fun onItemClick(position: Int) {
//        val clickedItem = itemArrayList[position]
//        Toast.makeText(this,clickedItem.title, Toast.LENGTH_SHORT).show()
//        val splittedlist = auth.currentUser?.email.toString().split(".")
//        val uid=splittedlist[0]
//        dbref = FirebaseDatabase.getInstance().getReference()
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("name").setValue(clickedItem.title.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("price").setValue(clickedItem.price.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("description").setValue(clickedItem.description.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("image").setValue(clickedItem.image.toString())
//
//
//    }
}