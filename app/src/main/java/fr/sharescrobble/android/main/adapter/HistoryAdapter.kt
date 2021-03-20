package fr.sharescrobble.android.main.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import fr.sharescrobble.android.R
import fr.sharescrobble.android.core.utils.DateUtils
import fr.sharescrobble.android.network.models.users.UserScrobbleModel

class HistoryAdapter internal constructor(private var ctx: Context?, data: Array<UserScrobbleModel>) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {
    // References
    private var mData: MutableList<UserScrobbleModel> = data.toMutableList()
    private val mInflater: LayoutInflater = LayoutInflater.from(ctx)
    private var mClickListener: ItemClickListener? = null

    // inflates the cell layout from xml when needed
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.history_list_item, parent, false)
        return ViewHolder(view)
    }

    // binds the data to the TextView in each cell
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.historyTitle.text = getItem(position).lastFmData.artist.name
        holder.historySubtitle.text = ctx?.getString(
            R.string.view_historyTitle,
            getItem(position).lastFmData.album.text,
            getItem(position).lastFmData.name
        )
        holder.historyText.text = ctx?.getString(
            R.string.view_historyDate,
            DateUtils.getTimeAgo(getItem(position).createdAt.time)
        )

        val picasso = Picasso.get()
        val imgSize = getItem(position).lastFmData.image.size
        val imgLink = getItem(position).lastFmData.image[imgSize - 1].text
        if (imgLink.contentEquals("")) {
            picasso.load(R.drawable.placeholder).placeholder(R.drawable.placeholder)
                .into(holder.historyCover)
        } else {
            picasso.load(imgLink).placeholder(R.drawable.placeholder)
                .into(holder.historyCover)
        }
    }

    // total number of cells
    override fun getItemCount(): Int {
        return mData.size
    }

    // stores and recycles views as they are scrolled off screen
    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var historyTitle: TextView = itemView.findViewById(R.id.historyTitle)
        var historySubtitle: TextView = itemView.findViewById(R.id.historySubtitle)
        var historyText: TextView = itemView.findViewById(R.id.historyText)
        var historyCover: ImageView = itemView.findViewById(R.id.historyCover)

        override fun onClick(view: View?) {
            if (mClickListener != null) mClickListener!!.onItemClick(view, adapterPosition)
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    // convenience method for getting data at click position
    fun getItem(id: Int): UserScrobbleModel {
        return mData[id]
    }

    // allows clicks events to be caught
    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun addAll(data: Array<UserScrobbleModel>) {
        mData.addAll(data)
        notifyDataSetChanged()
    }

    // parent activity will implement this method to respond to click events
    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }
}