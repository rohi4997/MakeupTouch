package com.rohit.makeuptouch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohit.makeuptouch.R
import com.rohit.makeuptouch.data.TodayOrdersData


class TodayOrdersAdapter(val context: Context, val items : ArrayList<TodayOrdersData>, private  val listener: Context): RecyclerView.Adapter<TodayOrdersAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.orders_history_viewgroup,parent,false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder:MyViewHolder, position: Int) {
        val currentItem=items[position]
        //holder.title.text = equipments[position]
        holder.name.text = currentItem.name
        holder.deliveryAddress.text=currentItem.deliveryAddress
        holder.price.text=currentItem.price.toString()
        Glide.with(context).load(currentItem.image).override(150, 150).into(holder.image)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var name = itemView.findViewById<TextView>(R.id.title)
        var deliveryAddress = itemView.findViewById<TextView>(R.id.description)
        var price : TextView =itemView.findViewById(R.id.price)
        var image = itemView.findViewById<ImageView>(R.id.itemImage)



        override fun onClick(p0: View?) {
            val position=adapterPosition
            if (position!=RecyclerView.NO_POSITION){
                //listener.onItemClick(position)
            }
        }
    }

}