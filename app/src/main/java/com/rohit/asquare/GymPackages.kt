package com.rohit.asquare

import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.rohit.asquare.adapter.CategoryOneAdapter
import com.rohit.asquare.adapter.MyOrdersAdapter
import com.rohit.asquare.adapter.UserAdapter
import com.rohit.asquare.data.CategoryOneData
import com.rohit.asquare.data.User

class GymPackages : AppCompatActivity(), CategoryOneAdapter.OnClListener {
    private  lateinit var dbref: DatabaseReference
    private lateinit var categoryOneRecycler: RecyclerView
    private lateinit var itemArrayList: ArrayList<CategoryOneData>
    private lateinit var categoryOneAdapter: CategoryOneAdapter
    lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gym_packages)
        showProgressDialog("Loading...")
        itemArrayList= arrayListOf<CategoryOneData>()
        categoryOneAdapter= CategoryOneAdapter(this,itemArrayList,this@GymPackages)

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
        dbref = FirebaseDatabase.getInstance().getReference("packages")

        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(userSnapshot in snapshot.children){
                        val item = userSnapshot.getValue(CategoryOneData::class.java)
                        itemArrayList.add(item!!)
                    }
                    categoryOneRecycler.adapter= categoryOneAdapter
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

    override fun onItemClick(position: Int) {
//        val clickedItem = itemArrayList[position]
//        Toast.makeText(this,clickedItem.title,Toast.LENGTH_SHORT).show()
//        val splittedlist = auth.currentUser?.email.toString().split(".")
//        val uid=splittedlist[0]
//        dbref = FirebaseDatabase.getInstance().getReference()
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("name").setValue(clickedItem.title.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("price").setValue(clickedItem.price.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("description").setValue(clickedItem.description.toString())
//        dbref.child("users").child(uid).child("usercart").child(clickedItem.title.toString()).child("image").setValue(clickedItem.image.toString())
//        val clickedItem = itemArrayList[position]
//        val intent = Intent(this,ItemDetail::class.java)
//        intent.putExtra("name",clickedItem.title.toString())
//        intent.putExtra("price",clickedItem.price.toString())
//        intent.putExtra("detail",clickedItem.description)
//        intent.putExtra("image",clickedItem.image)
//        startActivity(intent)

    }

    override fun onButtonClick(position: Int) {
        val clickedItem = itemArrayList[position]
        val intent = Intent(this,JoiningForm::class.java)
        intent.putExtra("name",clickedItem.title.toString())
//        intent.putExtra("price",clickedItem.price.toString())
//        intent.putExtra("detail",clickedItem.description)
//        intent.putExtra("image",clickedItem.image)
        startActivity(intent)
        finish()
    }
}