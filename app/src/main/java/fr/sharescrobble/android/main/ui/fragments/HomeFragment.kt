package fr.sharescrobble.android.main.ui.fragments

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.squareup.picasso.Picasso
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.core.utils.DateUtils
import fr.sharescrobble.android.core.utils.ErrorUtils
import fr.sharescrobble.android.network.models.users.UserModel
import fr.sharescrobble.android.network.models.users.UserScrobbleModel
import fr.sharescrobble.android.network.repositories.LastfmRepository
import fr.sharescrobble.android.network.repositories.ScrobbleRepository
import fr.sharescrobble.android.network.repositories.UsersRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.HttpException

class HomeFragment : Fragment() {
    private lateinit var privatePreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var layout: View

    private lateinit var loadingIndicator: LinearProgressIndicator

    private lateinit var homeSwipeContainer: SwipeRefreshLayout
    private lateinit var homeNotFound: RelativeLayout
    private lateinit var homeFound: RelativeLayout

    // Not found

    // Found
    private lateinit var cardScrobble: CardView
    private lateinit var scrobblingFromName: TextView
    private lateinit var imageUser: ImageView

    private lateinit var card: CardView
    private lateinit var homeTitle: TextView
    private lateinit var homeSubtitle: TextView
    private lateinit var homeText: TextView
    private lateinit var homeCover: ImageView

    private lateinit var homeUnsubscribe: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        this.layout = inflater.inflate(R.layout.fragment_home, container, false)

        this.privatePreferences =
            requireActivity().getSharedPreferences("Scrobble", Context.MODE_PRIVATE)
        this.editor = privatePreferences.edit()

        this.loadingIndicator = this.layout.findViewById(R.id.homeLoading)

        // Get references
        cardScrobble = layout.findViewById(R.id.cardScrobble)
        scrobblingFromName = layout.findViewById(R.id.scrobblingFromName)
        imageUser = layout.findViewById(R.id.imageUser)

        card = layout.findViewById(R.id.card)
        homeTitle = layout.findViewById(R.id.homeTitle)
        homeSubtitle = layout.findViewById(R.id.homeSubtitle)
        homeText = layout.findViewById(R.id.homeText)
        homeCover = layout.findViewById(R.id.homeCover)

        homeUnsubscribe = layout.findViewById(R.id.homeUnsubscribe)

        // Set up the pull to refresh
        this.homeSwipeContainer = this.layout.findViewById(R.id.homeSwipeContainer)
        this.homeSwipeContainer.setOnRefreshListener { this.loadSScrobbleData(false) }

        // Set up the RecyclerView
        this.homeNotFound = this.layout.findViewById(R.id.homeNotFound)
        this.homeFound = this.layout.findViewById(R.id.homeFound)

        // Set up the Button
        homeUnsubscribe.setOnClickListener {
            run {
                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        ScrobbleRepository.apiInterface.unsubscribe(scrobblingFromName.text.toString())

                        withContext(Dispatchers.Main) {
                            editor.remove("sourceScrobble")
                            editor.commit()

                            loadSScrobbleData()
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
        }

        // Listen for sharedPreferences change (Background Worker)
        // It should but it doesn't for some obscure reasons...
        // See https://stackoverflow.com/questions/2542938/sharedpreferences-onsharedpreferencechangelistener-not-being-called-consistently/3104265#3104265
        listener = OnSharedPreferenceChangeListener { sharedPreferences, key ->
            Log.d(Constants.TAG, key)
            Log.d(Constants.TAG, sharedPreferences.getString(key, "").toString())
            if (key == "sourceScrobble") loadSScrobbleData()
        }
        privatePreferences.registerOnSharedPreferenceChangeListener(listener)

        // Load init
        this.loadSScrobbleData()

        return layout
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)

        if (!hidden) {
            this.loadSScrobbleData()
        }
    }

    private fun loadSScrobbleData(progress: Boolean = true) {
        if (progress) {
            // Display loading
            this.loadingIndicator.show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = UsersRepository.apiInterface.getUser(
                    AuthService.currentJwtUser?.id?.toLong() ?: 0
                )

                val scrobblesData = UsersRepository.apiInterface.getUserScrobbles(
                    AuthService.currentJwtUser?.id?.toLong() ?: 0
                )

                withContext(Dispatchers.Main) {
                    Log.d(Constants.TAG, data.toString())

                    if (data.sourceScrobble != null) {
                        found(data, scrobblesData)
                    } else {
                        notFound()
                    }

                    loadingIndicator.hide()
                    homeSwipeContainer.isRefreshing = false
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

    private fun notFound() {
        homeFound.visibility = View.GONE
        homeNotFound.visibility = View.VISIBLE
    }

    private fun found(data: UserModel, scrobblesData: Array<UserScrobbleModel>) {
        homeFound.visibility = View.VISIBLE
        homeNotFound.visibility = View.GONE

        val picasso = Picasso.get()

        scrobblingFromName.text = data.sourceScrobble
        // Load a placeholder
        picasso.load(R.drawable.placeholder).placeholder(R.drawable.placeholder)
            .into(imageUser)

        // Load Last.fm user data
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sourceData = LastfmRepository.apiInterface.getUser(data.sourceScrobble ?: "")

                withContext(Dispatchers.Main) {
                    val imgSize = sourceData.image.size
                    val imgLink = sourceData.image[imgSize - 1].text

                    if (imgLink.contentEquals("")) {
                        picasso.load(R.drawable.placeholder).placeholder(R.drawable.placeholder)
                            .into(imageUser)
                    } else {
                        picasso.load(imgLink).placeholder(R.drawable.placeholder)
                            .into(imageUser)
                    }
                }

            } catch (e: Throwable) {
            }
        }

        if (scrobblesData.isEmpty() || scrobblesData[0].createdAt < data.startedShared) {
            card.visibility = View.INVISIBLE
        } else {
            homeTitle.text = scrobblesData[0].lastFmData.artist.name
            homeSubtitle.text =
                scrobblesData[0].lastFmData.album.text + " - " + scrobblesData[0].lastFmData.name
            homeText.text =
                "ShareScrobbled " + DateUtils.getTimeAgo(scrobblesData[0].createdAt.time)

            val imgSize = scrobblesData[0].lastFmData.image.size
            val imgLink = scrobblesData[0].lastFmData.image[imgSize - 1].text
            if (imgLink.contentEquals("")) {
                picasso.load(R.drawable.placeholder).placeholder(R.drawable.placeholder)
                    .into(homeCover)
            } else {
                picasso.load(imgLink).placeholder(R.drawable.placeholder)
                    .into(homeCover)
            }
        }
    }
}