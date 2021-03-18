package fr.sharescrobble.android.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.main.adapter.FriendsAdapter
import fr.sharescrobble.android.network.models.lastfm.UserFriendModel
import fr.sharescrobble.android.network.repositories.LastfmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendsFragment : Fragment(), FriendsAdapter.ItemClickListener {
    private lateinit var layout: View

    private lateinit var loadingIndicator: LinearProgressIndicator

    private var adapter: FriendsAdapter? = null
    private lateinit var friendsSwipeContainer: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    // TODO: Pull to refresh https://guides.codepath.com/android/implementing-pull-to-refresh-guide

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.layout =  inflater.inflate(R.layout.fragment_friends, container, false)

        this.loadingIndicator = this.layout.findViewById(R.id.rvFriendsLoading)

        // Set up the pull to refresh
        this.friendsSwipeContainer = this.layout.findViewById(R.id.friendsSwipeContainer)
        this.friendsSwipeContainer.setOnRefreshListener { this.getFriends(false) }

        // Set up the RecyclerView
        this.recyclerView = layout.findViewById(R.id.rvFriends)
        this.recyclerView.layoutManager =
            GridLayoutManager(activity, Constants.NB_COLUMNS, GridLayoutManager.VERTICAL, false)

        // Load data once
        this.getFriends(true)

        return layout
    }

    override fun onItemClick(view: View?, position: Int) {
        Log.i(
            Constants.TAG,
            "You clicked " + this.adapter!!.getItem(position) + ", which is at cell position " + position
        );
    }

    private fun getFriends(progress: Boolean = true) {
        if (progress) {
            // Display loading
            this.loadingIndicator.show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = LastfmRepository.apiInterface.getFriends(AuthService.currentJwtUser?.username ?: "")
                withContext(Dispatchers.Main) {
                    createRecyclerView(data)
                }

            } catch (e: Throwable) {
                TODO("Handle API errors")
            }
        }
    }

    private fun createRecyclerView(data: Array<UserFriendModel>) {
        if (this.adapter == null) {
            this.adapter = FriendsAdapter(activity, data)
            this.adapter!!.setClickListener(this)
            this.recyclerView.adapter = adapter
        }

        this.loadingIndicator.hide()
        this.friendsSwipeContainer.isRefreshing = false

        if (this.adapter != null) {
            this.adapter!!.clear()
            this.adapter!!.addAll(data)
        }
    }
}