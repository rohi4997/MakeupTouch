package com.rohit.asquare.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohit.asquare.MyOrders
import com.rohit.asquare.R
import com.rohit.asquare.data.CategoryOneData

class MyOrdersAdapter(val context: Context, val equipments:ArrayList<CategoryOneData>, private val listener:OnClListener):RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.orders_history_viewgroup,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=equipments[position]
        //holder.title.text = equipments[position]
        holder.title.text = currentItem.title
        holder.description.text=currentItem.description
        holder.price.text=currentItem.price.toString()
        Glide.with(context).load(currentItem.image).override(150, 150).into(holder.image)
    }

    override fun getItemCount(): Int {
        return equipments.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{
        var title = itemView.findViewById<TextView>(R.id.title)
        var description = itemView.findViewById<TextView>(R.id.description)
        var price :TextView=itemView.findViewById(R.id.price)
        var image = itemView.findViewById<ImageView>(R.id.itemImage)



        override fun onClick(p0: View?) {
            val position=adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                //listener.onItemClick(position)
            }
        }
    }

    interface OnClListener{
        fun onItemClick(position: Int)
    }
}

