package fr.sharescrobble.android.main.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.ErrorUtils
import fr.sharescrobble.android.main.adapter.HistoryAdapter
import fr.sharescrobble.android.network.models.users.UserScrobbleModel
import fr.sharescrobble.android.network.repositories.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HistoryFragment : Fragment(), HistoryAdapter.ItemClickListener {
    private lateinit var layout: View

    private lateinit var loadingIndicator: LinearProgressIndicator

    private var adapter: HistoryAdapter? = null
    private lateinit var historySwipeContainer: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        this.layout = inflater.inflate(R.layout.fragment_history, container, false)

        this.loadingIndicator = this.layout.findViewById(R.id.rvHistoryLoading)

        // Set up the pull to refresh
        this.historySwipeContainer = this.layout.findViewById(R.id.historySwipeContainer)
        this.historySwipeContainer.setOnRefreshListener { this.getHistory(false) }

        // Set up the RecyclerView
        this.recyclerView = layout.findViewById(R.id.rvHistory)
        this.recyclerView.layoutManager =
            GridLayoutManager(activity, Constants.NB_COLUMNS_HISTORY, GridLayoutManager.VERTICAL, false)

        // Load data once
        this.getHistory(true)

        return this.layout
    }

    override fun onItemClick(view: View?, position: Int) {
        Log.i(
            Constants.TAG,
            "You clicked " + this.adapter!!.getItem(position) + ", which is at cell position " + position
        )
    }

    private fun getHistory(progress: Boolean = true) {
        if (progress) {
            // Display loading
            this.loadingIndicator.show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = UsersRepository.apiInterface.getUserScrobbles(
                    AuthService.currentJwtUser?.id?.toLong() ?: 0
                )
                withContext(Dispatchers.Main) {
                    data.forEach { i -> Log.d(Constants.TAG, i.toString()) }
                    createOrPopulateRecyclerView(data)
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(activity, ErrorUtils.parseError(e.response())?.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun createOrPopulateRecyclerView(data: Array<UserScrobbleModel>) {
        if (this.adapter == null) {
            this.adapter = HistoryAdapter(activity, data)
            this.adapter!!.setClickListener(this)
            this.recyclerView.adapter = adapter
        }

        this.loadingIndicator.hide()
        this.historySwipeContainer.isRefreshing = false

        if (this.adapter != null) {
            this.adapter!!.clear()
            this.adapter!!.addAll(data)
        }
    }
}