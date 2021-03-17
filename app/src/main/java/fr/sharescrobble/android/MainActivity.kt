package fr.sharescrobble.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.auth.ui.AuthActivity
import fr.sharescrobble.android.core.Constants
import fr.sharescrobble.android.network.repositories.LastfmRepository
import fr.sharescrobble.android.network.repositories.ScrobbleRepository
import fr.sharescrobble.android.network.repositories.UsersRepository
import kotlinx.coroutines.*
import retrofit2.HttpException

class MainActivity : AppCompatActivity() {
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
    }
}