package fr.sharescrobble.android.main.ui.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.progressindicator.LinearProgressIndicator
import fr.sharescrobble.android.MyApplication
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.ErrorUtils
import fr.sharescrobble.android.core.utils.NotificationUtils
import fr.sharescrobble.android.main.adapter.FriendsAdapter
import fr.sharescrobble.android.main.ui.MainActivity
import fr.sharescrobble.android.main.worker.GPSTimeoutWorker
import fr.sharescrobble.android.main.worker.TimeTimeoutWorker
import fr.sharescrobble.android.network.models.lastfm.UserFriendModel
import fr.sharescrobble.android.network.repositories.LastfmRepository
import fr.sharescrobble.android.network.repositories.ScrobbleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.util.concurrent.TimeUnit
import kotlin.time.Duration


class FriendsFragment : Fragment(), FriendsAdapter.ItemClickListener {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var privatePreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var layout: View

    private lateinit var loadingIndicator: LinearProgressIndicator

    private var adapter: FriendsAdapter? = null
    private lateinit var friendsSwipeContainer: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.layout = inflater.inflate(R.layout.fragment_friends, container, false)

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        this.privatePreferences =
            requireActivity().getSharedPreferences("Scrobble", Context.MODE_PRIVATE)
        this.editor = privatePreferences.edit()
        this.fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        this.loadingIndicator = this.layout.findViewById(R.id.rvFriendsLoading)

        // Set up the pull to refresh
        this.friendsSwipeContainer = this.layout.findViewById(R.id.friendsSwipeContainer)
        this.friendsSwipeContainer.setOnRefreshListener { this.getFriends(false) }

        // Set up the RecyclerView
        this.recyclerView = layout.findViewById(R.id.rvFriends)
        this.recyclerView.layoutManager =
            GridLayoutManager(
                activity,
                Constants.NB_COLUMNS_FRIENDS,
                GridLayoutManager.VERTICAL,
                false
            )

        // Load data once
        this.getFriends(true)

        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    @SuppressLint("MissingPermission")
    override fun onItemClick(view: View?, position: Int) {
        val element = this.adapter!!.getItem(position);

        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Subscribe to " + element.name + " ?")
        builder.setPositiveButton(
            R.string.subscribe
        ) { dialog, _ ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    ScrobbleRepository.apiInterface.subscribe(element.name)
                    editor.putString("sourceScrobble", element.name)
                    editor.commit()

                    withContext(Dispatchers.Main) {
                        // Set up time based timeout
                        if (sharedPreferences.getBoolean("autoTimeout", false)) {
                            val timeout = sharedPreferences.getInt("timeout", 300)

                            val workRequest =
                                OneTimeWorkRequestBuilder<TimeTimeoutWorker>().setInitialDelay(
                                    timeout.toLong(),
                                    TimeUnit.MINUTES
                                ).build()
                            WorkManager.getInstance(requireActivity()).enqueueUniqueWork(
                                "timeTimeout",
                                ExistingWorkPolicy.REPLACE,
                                workRequest
                            )
                        }

                        // Set up GPS based timeout
                        if (sharedPreferences.getBoolean("gpsTimeout", false)) {
                            // Get current location and store it
                            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                                run {
                                    Log.d(Constants.TAG, location?.latitude.toString())
                                    Log.d(Constants.TAG, location?.longitude.toString())

                                    editor.putLong("latitude", location?.latitude?.toLong() ?: 0)
                                    editor.putLong("longitude", location?.longitude?.toLong() ?: 0)
                                    editor.commit()
                                }
                            }

                            val workRequest =
                                PeriodicWorkRequestBuilder<GPSTimeoutWorker>(
                                    Constants.GPS_DELAY.toLong(),
                                    TimeUnit.MINUTES
                                ).setInitialDelay(
                                    Constants.GPS_DELAY.toLong(),
                                    TimeUnit.MINUTES
                                ).build()
                            WorkManager.getInstance(requireActivity()).enqueueUniquePeriodicWork(
                                "timeTimeout",
                                ExistingPeriodicWorkPolicy.REPLACE,
                                workRequest
                            )
                        }

                        // Show a persistent notification
                        with(NotificationManagerCompat.from(requireActivity())) {
                            notify(
                                1,
                                NotificationUtils.persistentNotificationBuilder(
                                    requireActivity(),
                                    element.name
                                )
                            )
                        }

                        // Redirect to home
                        dialog.dismiss()
                        val parent: MainActivity = activity as MainActivity
                        parent.navigateTo(R.id.action_home)
                    }

                } catch (e: HttpException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            activity,
                            ErrorUtils.parseError(e.response())?.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        builder.setNegativeButton(
            R.string.cancel
        ) { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun getFriends(progress: Boolean = true) {
        if (progress) {
            // Display loading
            this.loadingIndicator.show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = LastfmRepository.apiInterface.getFriends(
                    AuthService.currentJwtUser?.username ?: ""
                )
                withContext(Dispatchers.Main) {
                    createRecyclerView(data)
                }

            } catch (e: HttpException) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        activity,
                        ErrorUtils.parseError(e.response())?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
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