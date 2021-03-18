package fr.sharescrobble.android


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.main.MyRecyclerViewAdapter
import fr.sharescrobble.android.network.models.lastfm.UserFriendModel
import fr.sharescrobble.android.network.repositories.LastfmRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity(), MyRecyclerViewAdapter.ItemClickListener {
    var adapter: MyRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(Constants.TAG, "Main")
        Log.d(Constants.TAG, intent.dataString.toString())

        if (!AuthService.isAuthenticated()) {
            val intent = Intent(this, AuthActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

            return
        }


        // set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvFriends)
        val numberOfColumns = 2
        recyclerView.layoutManager =
            GridLayoutManager(this, numberOfColumns, GridLayoutManager.VERTICAL, false)

        getFriends()
    }


    override fun onItemClick(view: View?, position: Int) {
        Log.i(
            Constants.TAG,
            "You clicked number " + this.adapter!!.getItem(position) + ", which is at cell position " + position
        );
    }

    private fun getFriends() {
        val loadingIndicator = findViewById<LinearProgressIndicator>(R.id.rvFriendsLoading)
        loadingIndicator.show()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = LastfmRepository.apiInterface.getFriends("SoFolichon")

                Log.d(Constants.TAG, "Got friends: ${data.size}")
                data.forEach { Log.d(Constants.TAG, "Got friends: ${it}") }

                withContext(Dispatchers.Main) {


                    createRecyclerView(data)
                }

            } catch (e: Throwable) {
            }
        }


    }

    private fun createRecyclerView(data: Array<UserFriendModel>) {
        val recyclerView = findViewById<RecyclerView>(R.id.rvFriends)

        adapter = MyRecyclerViewAdapter(this, data)
        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter
        val loadingIndicator = findViewById<LinearProgressIndicator>(R.id.rvFriendsLoading)
        loadingIndicator.hide()
    }
}
