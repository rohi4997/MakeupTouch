package com.rohit.asquare.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.rohit.asquare.CartItemDetail
import com.rohit.asquare.ItemDetail
import com.rohit.asquare.adapter.CartAdapter
import com.rohit.asquare.data.MyCartItems
import com.rohit.asquare.R

class CartFragment : Fragment(),CartAdapter.OnClListener {
    private  lateinit var dbref: DatabaseReference
    private lateinit var cartRecycler: RecyclerView
    private lateinit var itemArrayList: ArrayList<MyCartItems>
    private lateinit var auth: FirebaseAuth
    private lateinit var uid:String
    private lateinit var cartAdapter: CartAdapter
    private lateinit var totalAmount:TextView
    private lateinit var clearButton:Button
    lateinit var mProgressDialog:Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        val v = inflater.inflate(R.layout.fragment_cart, container, false)
        showProgressDialog("Loading Your Cart")
        auth = FirebaseAuth.getInstance()
        val splittedlist = auth.currentUser?.email.toString().split(".")
        uid = splittedlist[0]
        itemArrayList = arrayListOf<MyCartItems>()
        cartAdapter = CartAdapter(requireContext(), itemArrayList, this@CartFragment)

        //Recycler View1
        cartRecycler = v.findViewById(R.id.myCart)
        cartRecycler.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        cartRecycler.adapter=cartAdapter
        totalAmount = v.findViewById(R.id.totalAmount)
        clearButton = v.findViewById(R.id.place)
        getItemData()


//        if (itemArrayList.isNullOrEmpty()) {
//            clearButton.visibility=View.GONE
//            totalAmount.visibility=View.VISIBLE
//        }else{
//            clearButton.visibility=View.VISIBLE
//            totalAmount.visibility=View.GONE
//        }

        clearButton.setOnClickListener {
            dbref = FirebaseDatabase.getInstance().getReference("users")
            dbref.child(uid).child("usercart").setValue(null)
            itemArrayList.removeAll(itemArrayList)
            cartAdapter.notifyDataSetChanged()
            if (itemArrayList.isNullOrEmpty()) {
                clearButton.visibility=View.GONE
                totalAmount.visibility=View.VISIBLE
            }
            Snackbar.make(it,"Cleared All Items", Snackbar.LENGTH_LONG).show()
        }
        return v
    }



    private fun getItemData() {
        dbref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("usercart")
        //dbref = FirebaseDatabase.getInstance().getReference("items")
        dbref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val item = userSnapshot.getValue(MyCartItems::class.java)
                        itemArrayList.add(item!!)
                    }
                    cartRecycler.adapter = cartAdapter
                    if (itemArrayList.isNullOrEmpty()) {
                        clearButton.visibility=View.GONE
                        totalAmount.visibility=View.VISIBLE
                        mProgressDialog.dismiss()
                    }else{
                        clearButton.visibility=View.VISIBLE
                        totalAmount.visibility=View.GONE
                        mProgressDialog.dismiss()
                    }
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
        Toast.makeText(context,"Removed",Toast.LENGTH_SHORT).show()
        val clickedItem = itemArrayList[position]
        dbref = FirebaseDatabase.getInstance().getReference("users")
        dbref.child(uid).child("usercart").child(clickedItem.title.toString()).setValue(null)
        itemArrayList.remove(clickedItem)
        cartAdapter.notifyDataSetChanged()
        //getItemData()
        //cartRecycler.adapter = cartAdapter
        if (itemArrayList.isNullOrEmpty()) {
            clearButton.visibility=View.INVISIBLE
            totalAmount.visibility=View.VISIBLE
        }else{
            clearButton.visibility=View.VISIBLE
            totalAmount.visibility=View.INVISIBLE
        }
    }

    override fun onItemClick(position: Int) {
        val intent= Intent(context, CartItemDetail::class.java)
        val clickedItem = itemArrayList[position]
        intent.putExtra("name",clickedItem.title.toString())
        intent.putExtra("price",clickedItem.price.toString())
        intent.putExtra("detail",clickedItem.description)
        intent.putExtra("image",clickedItem.image)
        startActivity(intent)
    }

    fun showProgressDialog(text:String){
        mProgressDialog= Dialog(this.requireContext(),R.style.MyDialogTheme)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val dtv= mProgressDialog.findViewById<TextView>(R.id.textView)
        dtv.text=text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }

}