package fr.sharescrobble.android.auth.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import fr.sharescrobble.android.R
import fr.sharescrobble.android.auth.AuthService
import fr.sharescrobble.android.core.Constants

class AuthActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        val base64Token = intent.dataString

        if (base64Token == null) {
            AuthService.login()
            return
        }

        Log.d(Constants.TAG, base64Token.toString())
        AuthService.loginCallback(base64Token.toString())
    }
}