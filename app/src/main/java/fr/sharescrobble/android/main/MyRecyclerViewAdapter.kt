package fr.sharescrobble.android.main


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.sharescrobble.android.R


class MyRecyclerViewAdapter internal constructor(context: Context?, data: Array<String>) :
    RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {
    private val mData: Array<String> = data
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.friend_grid_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val picasso = Picasso.get()
        holder.myTextView.text = mData[position]
        //        picasso.load(RetrofitClient.TMDB_IMAGEURL + movie.posterPath).into(posterImageView)

        picasso.load("https://lastfm.freetls.fastly.net/i/u/avatar170s/b4975cc33e9c9709603727518fcfeffc.webp").placeholder(R.drawable.placeholder)
            .into(holder.myUserImage)
    }

    // total number of cells
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var myTextView: TextView = itemView.findViewById(R.id.friendNameTextView)
        var myUserImage: ImageView = itemView.findViewById(R.id.imageUser)
        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): String {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

}