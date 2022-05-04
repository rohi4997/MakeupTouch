package com.rohit.asquare.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rohit.asquare.R


class ProfileFragment : Fragment() {
    private  lateinit var dbref: DatabaseReference
    lateinit var mProgressDialog:Dialog
    var auth = FirebaseAuth.getInstance()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v=  inflater.inflate(R.layout.fragment_profile, container, false)
        val userName= v.findViewById<TextView>(R.id.userName)
        val userImage= v.findViewById<ImageView>(R.id.userImage)
        val userEmail= v.findViewById<TextView>(R.id.userEmail)
        val phoneNumber=v.findViewById<TextView>(R.id.phoneNo)
        val editProfile=v.findViewById<Button>(R.id.editProfile)
        showProgressDialog("Please Wait")
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        userEmail.text=user?.email
        val splittedlist = user?.email.toString().split(".")
        val uid=splittedlist[0]
        Glide.with(this).load(user?.photoUrl).into(userImage)
        dbref = FirebaseDatabase.getInstance().getReference("users")
        dbref.child(uid).child("phone").get().addOnSuccessListener {
            phoneNumber.text=it.value.toString()
        }.addOnFailureListener{
            phoneNumber.text="NA"
        }
        dbref.child(uid).child("name").get().addOnSuccessListener {
            userName.text=it.value.toString()
            mProgressDialog.dismiss()
        }.addOnFailureListener{
            phoneNumber.text="NA"
        }


        editProfile.setOnClickListener {

            val builder = AlertDialog.Builder(context)
            builder
                .setMessage("Enter Your Phone and Name")
                .setCancelable(true)

            val layout = LinearLayout(context)
            layout.orientation=LinearLayout.VERTICAL
            layout.setPadding(15,0,15,0)
            val inputName = EditText(context)
            val inputPhone = EditText(context)
            inputName.hint="Name"
            inputPhone.hint="Phone"
            inputPhone.inputType= InputType.TYPE_CLASS_NUMBER
            layout.addView(inputName)
            layout.addView(inputPhone)
            builder.setView(layout)

            //performing positive action
            builder.setPositiveButton("Update") { dialogInterface, which ->
                if (inputPhone.text==null || inputPhone.text.isEmpty() || inputName.text.isEmpty() || !(inputPhone.length()==10 || inputPhone.length()==12 || inputPhone.length()==13)){
                    Toast.makeText(context, "Please Enter a valid Phone No and Name", Toast.LENGTH_LONG).show()
                }else {
                    dbref = FirebaseDatabase.getInstance().getReference("users")
                    dbref.child(uid).child("name").setValue(inputName.text.toString())
                    dbref.child(uid).child("phone").setValue(inputPhone.text.toString())
                    Toast.makeText(context,"Updated",Toast.LENGTH_SHORT).show()
                }
            }

            val alertDialog: AlertDialog = builder.create()
            alertDialog.show()
        }


        return v
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