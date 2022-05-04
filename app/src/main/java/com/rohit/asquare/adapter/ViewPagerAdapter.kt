package com.rohit.asquare.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rohit.asquare.R


class ViewPagerAdapter(private val imagesList: List<Int>) : RecyclerView.Adapter<ViewPagerAdapter.ViewPagerHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewPagerHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.viewpager_viewgroup, parent, false)
        return ViewPagerHolder(view)
    }

    override fun onBindViewHolder(holder: ViewPagerHolder, position: Int) {
        val image = imagesList[position]
        holder.bind(image)
    }

    override fun getItemCount(): Int {
        return imagesList.size
    }


    class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val ivSliderImage = itemView.findViewById<pl.droidsonroids.gif.GifImageView>(R.id.images)

        fun bind(image: Int) {
            ivSliderImage.setImageResource(image)
        }
    }
}