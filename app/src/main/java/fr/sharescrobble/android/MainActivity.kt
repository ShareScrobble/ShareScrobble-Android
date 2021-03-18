package fr.sharescrobble.android


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.main.MyRecyclerViewAdapter
import fr.sharescrobble.android.network.repositories.ApiRepository
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

//        CoroutineScope(Dispatchers.IO).launch {
//            val response = ApiRepository.apiInterface.(1)
//            withContext(Dispatchers.Main) {
//                Log.d(Constants.TAG, response.toString())
//            }
//        }

        // data to populate the RecyclerView with

        // data to populate the RecyclerView with
        val data = arrayOf(
            "1",
            "2"
        )

        // set up the RecyclerView

        // set up the RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.rvFriends)
        val numberOfColumns = 2
        recyclerView.layoutManager = GridLayoutManager(this, numberOfColumns)
        adapter = MyRecyclerViewAdapter(this, data)
        adapter!!.setClickListener(this)
        recyclerView.adapter = adapter
    }


    override fun onItemClick(view: View?, position: Int) {
        Log.i(
            "TAG",
            "You clicked number " + this.adapter!!.getItem(position) + ", which is at cell position " + position
        );
    }


}
