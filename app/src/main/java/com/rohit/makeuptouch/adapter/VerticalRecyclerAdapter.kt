package com.rohit.makeuptouch.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rohit.makeuptouch.R
import com.rohit.makeuptouch.data.MyEquipments
import com.rohit.makeuptouch.fragments.HomeFragment


class VerticalRecyclerAdapter(val context: HomeFragment, val equipments:ArrayList<MyEquipments>,private val listener:OnClListener):RecyclerView.Adapter<VerticalRecyclerAdapter.MyViewHolder>(){ //,private val listener: OnItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.custom_view_group,parent,false)
        return MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem=equipments[position]
        holder.title.text = currentItem.title
        holder.description.text=currentItem.description
        holder.price.text=currentItem.price.toString()
        Glide.with(context).load(currentItem.image).into(holder.image)
        var pricedown: Double = ((holder.price.text.toString().toIntOrNull())!! *0.3)+(holder.price.text.toString().toIntOrNull()!!)
        holder.priceDown.text = "₹ " + pricedown.toString()

    }

    override fun getItemCount(): Int {
        return equipments.size
    }

    inner class MyViewHolder(itemView: View):RecyclerView.ViewHolder(itemView),View.OnClickListener{  //,listener: OnItemClickListener
        var title = itemView.findViewById<TextView>(R.id.title)
        var description = itemView.findViewById<TextView>(R.id.description)
        var price :TextView=itemView.findViewById(R.id.price)
        var image = itemView.findViewById<ImageView>(R.id.itemImage)
        var priceDown = itemView.findViewById<TextView>(R.id.priceDown)
        var priceOff = itemView.findViewById<TextView>(R.id.priceoff)


        init {
            itemView.setOnClickListener(this)
        }
        override fun onClick(p0: View?) {
            val position=adapterPosition
            val recyclerView= itemView.parent
            if (position!=RecyclerView.NO_POSITION){
                listener.onItemClick(position,recyclerView)
            }
        }
    }

    interface OnClListener{
        fun onItemClick(position: Int, itemView: ViewParent)
    }
}













//        holder.itemView.setOnClickListener(object :View.OnClickListener{
//            override fun onClick(p0: View?) {
//
//                val activity=p0!!.context as AppCompatActivity
//
//                val intent = Intent (activity, ItemDetail::class.java)
//                val bundle = Bundle()
//                bundle.putString("title",currentItem.title)
//                bundle.putString("price",currentItem.price.toString())

//                intent.putExtra("name",currentItem.title)
//                intent.putExtra("price",currentItem.price)
//                intent.putExtra("detail",currentItem.description)
//                intent.putExtra("image",currentItem.image)
//                startActivity(activity, intent,bundle)

//                val activity=p0!!.context as AppCompatActivity
//                val itemDetailFrag=ItemDetailFragment()
//                activity.supportFragmentManager.beginTransaction().replace(R.id.frameLayout,itemDetailFrag).addToBackStack(null).commit()
//            }

//        })