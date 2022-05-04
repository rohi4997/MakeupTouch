package com.rohit.asquare.adminpanel

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.rohit.asquare.R
import com.rohit.asquare.adapter.CartAdapter
import com.rohit.asquare.data.MyCartItems

class DeletePackage : AppCompatActivity(), CartAdapter.OnClListener {
    private lateinit var dbref: DatabaseReference
    private lateinit var cartRecycler: RecyclerView
    private lateinit var itemArrayList: ArrayList<MyCartItems>
    private lateinit var cartAdapter: CartAdapter
    lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.delete_package_adminpanel)
        val backBtn = findViewById<ImageView>(R.id.back)
        showProgressDialog("Loading...")
        itemArrayList = arrayListOf()
        cartAdapter = CartAdapter(this@DeletePackage, itemArrayList, this@DeletePackage)

        //Recycler View1
        cartRecycler = findViewById(R.id.myCart1)
        cartRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        cartRecycler.adapter = cartAdapter
        getItemData()

        backBtn.setOnClickListener {
            onBackPressed()
        }
    }


    private fun getItemData() {
        dbref = FirebaseDatabase.getInstance().getReference("packages")
        //dbref = FirebaseDatabase.getInstance().getReference("items")
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val item = userSnapshot.getValue(MyCartItems::class.java)
                        itemArrayList.add(item!!)
                    }
                    cartRecycler.adapter = cartAdapter
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

    override fun onButtonClick(position: Int) {
        Toast.makeText(this, "Removed", Toast.LENGTH_SHORT).show()
        val clickedItem = itemArrayList[position]
        dbref = FirebaseDatabase.getInstance().getReference("packages")
        dbref.child(clickedItem.title.toString()).setValue(null)
        itemArrayList.remove(clickedItem)
        cartAdapter.notifyDataSetChanged()
        //getItemData()
        //cartRecycler.adapter = cartAdapter
//        if (itemArrayList.isNullOrEmpty()) {
//            clearButton.visibility=View.INVISIBLE
//            totalAmount.visibility=View.VISIBLE
//        }else{
//            clearButton.visibility=View.VISIBLE
//            totalAmount.visibility=View.INVISIBLE
//        }
    }

    override fun onItemClick(position: Int) {

    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(this, R.style.MyDialogTheme)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv = mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }
}

